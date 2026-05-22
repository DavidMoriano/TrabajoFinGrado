package com.playpdf.controladores;

import com.playpdf.servicios.EstadisticaServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticaControlador {

	private final EstadisticaServicio estadisticaServicio;

	public EstadisticaControlador(EstadisticaServicio estadisticaServicio) {
		this.estadisticaServicio = estadisticaServicio;
	}

	@GetMapping("/me")
	public ResponseEntity<?> obtenerMisEstadisticas() {
		try {
			return ResponseEntity.ok(estadisticaServicio.obtenerMisEstadisticas());
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		}
	}

	@PostMapping
	public ResponseEntity<?> registrarPartida(@RequestBody Map<String, Object> datos) {
		try {
			Long idAsignatura = Long.valueOf(datos.get("id_asignatura").toString());
			int aciertos = datos.containsKey("aciertos") ? Integer.parseInt(datos.get("aciertos").toString()) : 0;
			int totalPreguntas = datos.containsKey("total_preguntas")
					? Integer.parseInt(datos.get("total_preguntas").toString())
					: 0;
			estadisticaServicio.registrarPartida(idAsignatura, aciertos, totalPreguntas);
			return ResponseEntity.ok(Map.of("message", "Partida registrada"));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		}
	}

	@GetMapping("/globales")
	public ResponseEntity<?> obtenerEstadisticasGlobales() {
		return ResponseEntity.ok(estadisticaServicio.obtenerEstadisticasGlobales());
	}
}
