package com.playpdf.repositorios;

import com.playpdf.modelos.Estadistica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstadisticaRepositorio extends JpaRepository<Estadistica, Long> {

	List<Estadistica> findByUsuarioIdUsuario(Long idUsuario);

	Optional<Estadistica> findByUsuarioIdUsuarioAndAsignaturaIdAsignatura(Long idUsuario, Long idAsignatura);

	@Modifying
	@Transactional
	@Query("DELETE FROM Estadistica e WHERE e.asignatura.idAsignatura = :idAsignatura")
	void deleteByAsignaturaIdAsignatura(@Param("idAsignatura") Long idAsignatura);

	@Query("SELECT SUM(e.totalPartidas) FROM Estadistica e")
	Long contarTotalPartidasGlobal();

	@Query("SELECT SUM(e.totalAciertos) FROM Estadistica e")
	Long contarTotalAciertosGlobal();
}
