package com.playpdf.seguridad;

import com.playpdf.modelos.Usuario;
import com.playpdf.repositorios.UsuarioRepositorio;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioDetallesServicio implements UserDetailsService {

	private final UsuarioRepositorio usuarioRepositorio;

	public UsuarioDetallesServicio(UsuarioRepositorio usuarioRepositorio) {
		this.usuarioRepositorio = usuarioRepositorio;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Usuario usuario = usuarioRepositorio.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

		return new User(usuario.getEmail(), usuario.getContrasena(),
				List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())));
	}
}
