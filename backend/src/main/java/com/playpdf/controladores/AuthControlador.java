package com.playpdf.controladores;

import com.playpdf.dto.LoginRequest;
import com.playpdf.dto.LoginResponse;
import com.playpdf.dto.RegistroRequest;
import com.playpdf.servicios.AuthServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthControlador {

	private final AuthServicio authServicio;

	public AuthControlador(AuthServicio authServicio) {
		this.authServicio = authServicio;
	}

	// POST /api/auth/login
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request) {
		try {
			LoginResponse respuesta = authServicio.iniciarSesion(request);
			return ResponseEntity.ok(respuesta);
		} catch (RuntimeException e) {
			return ResponseEntity.status(401).body(java.util.Map.of("message", e.getMessage()));
		}
	}

	// POST /api/auth/register
	@PostMapping("/register")
	public ResponseEntity<?> registro(@RequestBody RegistroRequest request) {
		try {
			LoginResponse respuesta = authServicio.registrarUsuario(request);
			return ResponseEntity.ok(respuesta);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
		}
	}
}
