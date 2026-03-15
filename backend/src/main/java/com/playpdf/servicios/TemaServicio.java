package com.playpdf.servicios;

import com.playpdf.dto.TemaDto;
import com.playpdf.modelos.Asignatura;
import com.playpdf.modelos.Tema;
import com.playpdf.repositorios.AsignaturaRepositorio;
import com.playpdf.repositorios.TemaRepositorio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TemaServicio {

	private final TemaRepositorio temaRepositorio;
	private final AsignaturaRepositorio asignaturaRepositorio;

	@Value("${almacenamiento.directorio-pdfs}")
	private String directorioPdfs;

	public TemaServicio(TemaRepositorio temaRepositorio, AsignaturaRepositorio asignaturaRepositorio) {
		this.temaRepositorio = temaRepositorio;
		this.asignaturaRepositorio = asignaturaRepositorio;
	}

	public List<TemaDto> obtenerPorAsignatura(Long idAsignatura) {
		return temaRepositorio.findByAsignaturaIdAsignatura(idAsignatura).stream().map(TemaDto::desde)
				.collect(Collectors.toList());
	}

	public TemaDto obtenerPorId(Long id) {
		Tema tema = temaRepositorio.findById(id).orElseThrow(() -> new RuntimeException("Tema no encontrado: " + id));
		return TemaDto.desde(tema);
	}

	public TemaDto crear(String titulo, String descripcion, Long idAsignatura, MultipartFile archivoPdf)
			throws IOException {

		Asignatura asignatura = asignaturaRepositorio.findById(idAsignatura)
				.orElseThrow(() -> new RuntimeException("Asignatura no encontrada: " + idAsignatura));

		// Guardar el PDF en disco
		String nombreUnico = UUID.randomUUID() + "_" + archivoPdf.getOriginalFilename();
		Path directorio = Paths.get(directorioPdfs);
		Files.createDirectories(directorio);
		Path rutaArchivo = directorio.resolve(nombreUnico);
		Files.copy(archivoPdf.getInputStream(), rutaArchivo);

		Tema tema = new Tema();
		tema.setTitulo(titulo);
		tema.setDescripcion(descripcion);
		tema.setNombreArchivoPdf(archivoPdf.getOriginalFilename());
		tema.setRutaArchivoPdf(rutaArchivo.toString());
		tema.setAsignatura(asignatura);

		return TemaDto.desde(temaRepositorio.save(tema));
	}

	public TemaDto actualizar(Long id, String titulo, String descripcion, MultipartFile archivoPdf) throws IOException {

		Tema tema = temaRepositorio.findById(id).orElseThrow(() -> new RuntimeException("Tema no encontrado: " + id));

		if (titulo != null)
			tema.setTitulo(titulo);
		if (descripcion != null)
			tema.setDescripcion(descripcion);

		// Si se sube un nuevo PDF, reemplazar el anterior
		if (archivoPdf != null && !archivoPdf.isEmpty()) {
			// Borrar el archivo anterior si existe
			if (tema.getRutaArchivoPdf() != null) {
				Path anterior = Paths.get(tema.getRutaArchivoPdf());
				Files.deleteIfExists(anterior);
			}
			String nombreUnico = UUID.randomUUID() + "_" + archivoPdf.getOriginalFilename();
			Path directorio = Paths.get(directorioPdfs);
			Files.createDirectories(directorio);
			Path rutaArchivo = directorio.resolve(nombreUnico);
			Files.copy(archivoPdf.getInputStream(), rutaArchivo);
			tema.setNombreArchivoPdf(archivoPdf.getOriginalFilename());
			tema.setRutaArchivoPdf(rutaArchivo.toString());
		}

		return TemaDto.desde(temaRepositorio.save(tema));
	}

	public void eliminar(Long id) {
		Tema tema = temaRepositorio.findById(id).orElseThrow(() -> new RuntimeException("Tema no encontrado: " + id));

		// Eliminar el archivo PDF del disco
		if (tema.getRutaArchivoPdf() != null) {
			try {
				Files.deleteIfExists(Paths.get(tema.getRutaArchivoPdf()));
			} catch (IOException e) {
				// Continuar aunque no se pueda borrar el archivo
			}
		}
		temaRepositorio.deleteById(id);
	}

	// Devuelve la ruta del archivo PDF para que el servicio de IA pueda leerlo
	public Path obtenerRutaPdf(Long idTema) {
		Tema tema = temaRepositorio.findById(idTema)
				.orElseThrow(() -> new RuntimeException("Tema no encontrado: " + idTema));
		return Paths.get(tema.getRutaArchivoPdf());
	}
}
