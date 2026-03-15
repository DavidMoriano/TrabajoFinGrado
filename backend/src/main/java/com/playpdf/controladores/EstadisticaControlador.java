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

	// GET /api/estadisticas/me
	@GetMapping("/me")
	public ResponseEntity<?> obtenerMisEstadisticas() {
		try {
			return ResponseEntity.ok(estadisticaServicio.obtenerMisEstadisticas());
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		}
	}

	// POST /api/estadisticas
	@PostMapping
	public ResponseEntity<?> registrarAcceso(@RequestBody Map<String, Object> datos) {
		try {
			Long idAsignatura = Long.valueOf(datos.get("id_asignatura").toString());
			estadisticaServicio.registrarAcceso(idAsignatura);
			return ResponseEntity.ok(Map.of("message", "Acceso registrado"));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		}
	}

	// GET /api/estadisticas/globales (solo administrador - controlado en
	// SecurityConfig)
	@GetMapping("/globales")
	public ResponseEntity<?> obtenerEstadisticasGlobales() {
		return ResponseEntity.ok(estadisticaServicio.obtenerEstadisticasGlobales());
	}
}
