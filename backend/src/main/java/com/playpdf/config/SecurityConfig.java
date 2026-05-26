package com.playpdf.config;

import com.playpdf.seguridad.JwtFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtFilter jwtFilter;

	@Value("${frontend.url}")
	private String frontendUrl;

	public SecurityConfig(JwtFilter jwtFilter) {
		this.jwtFilter = jwtFilter;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors(cors -> cors.configurationSource(corsConfigurationSource())).csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						// Rutas públicas
						.requestMatchers("/api/auth/**").permitAll()
						// Centros - solo ADMIN puede crear y eliminar
						.requestMatchers(HttpMethod.GET, "/api/centros/**").authenticated()
						.requestMatchers(HttpMethod.POST, "/api/centros/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/centros/**").hasRole("ADMIN")
						// Usuarios - solo ADMIN
						.requestMatchers("/api/usuarios/**").hasRole("ADMIN")
						// Estadísticas globales - solo ADMIN
						.requestMatchers("/api/estadisticas/globales").hasRole("ADMIN")
						// Asignaturas - cualquier autenticado
						.requestMatchers("/api/asignaturas/**").authenticated()
						// Temas - cualquier autenticado
						.requestMatchers("/api/temas/**").authenticated()
						// Juegos - cualquier autenticado
						.requestMatchers("/api/juegos/**").authenticated()
						// Estadísticas propias - cualquier autenticado
						.requestMatchers("/api/estadisticas/**").authenticated()
						// Resto
						.anyRequest().authenticated())
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of(frontendUrl));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/api/**", config);
		return source;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}