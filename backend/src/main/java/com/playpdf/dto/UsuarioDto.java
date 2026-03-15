package com.playpdf.dto;

import com.playpdf.modelos.Usuario;
import java.time.format.DateTimeFormatter;

public class UsuarioDto {

	private Long idUsuario;
	private String nombre;
	private String apellidos;
	private String email;
	private String rol;
	private Integer edad;
	private String estudios;
	private String fechaRegistro;

	public UsuarioDto() {
	}

	public UsuarioDto(Long idUsuario, String nombre, String apellidos, String email, String rol, Integer edad,
			String estudios, String fechaRegistro) {
		this.idUsuario = idUsuario;
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.email = email;
		this.rol = rol;
		this.edad = edad;
		this.estudios = estudios;
		this.fechaRegistro = fechaRegistro;
	}

	public static UsuarioDto desde(Usuario u) {
		String fecha = null;
		if (u.getFechaRegistro() != null) {
			fecha = u.getFechaRegistro().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		}
		return new UsuarioDto(u.getIdUsuario(), u.getNombre(), u.getApellidos(), u.getEmail(), u.getRol().name(),
				u.getEdad(), u.getEstudios(), fecha);
	}

	public Long getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}

	public Integer getEdad() {
		return edad;
	}

	public void setEdad(Integer edad) {
		this.edad = edad;
	}

	public String getEstudios() {
		return estudios;
	}

	public void setEstudios(String estudios) {
		this.estudios = estudios;
	}

	public String getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(String fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}
}