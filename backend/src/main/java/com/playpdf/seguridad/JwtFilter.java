package com.playpdf.seguridad;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final UsuarioDetallesServicio usuarioDetallesServicio;

	public JwtFilter(JwtUtil jwtUtil, UsuarioDetallesServicio usuarioDetallesServicio) {
		this.jwtUtil = jwtUtil;
		this.usuarioDetallesServicio = usuarioDetallesServicio;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String cabecera = request.getHeader("Authorization");

		if (cabecera != null && cabecera.startsWith("Bearer ")) {
			String token = cabecera.substring(7);

			if (jwtUtil.validarToken(token)) {
				String email = jwtUtil.extraerEmail(token);
				UserDetails userDetails = usuarioDetallesServicio.loadUserByUsername(email);

				UsernamePasswordAuthenticationToken autenticacion = new UsernamePasswordAuthenticationToken(userDetails,
						null, userDetails.getAuthorities());

				autenticacion.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(autenticacion);
			}
		}

		filterChain.doFilter(request, response);
	}
}
