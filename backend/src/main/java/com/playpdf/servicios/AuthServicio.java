package com.playpdf.servicios;

import com.playpdf.dto.LoginRequest;
import com.playpdf.dto.LoginResponse;
import com.playpdf.dto.RegistroRequest;
import com.playpdf.dto.UsuarioDto;
import com.playpdf.modelos.Usuario;
import com.playpdf.repositorios.UsuarioRepositorio;
import com.playpdf.seguridad.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServicio {

	private final UsuarioRepositorio usuarioRepositorio;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	@Value("${registro.codigo-profesor}")
	private String codigoProfesor;

	@Value("${registro.codigo-admin}")
	private String codigoAdmin;

	public AuthServicio(UsuarioRepositorio usuarioRepositorio, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
		this.usuarioRepositorio = usuarioRepositorio;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
	}

	public LoginResponse iniciarSesion(LoginRequest request) {
		Usuario usuario = usuarioRepositorio.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("Credenciales incorrectas"));

		if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
			throw new RuntimeException("Credenciales incorrectas");
		}

		String token = jwtUtil.generarToken(usuario.getEmail(), usuario.getRol().name());
		return new LoginResponse(token, UsuarioDto.desde(usuario));
	}

	public LoginResponse registrarUsuario(RegistroRequest request) {
		if (usuarioRepositorio.existsByEmail(request.getEmail())) {
			throw new RuntimeException("El email ya está registrado");
		}

		// Convertir siempre a mayúsculas para que coincida con el enum
		String rol = request.getRol() != null ? request.getRol().toUpperCase() : "ALUMNO";

		if ("PROFESOR".equals(rol)) {
			if (!codigoProfesor.equals(request.getCodigo_acceso())) {
				throw new RuntimeException("Código de acceso incorrecto para profesor");
			}
		} else if ("ADMIN".equals(rol)) {
			if (!codigoAdmin.equals(request.getCodigo_acceso())) {
				throw new RuntimeException("Código de acceso incorrecto para administrador");
			}
		}

		Usuario usuario = new Usuario();
		usuario.setNombre(request.getNombre());
		usuario.setApellidos(request.getApellidos());
		usuario.setEmail(request.getEmail());
		usuario.setContrasena(passwordEncoder.encode(request.getContrasena()));
		usuario.setRol(Usuario.Rol.valueOf(rol)); // rol ya está en mayúsculas
		usuario.setEdad(request.getEdad());
		usuario.setEstudios(request.getEstudios());

		usuarioRepositorio.save(usuario);

		String token = jwtUtil.generarToken(usuario.getEmail(), usuario.getRol().name());
		return new LoginResponse(token, UsuarioDto.desde(usuario));
	}
}