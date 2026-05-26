package com.playpdf.servicios;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.playpdf.modelos.Pregunta;
import com.playpdf.modelos.Respuesta;
import com.playpdf.modelos.Tema;
import com.playpdf.repositorios.PreguntaRepositorio;
import com.playpdf.repositorios.RespuestaRepositorio;
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

@Service
public class JuegoServicio {

	private final TemaRepositorio temaRepositorio;
	private final PreguntaRepositorio preguntaRepositorio;
	private final RespuestaRepositorio respuestaRepositorio;
	private final TemaServicio temaServicio;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Value("${ia.api-key}")
	private String apiKey;

	@Value("${ia.modelo}")
	private String modelo;

	@Value("${ia.url}")
	private String iaUrl;

	public JuegoServicio(TemaRepositorio temaRepositorio, PreguntaRepositorio preguntaRepositorio,
			RespuestaRepositorio respuestaRepositorio, TemaServicio temaServicio) {
		this.temaRepositorio = temaRepositorio;
		this.preguntaRepositorio = preguntaRepositorio;
		this.respuestaRepositorio = respuestaRepositorio;
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
				respuestas.add(Map.of(
						"id_respuesta", r.getIdRespuesta(),
						"texto", r.getTexto(),
						"esCorrecta", r.getEsCorrecta()));
			}
			resultado.add(Map.of(
					"id_pregunta", p.getIdPregunta(),
					"enunciado", p.getEnunciado(),
					"tipo", p.getTipo().name(),
					"respuestas", respuestas));
		}
		return resultado;
	}

	@Transactional
	public void borrarPreguntas(Long idTema) {
		// Hay que borrar las respuestas primero para no violar la FK
		respuestaRepositorio.deleteByTemaIdTema(idTema);
		preguntaRepositorio.deleteByIdTema(idTema);
	}

	@Transactional
	public void generarPreguntasConIA(Long idTema) throws Exception {
		Tema tema = temaRepositorio.findById(idTema)
				.orElseThrow(() -> new RuntimeException("Tema no encontrado: " + idTema));

		String textoPdf = leerTextoPdf(idTema);

		// Generar preguntas de quiz si no existen
		List<Pregunta> quizExistentes = preguntaRepositorio.findByTemaIdTemaAndTipo(idTema, Pregunta.TipoPregunta.quiz);
		if (quizExistentes.isEmpty()) {
			String jsonQuiz = llamarApiIA(textoPdf, tema.getTitulo(), "quiz");
			guardarPreguntasDesdeJson(jsonQuiz, tema, Pregunta.TipoPregunta.quiz);
		}

		// Generar preguntas de puzzle si no existen
		List<Pregunta> puzzleExistentes = preguntaRepositorio.findByTemaIdTemaAndTipo(idTema, Pregunta.TipoPregunta.puzzle);
		if (puzzleExistentes.isEmpty()) {
			String jsonPuzzle = llamarApiIA(textoPdf, tema.getTitulo(), "puzzle");
			guardarPreguntasDesdeJson(jsonPuzzle, tema, Pregunta.TipoPregunta.puzzle);
		}
	}

	private String leerTextoPdf(Long idTema) {
		try {
			Path ruta = temaServicio.obtenerRutaPdf(idTema);
			try (org.apache.pdfbox.pdmodel.PDDocument documento =
					org.apache.pdfbox.Loader.loadPDF(ruta.toFile())) {
				org.apache.pdfbox.text.PDFTextStripper extractor = new org.apache.pdfbox.text.PDFTextStripper();
				String texto = extractor.getText(documento);
				texto = texto.replaceAll("\\s+", " ").trim();
				return texto.length() > 4000 ? texto.substring(0, 4000) : texto;
			}
		} catch (Exception e) {
			try {
				Path ruta = temaServicio.obtenerRutaPdf(idTema);
				byte[] bytes = Files.readAllBytes(ruta);
				String contenido = new String(bytes, java.nio.charset.StandardCharsets.ISO_8859_1);
				String resultado = contenido.replaceAll("[^\\x20-\\x7E\\n]", " ").replaceAll("\\s+", " ").trim();
				return resultado.length() > 4000 ? resultado.substring(0, 4000) : resultado;
			} catch (Exception ex) {
				return "Tema educativo general.";
			}
		}
	}

	private String llamarApiIA(String textoPdf, String tituloTema, String tipo) throws Exception {
		String prompt;

		if ("quiz".equals(tipo)) {
			prompt = """
					Eres un generador de preguntas educativas.
					Basándote en el siguiente texto del tema "%s", genera exactamente 5 preguntas de tipo quiz.
					Responde ÚNICAMENTE con un array JSON, sin texto adicional, sin markdown, sin bloques de código.
					Formato exacto:
					[{"enunciado":"¿Pregunta?","respuestas":[{"texto":"Opción A","esCorrecta":true},{"texto":"Opción B","esCorrecta":false},{"texto":"Opción C","esCorrecta":false},{"texto":"Opción D","esCorrecta":false}]}]
					
					Texto del tema:
					%s
					""".formatted(tituloTema, textoPdf);
		} else {
			// puzzle: frase con una palabra clave oculta como _____
			prompt = """
					Eres un generador de ejercicios educativos de completar frases.
					Basándote en el siguiente texto del tema "%s", genera exactamente 5 frases incompletas.
					En cada frase, oculta una palabra clave importante con _____.
					El campo "respuesta" debe contener ÚNICAMENTE la palabra que falta (en minúsculas).
					Responde ÚNICAMENTE con un array JSON, sin texto adicional, sin markdown, sin bloques de código.
					Formato exacto:
					[{"frase":"La _____ es el proceso por el que las plantas producen energía.","respuesta":"fotosíntesis"}]
					
					Texto del tema:
					%s
					""".formatted(tituloTema, textoPdf);
		}

		String cuerpo = objectMapper.writeValueAsString(Map.of(
				"model", modelo,
				"max_tokens", 2000,
				"temperature", 0.7,
				"messages", List.of(
						Map.of("role", "system", "content",
								"Eres un asistente educativo. Responde SIEMPRE con JSON puro, sin markdown ni texto adicional."),
						Map.of("role", "user", "content", prompt))));

		HttpClient cliente = HttpClient.newHttpClient();
		HttpRequest peticion = HttpRequest.newBuilder()
				.uri(URI.create(iaUrl))
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + apiKey)
				.POST(HttpRequest.BodyPublishers.ofString(cuerpo))
				.build();

		HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());

		if (respuesta.statusCode() != 200) {
			throw new RuntimeException("Error en la API de IA. Código: " + respuesta.statusCode()
					+ ". Respuesta: " + respuesta.body());
		}

		JsonNode raiz = objectMapper.readTree(respuesta.body());
		JsonNode choices = raiz.path("choices");
		if (choices.isEmpty() || !choices.isArray()) {
			throw new RuntimeException("Respuesta inesperada de la API de IA: " + respuesta.body());
		}
		return choices.get(0).path("message").path("content").asText();
	}

	private void guardarPreguntasDesdeJson(String json, Tema tema, Pregunta.TipoPregunta tipo) throws Exception {
		json = json.replaceAll("(?i)```json", "").replaceAll("```", "").trim();
		int inicio = json.indexOf('[');
		int fin = json.lastIndexOf(']');
		if (inicio >= 0 && fin > inicio) {
			json = json.substring(inicio, fin + 1);
		}

		JsonNode array = objectMapper.readTree(json);
		if (!array.isArray() || array.isEmpty()) {
			throw new RuntimeException("La IA no devolvió un array válido para tipo: " + tipo);
		}

		for (JsonNode nodo : array) {
			Pregunta pregunta = new Pregunta();
			pregunta.setTipo(tipo);
			pregunta.setTema(tema);

			if (tipo == Pregunta.TipoPregunta.quiz) {
				pregunta.setEnunciado(nodo.get("enunciado").asText());
				preguntaRepositorio.save(pregunta);

				for (JsonNode r : nodo.get("respuestas")) {
					Respuesta respuesta = new Respuesta();
					respuesta.setTexto(r.get("texto").asText());
					respuesta.setEsCorrecta(r.get("esCorrecta").asBoolean());
					respuesta.setPregunta(pregunta);
					pregunta.getRespuestas().add(respuesta);
				}

			} else {
				// puzzle: frase con _____ como enunciado, respuesta correcta = la palabra
				pregunta.setEnunciado(nodo.get("frase").asText());
				preguntaRepositorio.save(pregunta);

				// Una única respuesta marcada como correcta
				Respuesta respuesta = new Respuesta();
				respuesta.setTexto(nodo.get("respuesta").asText().toLowerCase().trim());
				respuesta.setEsCorrecta(true);
				respuesta.setPregunta(pregunta);
				pregunta.getRespuestas().add(respuesta);
			}

			preguntaRepositorio.save(pregunta);
		}
	}
}
