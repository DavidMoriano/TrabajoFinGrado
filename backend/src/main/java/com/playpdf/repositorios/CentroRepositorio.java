package com.playpdf.repositorios;

import com.playpdf.modelos.Centro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CentroRepositorio extends JpaRepository<Centro, Long> {
    java.util.Optional<Centro> findByCodigoAcceso(String codigoAcceso);
}
