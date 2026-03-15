package com.playpdf.servicios;

import com.playpdf.modelos.Centro;
import com.playpdf.repositorios.CentroRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CentroServicio {

	private final CentroRepositorio centroRepositorio;

	public CentroServicio(CentroRepositorio centroRepositorio) {
		this.centroRepositorio = centroRepositorio;
	}

	public List<Centro> obtenerTodos() {
		return centroRepositorio.findAll();
	}

	public Centro obtenerPorId(Long id) {
		return centroRepositorio.findById(id).orElseThrow(() -> new RuntimeException("Centro no encontrado: " + id));
	}

	public Centro crear(Centro centro) {
		return centroRepositorio.save(centro);
	}

	public void eliminar(Long id) {
		if (!centroRepositorio.existsById(id)) {
			throw new RuntimeException("Centro no encontrado: " + id);
		}
		centroRepositorio.deleteById(id);
	}
}
