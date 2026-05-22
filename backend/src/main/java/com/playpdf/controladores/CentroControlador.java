package com.playpdf.controladores;

import com.playpdf.modelos.Centro;
import com.playpdf.servicios.CentroServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/centros")
public class CentroControlador {

	private final CentroServicio centroServicio;

	public CentroControlador(CentroServicio centroServicio) {
		this.centroServicio = centroServicio;
	}

	// GET /api/centros
	@GetMapping
	public ResponseEntity<List<Centro>> obtenerTodos() {
		return ResponseEntity.ok(centroServicio.obtenerTodos());
	}

	// GET /api/centros/{id}
	@GetMapping("/{id}")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(centroServicio.obtenerPorId(id));
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

	// GET /api/centros/codigo/{codigo}
	@GetMapping("/codigo/{codigo}")
	public ResponseEntity<?> obtenerPorCodigo(@PathVariable String codigo) {
		try {
			return ResponseEntity.ok(centroServicio.obtenerPorCodigo(codigo));
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
		}
	}

	// POST /api/centros (solo administrador)
	@PostMapping
	public ResponseEntity<?> crear(@RequestBody Centro centro) {
		try {
			// Normalizar código a mayúsculas
			if (centro.getCodigoAcceso() != null) {
				centro.setCodigoAcceso(centro.getCodigoAcceso().toUpperCase());
			}
			return ResponseEntity.ok(centroServicio.crear(centro));
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
		}
	}

	// DELETE /api/centros/{id} (solo administrador)
	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Long id) {
		try {
			centroServicio.eliminar(id);
			return ResponseEntity.ok(java.util.Map.of("message", "Centro eliminado"));
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}
}
