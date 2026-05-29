package com.playpdf.repositorios;

import com.playpdf.modelos.UsuarioCentro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioCentroRepositorio extends JpaRepository<UsuarioCentro, Long> {
    List<UsuarioCentro> findByUsuarioIdUsuario(Long idUsuario);
    Optional<UsuarioCentro> findByUsuarioIdUsuarioAndCentroIdCentro(Long idUsuario, Long idCentro);
    boolean existsByUsuarioIdUsuarioAndCentroIdCentro(Long idUsuario, Long idCentro);
}
