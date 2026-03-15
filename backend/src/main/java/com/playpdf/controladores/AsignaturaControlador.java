package com.playpdf.controladores;

import com.playpdf.dto.AsignaturaDto;
import com.playpdf.request.AsignaturaRequest;
import com.playpdf.servicios.AsignaturaServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/asignaturas")
public class AsignaturaControlador {

	private final AsignaturaServicio asignaturaServicio;

	public AsignaturaControlador(AsignaturaServicio asignaturaServicio) {
		this.asignaturaServicio = asignaturaServicio;
	}

	@GetMapping
	public ResponseEntity<List<AsignaturaDto>> obtenerTodas() {
		return ResponseEntity.ok(asignaturaServicio.obtenerTodas());
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(asignaturaServicio.obtenerPorId(id));
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/profesor/{idProfesor}")
	public ResponseEntity<List<AsignaturaDto>> obtenerPorProfesor(@PathVariable Long idProfesor) {
		return ResponseEntity.ok(asignaturaServicio.obtenerPorProfesor(idProfesor));
	}

	@PostMapping
	public ResponseEntity<?> crear(@RequestBody AsignaturaRequest datos) {
		try {
			return ResponseEntity.ok(asignaturaServicio.crear(datos));
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody AsignaturaRequest datos) {
		try {
			return ResponseEntity.ok(asignaturaServicio.actualizar(id, datos));
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Long id) {
		try {
			asignaturaServicio.eliminar(id);
			return ResponseEntity.ok(Map.of("message", "Asignatura eliminada"));
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}
}