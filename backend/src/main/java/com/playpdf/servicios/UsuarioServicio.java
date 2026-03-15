package com.playpdf.servicios;

import com.playpdf.dto.UsuarioDto;
import com.playpdf.modelos.Usuario;
import com.playpdf.repositorios.UsuarioRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioServicio {

	private final UsuarioRepositorio usuarioRepositorio;

	public UsuarioServicio(UsuarioRepositorio usuarioRepositorio) {
		this.usuarioRepositorio = usuarioRepositorio;
	}

	public List<UsuarioDto> obtenerProfesores() {
		return usuarioRepositorio.findByRol(Usuario.Rol.PROFESOR).stream().map(UsuarioDto::desde)
				.collect(Collectors.toList());
	}

	public List<UsuarioDto> obtenerAlumnos() {
		return usuarioRepositorio.findByRol(Usuario.Rol.ALUMNO).stream().map(UsuarioDto::desde)
				.collect(Collectors.toList());
	}

	public UsuarioDto obtenerPorId(Long id) {
		Usuario u = usuarioRepositorio.findById(id)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));
		return UsuarioDto.desde(u);
	}
}
