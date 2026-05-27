package com.playpdf.repositorios;

import com.playpdf.modelos.Respuesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RespuestaRepositorio extends JpaRepository<Respuesta, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Respuesta r WHERE r.pregunta.tema.idTema = :idTema")
    void deleteByTemaIdTema(@Param("idTema") Long idTema);

    @Modifying
    @Transactional
    @Query("DELETE FROM Respuesta r WHERE r.pregunta.tema.asignatura.idAsignatura = :idAsignatura")
    void deleteByAsignaturaIdAsignatura(@Param("idAsignatura") Long idAsignatura);
}
