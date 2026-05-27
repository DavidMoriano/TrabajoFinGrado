package com.playpdf.repositorios;

import com.playpdf.modelos.Tema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TemaRepositorio extends JpaRepository<Tema, Long> {
	List<Tema> findByAsignaturaIdAsignatura(Long idAsignatura);

	@Modifying
	@Transactional
	@Query("DELETE FROM Tema t WHERE t.asignatura.idAsignatura = :idAsignatura")
	void deleteByAsignaturaIdAsignatura(@Param("idAsignatura") Long idAsignatura);
}
