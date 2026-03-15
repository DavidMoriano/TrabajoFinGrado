package com.playpdf.dto;

import com.playpdf.modelos.Asignatura;

public class AsignaturaDto {

	private Long idAsignatura;
	private String nombre;
	private String descripcion;
	private Long idProfesor;
	private String nombreProfesor;
	private Long idCentro;
	private String nombreCentro;
	private int cantidadTemas;

	public static AsignaturaDto desde(Asignatura a) {
		AsignaturaDto dto = new AsignaturaDto();
		dto.setIdAsignatura(a.getIdAsignatura());
		dto.setNombre(a.getNombre());
		dto.setDescripcion(a.getDescripcion());
		if (a.getProfesor() != null) {
			dto.setIdProfesor(a.getProfesor().getIdUsuario());
			dto.setNombreProfesor(a.getProfesor().getNombre() + " " + a.getProfesor().getApellidos());
		}
		if (a.getCentro() != null) {
			dto.setIdCentro(a.getCentro().getIdCentro());
			dto.setNombreCentro(a.getCentro().getNombre());
		}
		return dto;
	}

	public Long getIdAsignatura() {
		return idAsignatura;
	}

	public void setIdAsignatura(Long idAsignatura) {
		this.idAsignatura = idAsignatura;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Long getIdProfesor() {
		return idProfesor;
	}

	public void setIdProfesor(Long idProfesor) {
		this.idProfesor = idProfesor;
	}

	public String getNombreProfesor() {
		return nombreProfesor;
	}

	public void setNombreProfesor(String nombreProfesor) {
		this.nombreProfesor = nombreProfesor;
	}

	public Long getIdCentro() {
		return idCentro;
	}

	public void setIdCentro(Long idCentro) {
		this.idCentro = idCentro;
	}

	public String getNombreCentro() {
		return nombreCentro;
	}

	public void setNombreCentro(String nombreCentro) {
		this.nombreCentro = nombreCentro;
	}

	public int getCantidadTemas() {
		return cantidadTemas;
	}

	public void setCantidadTemas(int cantidadTemas) {
		this.cantidadTemas = cantidadTemas;
	}
}