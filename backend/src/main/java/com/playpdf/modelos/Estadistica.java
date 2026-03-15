package com.playpdf.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "estadisticas", uniqueConstraints = @UniqueConstraint(columnNames = { "id_usuario", "id_asignatura" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Estadistica {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_estadistica")
	private Long idEstadistica;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_usuario", nullable = false)
	private Usuario usuario;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_asignatura")
	private Asignatura asignatura;

	@Column(name = "total_partidas")
	private Integer totalPartidas = 0;

	@Column(name = "total_aciertos")
	private Integer totalAciertos = 0;

	@Column(name = "total_preguntas")
	private Integer totalPreguntas = 0;

	@Column(name = "racha_actual")
	private Integer rachaActual = 0;

	@Column(name = "fecha_ultimo_acceso")
	private LocalDateTime fechaUltimoAcceso;

	@PrePersist
	@PreUpdate
	protected void onUpdate() {
		fechaUltimoAcceso = LocalDateTime.now();
	}

	public Long getIdEstadistica() {
		return this.idEstadistica;
	}

	public Usuario getUsuario() {
		return this.usuario;
	}

	public Asignatura getAsignatura() {
		return this.asignatura;
	}

	public Integer getTotalPartidas() {
		return this.totalPartidas;
	}

	public Integer getTotalAciertos() {
		return this.totalAciertos;
	}

	public Integer getTotalPreguntas() {
		return this.totalPreguntas;
	}

	public Integer getRachaActual() {
		return this.rachaActual;
	}

	public LocalDateTime getFechaUltimoAcceso() {
		return this.fechaUltimoAcceso;
	}

	public void setIdEstadistica(Long idEstadistica) {
		this.idEstadistica = idEstadistica;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public void setAsignatura(Asignatura asignatura) {
		this.asignatura = asignatura;
	}

	public void setTotalPartidas(Integer totalPartidas) {
		this.totalPartidas = totalPartidas;
	}

	public void setTotalAciertos(Integer totalAciertos) {
		this.totalAciertos = totalAciertos;
	}

	public void setTotalPreguntas(Integer totalPreguntas) {
		this.totalPreguntas = totalPreguntas;
	}

	public void setRachaActual(Integer rachaActual) {
		this.rachaActual = rachaActual;
	}

	public void setFechaUltimoAcceso(LocalDateTime fechaUltimoAcceso) {
		this.fechaUltimoAcceso = fechaUltimoAcceso;
	}

}
