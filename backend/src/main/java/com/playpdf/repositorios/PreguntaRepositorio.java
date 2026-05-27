package com.playpdf.repositorios;

import com.playpdf.modelos.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PreguntaRepositorio extends JpaRepository<Pregunta, Long> {

	List<Pregunta> findByTemaIdTemaAndTipo(Long idTema, Pregunta.TipoPregunta tipo);

	List<Pregunta> findByTemaIdTema(Long idTema);

	@Modifying
	@Transactional
	@Query("DELETE FROM Pregunta p WHERE p.tema.idTema = :idTema")
	void deleteByIdTema(@Param("idTema") Long idTema);

	@Modifying
	@Transactional
	@Query("DELETE FROM Pregunta p WHERE p.tema.asignatura.idAsignatura = :idAsignatura")
	void deleteByAsignaturaIdAsignatura(@Param("idAsignatura") Long idAsignatura);
}