package com.playpdf.servicios;

import com.playpdf.modelos.Asignatura;
import com.playpdf.modelos.Estadistica;
import com.playpdf.modelos.Usuario;
import com.playpdf.repositorios.AsignaturaRepositorio;
import com.playpdf.repositorios.EstadisticaRepositorio;
import com.playpdf.repositorios.UsuarioRepositorio;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EstadisticaServicio {

	private final EstadisticaRepositorio estadisticaRepositorio;
	private final UsuarioRepositorio usuarioRepositorio;
	private final AsignaturaRepositorio asignaturaRepositorio;

	public EstadisticaServicio(EstadisticaRepositorio estadisticaRepositorio, UsuarioRepositorio usuarioRepositorio,
			AsignaturaRepositorio asignaturaRepositorio) {
		this.estadisticaRepositorio = estadisticaRepositorio;
		this.usuarioRepositorio = usuarioRepositorio;
		this.asignaturaRepositorio = asignaturaRepositorio;
	}

	public Map<String, Object> obtenerMisEstadisticas() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		Usuario usuario = usuarioRepositorio.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		List<Estadistica> lista = estadisticaRepositorio.findByUsuarioIdUsuario(usuario.getIdUsuario());

		int totalPartidas = lista.stream().mapToInt(Estadistica::getTotalPartidas).sum();
		int totalAciertos = lista.stream().mapToInt(Estadistica::getTotalAciertos).sum();
		int totalPreguntas = lista.stream().mapToInt(Estadistica::getTotalPreguntas).sum();
		int rachaActual = lista.stream().mapToInt(Estadistica::getRachaActual).max().orElse(0);

		List<Map<String, Object>> porAsignatura = new ArrayList<>();
		for (Estadistica e : lista) {
			if (e.getAsignatura() != null) {
				porAsignatura.add(Map.of(
						"nombreAsignatura", e.getAsignatura().getNombre(),
						"totalPartidas", e.getTotalPartidas(),
						"totalAciertos", e.getTotalAciertos(),
						"totalPreguntas", e.getTotalPreguntas()));
			}
		}

		return Map.of(
				"totalPartidas", totalPartidas,
				"totalAciertos", totalAciertos,
				"totalPreguntas", totalPreguntas,
				"rachaActual", rachaActual,
				"porAsignatura", porAsignatura);
	}

	public void registrarPartida(Long idAsignatura, int aciertos, int totalPreguntas) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		Usuario usuario = usuarioRepositorio.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		Asignatura asignatura = asignaturaRepositorio.findById(idAsignatura)
				.orElseThrow(() -> new RuntimeException("Asignatura no encontrada"));

		Optional<Estadistica> existente = estadisticaRepositorio
				.findByUsuarioIdUsuarioAndAsignaturaIdAsignatura(usuario.getIdUsuario(), idAsignatura);

		Estadistica estadistica = existente.orElseGet(() -> {
			Estadistica nueva = new Estadistica();
			nueva.setUsuario(usuario);
			nueva.setAsignatura(asignatura);
			nueva.setTotalPartidas(0);
			nueva.setTotalAciertos(0);
			nueva.setTotalPreguntas(0);
			nueva.setRachaActual(0);
			return nueva;
		});

		estadistica.setTotalPartidas(estadistica.getTotalPartidas() + 1);
		estadistica.setTotalAciertos(estadistica.getTotalAciertos() + aciertos);
		estadistica.setTotalPreguntas(estadistica.getTotalPreguntas() + totalPreguntas);

		// Racha: si acertó todo en esta partida, incrementar; si no, resetear
		if (totalPreguntas > 0 && aciertos == totalPreguntas) {
			estadistica.setRachaActual(estadistica.getRachaActual() + 1);
		} else {
			estadistica.setRachaActual(0);
		}

		estadisticaRepositorio.save(estadistica);
	}

	public Map<String, Object> obtenerEstadisticasGlobales() {
		Long totalPartidas = estadisticaRepositorio.contarTotalPartidasGlobal();
		Long totalAciertos = estadisticaRepositorio.contarTotalAciertosGlobal();
		long totalUsuarios = usuarioRepositorio.count();

		return Map.of(
				"totalPartidasGlobal", totalPartidas != null ? totalPartidas : 0,
				"totalAciertosGlobal", totalAciertos != null ? totalAciertos : 0,
				"totalUsuarios", totalUsuarios);
	}
}
