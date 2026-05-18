package com.example.siaa_control_asistencia.repository;

import com.example.siaa_control_asistencia.entity.DetalleAsistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleAsistenciaRepository extends JpaRepository<DetalleAsistencia, Long> {

    List<DetalleAsistencia> findByAlumno_IdAlumno(Long idAlumno);

    List<DetalleAsistencia> findByAsistencia_IdAsistencia(Long idAsistencia);
}