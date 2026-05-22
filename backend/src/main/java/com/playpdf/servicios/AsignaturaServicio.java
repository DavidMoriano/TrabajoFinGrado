package com.playpdf.servicios;

import com.playpdf.dto.AsignaturaDto;
import com.playpdf.modelos.Asignatura;
import com.playpdf.modelos.Centro;
import com.playpdf.modelos.Usuario;
import com.playpdf.repositorios.AsignaturaRepositorio;
import com.playpdf.repositorios.CentroRepositorio;
import com.playpdf.repositorios.TemaRepositorio;
import com.playpdf.repositorios.UsuarioRepositorio;
import com.playpdf.request.AsignaturaRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AsignaturaServicio {

	private final AsignaturaRepositorio asignaturaRepositorio;
	private final UsuarioRepositorio usuarioRepositorio;
	private final CentroRepositorio centroRepositorio;
	private final TemaRepositorio temaRepositorio;

	public AsignaturaServicio(AsignaturaRepositorio asignaturaRepositorio, UsuarioRepositorio usuarioRepositorio,
			CentroRepositorio centroRepositorio, TemaRepositorio temaRepositorio) {
		this.asignaturaRepositorio = asignaturaRepositorio;
		this.usuarioRepositorio = usuarioRepositorio;
		this.centroRepositorio = centroRepositorio;
		this.temaRepositorio = temaRepositorio;
	}

	public List<AsignaturaDto> obtenerTodas() {
		return asignaturaRepositorio.findAllConDetalle().stream().map(a -> {
			AsignaturaDto dto = AsignaturaDto.desde(a);
			dto.setCantidadTemas(temaRepositorio.findByAsignaturaIdAsignatura(a.getIdAsignatura()).size());
			return dto;
		}).collect(Collectors.toList());
	}

	public AsignaturaDto obtenerPorId(Long id) {
		Asignatura a = asignaturaRepositorio.findById(id)
				.orElseThrow(() -> new RuntimeException("Asignatura no encontrada: " + id));
		AsignaturaDto dto = AsignaturaDto.desde(a);
		dto.setCantidadTemas(temaRepositorio.findByAsignaturaIdAsignatura(id).size());
		return dto;
	}

	public List<AsignaturaDto> obtenerPorCentro(Long idCentro) {
		return asignaturaRepositorio.findByCentroIdCentro(idCentro).stream().map(a -> {
			AsignaturaDto dto = AsignaturaDto.desde(a);
			dto.setCantidadTemas(temaRepositorio.findByAsignaturaIdAsignatura(a.getIdAsignatura()).size());
			return dto;
		}).collect(Collectors.toList());
	}

	public List<AsignaturaDto> obtenerPorProfesor(Long idProfesor) {
		return asignaturaRepositorio.findByProfesorIdUsuario(idProfesor).stream().map(a -> {
			AsignaturaDto dto = AsignaturaDto.desde(a);
			dto.setCantidadTemas(temaRepositorio.findByAsignaturaIdAsignatura(a.getIdAsignatura()).size());
			return dto;
		}).collect(Collectors.toList());
	}

	public AsignaturaDto crear(AsignaturaRequest datos) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		Usuario profesor = usuarioRepositorio.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		if (datos.getIdCentro() == null) {
			throw new RuntimeException("Debes seleccionar un centro para crear la asignatura");
		}
		Centro centro = centroRepositorio.findById(datos.getIdCentro())
				.orElseThrow(() -> new RuntimeException("Centro no encontrado"));

		Asignatura asignatura = new Asignatura();
		asignatura.setNombre(datos.getNombre());
		asignatura.setDescripcion(datos.getDescripcion());
		asignatura.setProfesor(profesor);
		asignatura.setCentro(centro);

		return AsignaturaDto.desde(asignaturaRepositorio.save(asignatura));
	}

	public AsignaturaDto actualizar(Long id, AsignaturaRequest datos) {
		Asignatura asignatura = asignaturaRepositorio.findById(id)
				.orElseThrow(() -> new RuntimeException("Asignatura no encontrada: " + id));

		if (datos.getNombre() != null)
			asignatura.setNombre(datos.getNombre());
		if (datos.getDescripcion() != null)
			asignatura.setDescripcion(datos.getDescripcion());

		return AsignaturaDto.desde(asignaturaRepositorio.save(asignatura));
	}

	public void eliminar(Long id) {
		if (!asignaturaRepositorio.existsById(id)) {
			throw new RuntimeException("Asignatura no encontrada: " + id);
		}
		asignaturaRepositorio.deleteById(id);
	}
}