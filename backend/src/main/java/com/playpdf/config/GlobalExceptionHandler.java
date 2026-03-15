package com.playpdf.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<?> manejarRuntimeException(RuntimeException e) {
		return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> manejarException(Exception e) {
		return ResponseEntity.internalServerError()
				.body(Map.of("message", "Error interno del servidor: " + e.getMessage()));
	}
}
