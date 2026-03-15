package com.playpdf.modelos;

import jakarta.persistence.*;

@Entity
@Table(name = "asignaturas")
public class Asignatura {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_asignatura")
	private Long idAsignatura;

	@Column(nullable = false, length = 200)
	private String nombre;

	@Column(columnDefinition = "TEXT")
	private String descripcion;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_profesor")
	private Usuario profesor;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_centro")
	private Centro centro;

	public Asignatura() {
		super();
	}

	public Asignatura(Long idAsignatura, String nombre, String descripcion, Usuario profesor, Centro centro) {
		super();
		this.idAsignatura = idAsignatura;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.profesor = profesor;
		this.centro = centro;
	}

	public Long getIdAsignatura() {
		return this.idAsignatura;
	}

	public String getNombre() {
		return this.nombre;
	}

	public String getDescripcion() {
		return this.descripcion;
	}

	public Usuario getProfesor() {
		return this.profesor;
	}

	public Centro getCentro() {
		return this.centro;
	}

	public void setIdAsignatura(Long idAsignatura) {
		this.idAsignatura = idAsignatura;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public void setProfesor(Usuario profesor) {
		this.profesor = profesor;
	}

	public void setCentro(Centro centro) {
		this.centro = centro;
	}

}
