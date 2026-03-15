package com.playpdf.repositorios;

import com.playpdf.modelos.Estadistica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstadisticaRepositorio extends JpaRepository<Estadistica, Long> {

	List<Estadistica> findByUsuarioIdUsuario(Long idUsuario);

	Optional<Estadistica> findByUsuarioIdUsuarioAndAsignaturaIdAsignatura(Long idUsuario, Long idAsignatura);

	@Query("SELECT SUM(e.totalPartidas) FROM Estadistica e")
	Long contarTotalPartidasGlobal();

	@Query("SELECT SUM(e.totalAciertos) FROM Estadistica e")
	Long contarTotalAciertosGlobal();
}
