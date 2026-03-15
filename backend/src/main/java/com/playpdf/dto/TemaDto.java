package com.playpdf.dto;

import com.playpdf.modelos.Tema;
import java.time.format.DateTimeFormatter;

public class TemaDto {

	private Long idTema;
	private String titulo;
	private String descripcion;
	private String nombreArchivoPdf;
	private String fechaSubida;
	private Long idAsignatura;

	public TemaDto() {
	}

	public TemaDto(Long idTema, String titulo, String descripcion, String nombreArchivoPdf, String fechaSubida,
			Long idAsignatura) {
		this.idTema = idTema;
		this.titulo = titulo;
		this.descripcion = descripcion;
		this.nombreArchivoPdf = nombreArchivoPdf;
		this.fechaSubida = fechaSubida;
		this.idAsignatura = idAsignatura;
	}

	// Método estático que necesita el servicio
	public static TemaDto desde(Tema t) {
		String fecha = null;
		if (t.getFechaSubida() != null) {
			fecha = t.getFechaSubida().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		}
		Long idAsignatura = null;
		if (t.getAsignatura() != null) {
			idAsignatura = t.getAsignatura().getIdAsignatura();
		}
		return new TemaDto(t.getIdTema(), t.getTitulo(), t.getDescripcion(), t.getNombreArchivoPdf(), fecha,
				idAsignatura);
	}

	public Long getIdTema() {
		return idTema;
	}

	public void setIdTema(Long idTema) {
		this.idTema = idTema;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getNombreArchivoPdf() {
		return nombreArchivoPdf;
	}

	public void setNombreArchivoPdf(String nombre) {
		this.nombreArchivoPdf = nombre;
	}

	public String getFechaSubida() {
		return fechaSubida;
	}

	public void setFechaSubida(String fechaSubida) {
		this.fechaSubida = fechaSubida;
	}

	public Long getIdAsignatura() {
		return idAsignatura;
	}

	public void setIdAsignatura(Long idAsignatura) {
		this.idAsignatura = idAsignatura;
	}
}