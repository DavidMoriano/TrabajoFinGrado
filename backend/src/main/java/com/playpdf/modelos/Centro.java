package com.playpdf.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "centros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Centro {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_centro")
	private Long idCentro;

	@Column(nullable = false, length = 200)
	private String nombre;

	@Column(nullable = false, length = 100)
	private String ciudad;

	@Column(nullable = false, length = 300)
	private String direccion;

	@Column(nullable = false)
	private Double latitud;

	@Column(nullable = false)
	private Double longitud;

	@Column(name = "codigo_acceso", unique = true, length = 20)
	private String codigoAcceso;

	public String getCodigoAcceso() { return codigoAcceso; }
	public void setCodigoAcceso(String codigoAcceso) { this.codigoAcceso = codigoAcceso; }

	public Long getIdCentro() {
		return this.idCentro;
	}

	public String getNombre() {
		return this.nombre;
	}

	public String getCiudad() {
		return this.ciudad;
	}

	public String getDireccion() {
		return this.direccion;
	}

	public Double getLatitud() {
		return this.latitud;
	}

	public Double getLongitud() {
		return this.longitud;
	}
}
