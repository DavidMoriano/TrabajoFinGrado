package com.playpdf.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "temas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tema {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_tema")
	private Long idTema;

	@Column(nullable = false, length = 300)
	private String titulo;

	@Column(columnDefinition = "TEXT")
	private String descripcion;

	@Column(name = "nombre_archivo_pdf", length = 300)
	private String nombreArchivoPdf;

	@Column(name = "ruta_archivo_pdf", length = 500)
	private String rutaArchivoPdf;

	@Column(name = "fecha_subida")
	private LocalDateTime fechaSubida;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_asignatura", nullable = false)
	private Asignatura asignatura;

	@PrePersist
	protected void onCreate() {
		fechaSubida = LocalDateTime.now();
	}

	public Long getIdTema() {
		return this.idTema;
	}

	public String getTitulo() {
		return this.titulo;
	}

	public String getDescripcion() {
		return this.descripcion;
	}

	public String getNombreArchivoPdf() {
		return this.nombreArchivoPdf;
	}

	public String getRutaArchivoPdf() {
		return this.rutaArchivoPdf;
	}

	public LocalDateTime getFechaSubida() {
		return this.fechaSubida;
	}

	public Asignatura getAsignatura() {
		return this.asignatura;
	}

	public void setIdTema(Long idTema) {
		this.idTema = idTema;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public void setNombreArchivoPdf(String nombreArchivoPdf) {
		this.nombreArchivoPdf = nombreArchivoPdf;
	}

	public void setRutaArchivoPdf(String rutaArchivoPdf) {
		this.rutaArchivoPdf = rutaArchivoPdf;
	}

	public void setFechaSubida(LocalDateTime fechaSubida) {
		this.fechaSubida = fechaSubida;
	}

	public void setAsignatura(Asignatura asignatura) {
		this.asignatura = asignatura;
	}

}
