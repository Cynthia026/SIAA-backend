package com.example.siaa_control_asistencia.repository;

import com.example.siaa_control_asistencia.entity.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocenteRepository extends JpaRepository<Docente, Long> {
    Optional<Docente> findByUsuario_IdUsuario(Long idUsuario);
}
