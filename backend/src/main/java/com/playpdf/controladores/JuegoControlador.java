package com.playpdf.controladores;

import com.playpdf.servicios.JuegoServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/juegos")
public class JuegoControlador {

	private final JuegoServicio juegoServicio;

	public JuegoControlador(JuegoServicio juegoServicio) {
		this.juegoServicio = juegoServicio;
	}

	// GET /api/juegos
	@GetMapping
	public ResponseEntity<List<Map<String, Object>>> obtenerTiposJuego() {
		return ResponseEntity.ok(juegoServicio.obtenerTiposJuego());
	}

	// GET /api/juegos/{idJuego}/preguntas?tema={idTema}
	@GetMapping("/{idJuego}/preguntas")
	public ResponseEntity<?> obtenerPreguntas(@PathVariable String idJuego, @RequestParam("tema") Long idTema) {
		try {
			return ResponseEntity.ok(juegoServicio.obtenerPreguntas(idTema, idJuego));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		}
	}

	// POST /api/juegos/generar-preguntas
	@PostMapping("/generar-preguntas")
	public ResponseEntity<?> generarPreguntas(@RequestBody Map<String, Object> datos) {
		try {
			Long idTema = Long.valueOf(datos.get("id_tema").toString());
			juegoServicio.generarPreguntasConIA(idTema);
			return ResponseEntity.ok(Map.of("message", "Preguntas generadas correctamente"));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		}
	}
}
