package com.playpdf.repositorios;

import com.playpdf.modelos.Tema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemaRepositorio extends JpaRepository<Tema, Long> {
	List<Tema> findByAsignaturaIdAsignatura(Long idAsignatura);
}
