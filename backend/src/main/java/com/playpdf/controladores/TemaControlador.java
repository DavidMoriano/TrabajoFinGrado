package com.playpdf.controladores;

import com.playpdf.dto.TemaDto;
import com.playpdf.servicios.TemaServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/temas")
public class TemaControlador {

	private final TemaServicio temaServicio;

	public TemaControlador(TemaServicio temaServicio) {
		this.temaServicio = temaServicio;
	}

	// GET /api/temas/asignatura/{idAsignatura}
	@GetMapping("/asignatura/{idAsignatura}")
	public ResponseEntity<List<TemaDto>> obtenerPorAsignatura(@PathVariable Long idAsignatura) {
		return ResponseEntity.ok(temaServicio.obtenerPorAsignatura(idAsignatura));
	}

	// GET /api/temas/{id}
	@GetMapping("/{id}")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(temaServicio.obtenerPorId(id));
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

	// POST /api/temas (multipart/form-data)
	@PostMapping(consumes = "multipart/form-data")
	public ResponseEntity<?> crear(@RequestParam("titulo") String titulo,
			@RequestParam(value = "descripcion", required = false) String descripcion,
			@RequestParam("id_asignatura") Long idAsignatura, @RequestParam("archivo_pdf") MultipartFile archivoPdf) {
		try {
			TemaDto tema = temaServicio.crear(titulo, descripcion, idAsignatura, archivoPdf);
			return ResponseEntity.ok(tema);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		}
	}

	// PUT /api/temas/{id} (multipart/form-data)
	@PutMapping(value = "/{id}", consumes = "multipart/form-data")
	public ResponseEntity<?> actualizar(@PathVariable Long id,
			@RequestParam(value = "titulo", required = false) String titulo,
			@RequestParam(value = "descripcion", required = false) String descripcion,
			@RequestParam(value = "archivo_pdf", required = false) MultipartFile archivoPdf) {
		try {
			TemaDto tema = temaServicio.actualizar(id, titulo, descripcion, archivoPdf);
			return ResponseEntity.ok(tema);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		}
	}

	// DELETE /api/temas/{id}
	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Long id) {
		try {
			temaServicio.eliminar(id);
			return ResponseEntity.ok(Map.of("message", "Tema eliminado"));
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}
}
