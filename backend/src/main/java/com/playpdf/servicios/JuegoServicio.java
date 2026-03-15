package com.playpdf.servicios;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.playpdf.modelos.Pregunta;
import com.playpdf.modelos.Respuesta;
import com.playpdf.modelos.Tema;
import com.playpdf.repositorios.PreguntaRepositorio;
import com.playpdf.repositorios.TemaRepositorio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class JuegoServicio {

	private final TemaRepositorio temaRepositorio;
	private final PreguntaRepositorio preguntaRepositorio;
	private final TemaServicio temaServicio;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Value("${ia.api-key}")
	private String apiKey;

	@Value("${ia.modelo}")
	private String modelo;

	@Value("${ia.url}")
	private String iaUrl;

	public JuegoServicio(TemaRepositorio temaRepositorio, PreguntaRepositorio preguntaRepositorio,
			TemaServicio temaServicio) {
		this.temaRepositorio = temaRepositorio;
		this.preguntaRepositorio = preguntaRepositorio;
		this.temaServicio = temaServicio;
	}

	public List<Map<String, Object>> obtenerTiposJuego() {
		return List.of(
				Map.of("id", "quiz", "nombre", "Quiz tipo test", "icono", "❓", "descripcion",
						"Preguntas de opción múltiple generadas por IA"),
				Map.of("id", "puzzle", "nombre", "Puzzle de palabras", "icono", "🧩", "descripcion",
						"Completa las palabras clave del tema"));
	}

	public List<Map<String, Object>> obtenerPreguntas(Long idTema, String tipoJuego) {
		Pregunta.TipoPregunta tipo = Pregunta.TipoPregunta.valueOf(tipoJuego);
		List<Pregunta> preguntas = preguntaRepositorio.findByTemaIdTemaAndTipo(idTema, tipo);

		List<Map<String, Object>> resultado = new ArrayList<>();
		for (Pregunta p : preguntas) {
			List<Map<String, Object>> respuestas = new ArrayList<>();
			for (Respuesta r : p.getRespuestas()) {
				respuestas.add(Map.of("id_respuesta", r.getIdRespuesta(), "texto", r.getTexto(), "esCorrecta",
						r.getEsCorrecta()));
			}
			resultado.add(Map.of("id_pregunta", p.getIdPregunta(), "enunciado", p.getEnunciado(), "tipo",
					p.getTipo().name(), "respuestas", respuestas));
		}
		return resultado;
	}

	@Transactional
	public void generarPreguntasConIA(Long idTema) throws Exception {
		Tema tema = temaRepositorio.findById(idTema)
				.orElseThrow(() -> new RuntimeException("Tema no encontrado: " + idTema));

		// Si ya hay preguntas, no regenerar
		Optional<Pregunta> existentes = preguntaRepositorio.findById(idTema);
		if (!existentes.isEmpty())
			return;

		// Leer el texto del PDF
		String textoPdf = leerTextoPdf(idTema);

		// Llamar a la API de IA
		String jsonPreguntas = llamarApiIA(textoPdf, tema.getTitulo());

		// Parsear y guardar las preguntas en BD
		guardarPreguntasDesdeJson(jsonPreguntas, tema);
	}

	private String leerTextoPdf(Long idTema) {
		try {
			Path ruta = temaServicio.obtenerRutaPdf(idTema);
			// Lectura simple del archivo como bytes - en producción usar Apache PDFBox
			// para extraer el texto real del PDF
			byte[] bytes = Files.readAllBytes(ruta);
			// Intentar extraer texto plano (funciona con PDFs no escaneados)
			String contenido = new String(bytes, "ISO-8859-1");
			// Limpiar caracteres no imprimibles
			contenido = contenido.replaceAll("[^\\x20-\\x7E\\n]", " ").trim();
			// Limitar a 3000 caracteres para no superar el límite del API
			return contenido.length() > 3000 ? contenido.substring(0, 3000) : contenido;
		} catch (Exception e) {
			return "Contenido del tema: " + idTema;
		}
	}

	private String llamarApiIA(String textoPdf, String tituloTema) throws Exception {
		String prompt = """
				Eres un generador de preguntas educativas.
				Basándote en el siguiente texto del tema "%s", genera exactamente 5 preguntas de tipo quiz.
				Responde ÚNICAMENTE con un JSON válido con este formato exacto, sin texto adicional:
				[
				  {
				    "enunciado": "¿Pregunta?",
				    "respuestas": [
				      {"texto": "Opción A", "esCorrecta": true},
				      {"texto": "Opción B", "esCorrecta": false},
				      {"texto": "Opción C", "esCorrecta": false},
				      {"texto": "Opción D", "esCorrecta": false}
				    ]
				  }
				]

				Texto del tema:
				%s
				""".formatted(tituloTema, textoPdf);

		String cuerpo = objectMapper.writeValueAsString(Map.of("model", modelo, "max_tokens", 2000, "messages",
				List.of(Map.of("role", "user", "content", prompt))));

		HttpClient cliente = HttpClient.newHttpClient();
		HttpRequest peticion = HttpRequest.newBuilder().uri(URI.create(iaUrl))
				.header("Content-Type", "application/json").header("Authorization", "Bearer " + apiKey)
				.POST(HttpRequest.BodyPublishers.ofString(cuerpo)).build();

		HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());

		JsonNode raiz = objectMapper.readTree(respuesta.body());
		return raiz.path("choices").get(0).path("message").path("content").asText();
	}

	private void guardarPreguntasDesdeJson(String json, Tema tema) throws Exception {
		// Limpiar posibles ```json ``` del modelo
		json = json.replaceAll("```json", "").replaceAll("```", "").trim();

		JsonNode array = objectMapper.readTree(json);
		for (JsonNode nodo : array) {
			Pregunta pregunta = new Pregunta();
			pregunta.setEnunciado(nodo.get("enunciado").asText());
			pregunta.setTipo(Pregunta.TipoPregunta.quiz);
			pregunta.setTema(tema);
			preguntaRepositorio.save(pregunta);

			for (JsonNode r : nodo.get("respuestas")) {
				Respuesta respuesta = new Respuesta();
				respuesta.setTexto(r.get("texto").asText());
				respuesta.setEsCorrecta(r.get("esCorrecta").asBoolean());
				respuesta.setPregunta(pregunta);
				// Guardar con cascada al guardar la pregunta de nuevo
				pregunta.getRespuestas().add(respuesta);
			}
			preguntaRepositorio.save(pregunta);
		}
	}
}
