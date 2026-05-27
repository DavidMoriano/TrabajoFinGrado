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

	@GetMapping
	public ResponseEntity<List<Centro>> obtenerTodos() {
		return ResponseEntity.ok(centroServicio.obtenerTodos());
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(centroServicio.obtenerPorId(id));
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/codigo/{codigo}")
	public ResponseEntity<?> obtenerPorCodigo(@PathVariable String codigo) {
		try {
			return ResponseEntity.ok(centroServicio.obtenerPorCodigo(codigo));
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
		}
	}

	@PostMapping
	public ResponseEntity<?> crear(@RequestBody Centro centro) {
		try {
			if (centro.getCodigoAcceso() != null) {
				centro.setCodigoAcceso(centro.getCodigoAcceso().toUpperCase());
			}
			return ResponseEntity.ok(centroServicio.crear(centro));
		} catch (org.springframework.dao.DataIntegrityViolationException e) {
			return ResponseEntity.badRequest().body(java.util.Map.of("message", "El código de acceso ya está en uso. Elige otro código."));
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Long id) {
		try {
			centroServicio.eliminar(id);
			return ResponseEntity.ok(java.util.Map.of("message", "Centro eliminado"));
		} catch (org.springframework.dao.DataIntegrityViolationException e) {
			return ResponseEntity.badRequest().body(java.util.Map.of("message", "No se puede eliminar el centro porque tiene asignaturas asociadas. Elimina primero todas sus asignaturas."));
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
		}
	}
}
