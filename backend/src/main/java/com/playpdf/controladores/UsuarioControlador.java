package com.playpdf.controladores;

import com.playpdf.dto.UsuarioDto;
import com.playpdf.servicios.UsuarioServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioControlador {

	private final UsuarioServicio usuarioServicio;

	public UsuarioControlador(UsuarioServicio usuarioServicio) {
		this.usuarioServicio = usuarioServicio;
	}

	// GET /api/usuarios/profesores
	@GetMapping("/profesores")
	public ResponseEntity<List<UsuarioDto>> obtenerProfesores() {
		return ResponseEntity.ok(usuarioServicio.obtenerProfesores());
	}

	// GET /api/usuarios/alumnos
	@GetMapping("/alumnos")
	public ResponseEntity<List<UsuarioDto>> obtenerAlumnos() {
		return ResponseEntity.ok(usuarioServicio.obtenerAlumnos());
	}

	// GET /api/usuarios/{id}
	@GetMapping("/{id}")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(usuarioServicio.obtenerPorId(id));
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}
}
