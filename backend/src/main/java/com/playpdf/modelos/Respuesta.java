package com.playpdf.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "respuestas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Respuesta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_respuesta")
	private Long idRespuesta;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String texto;

	@Column(name = "es_correcta", nullable = false)
	private Boolean esCorrecta = false;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_pregunta", nullable = false)
	private Pregunta pregunta;

	public Long getIdRespuesta() {
		return this.idRespuesta;
	}

	public String getTexto() {
		return this.texto;
	}

	public Boolean getEsCorrecta() {
		return this.esCorrecta;
	}

	public Pregunta getPregunta() {
		return this.pregunta;
	}

	public void setIdRespuesta(Long idRespuesta) {
		this.idRespuesta = idRespuesta;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public void setEsCorrecta(Boolean esCorrecta) {
		this.esCorrecta = esCorrecta;
	}

	public void setPregunta(Pregunta pregunta) {
		this.pregunta = pregunta;
	}

}
