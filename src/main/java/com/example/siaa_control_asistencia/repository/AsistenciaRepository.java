package com.example.siaa_control_asistencia.repository;

import com.example.siaa_control_asistencia.entity.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    
    List<Asistencia> findByGrupo_IdGrupo(Long idGrupo);
    
    List<Asistencia> findByMateria_IdMateria(Long idMateria);
    
    List<Asistencia> findByDocente_IdDocente(Long idDocente);
    
    List<Asistencia> findByFechaClaseBetween(LocalDate fechaInicio, LocalDate fechaFin);
    
    Optional<Asistencia> findByMateria_IdMateriaAndGrupo_IdGrupoAndFechaClase(
        Long idMateria, Long idGrupo, LocalDate fechaClase
    );
    
    @Query("SELECT a FROM Asistencia a WHERE a.grupo.idGrupo = :idGrupo " +
           "AND a.fechaClase BETWEEN :fechaInicio AND :fechaFin")
    List<Asistencia> findByGrupoAndFechaRange(
        @Param("idGrupo") Long idGrupo,
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin
    );
}
