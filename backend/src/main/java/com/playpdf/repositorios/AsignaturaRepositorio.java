package com.playpdf.repositorios;

import com.playpdf.modelos.Asignatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsignaturaRepositorio extends JpaRepository<Asignatura, Long> {

	List<Asignatura> findByProfesorIdUsuario(Long idProfesor);

	@Query("SELECT a FROM Asignatura a LEFT JOIN FETCH a.profesor LEFT JOIN FETCH a.centro")
	List<Asignatura> findAllConDetalle();
}
