package com.playpdf.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "preguntas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pregunta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_pregunta")
	private Long idPregunta;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String enunciado;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TipoPregunta tipo = TipoPregunta.quiz;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tema", nullable = false)
	private Tema tema;

	@OneToMany(mappedBy = "pregunta", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Respuesta> respuestas = new ArrayList<>();

	public Long getIdPregunta() {
		return this.idPregunta;
	}

	public String getEnunciado() {
		return this.enunciado;
	}

	public TipoPregunta getTipo() {
		return this.tipo;
	}

	public Tema getTema() {
		return this.tema;
	}

	public List<Respuesta> getRespuestas() {
		return this.respuestas;
	}

	public enum TipoPregunta {
		quiz, puzzle
	}

	public void setIdPregunta(Long idPregunta) {
		this.idPregunta = idPregunta;
	}

	public void setEnunciado(String enunciado) {
		this.enunciado = enunciado;
	}

	public void setTipo(TipoPregunta tipo) {
		this.tipo = tipo;
	}

	public void setTema(Tema tema) {
		this.tema = tema;
	}

	public void setRespuestas(List<Respuesta> respuestas) {
		this.respuestas = respuestas;
	}

}
