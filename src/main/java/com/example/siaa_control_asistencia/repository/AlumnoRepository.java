package com.example.siaa_control_asistencia.repository;

import com.example.siaa_control_asistencia.entity.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

    Optional<Alumno> findByUsuario_IdUsuario(Long idUsuario);

    // Obtener todos los alumnos de un grupo específico
    List<Alumno> findByGrupo_IdGrupo(Long idGrupo);
}
