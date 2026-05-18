package com.example.siaa_control_asistencia.repository;

import com.example.siaa_control_asistencia.entity.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {
    Optional<Grupo> findByNombreGrupo(String nombreGrupo);
    Boolean existsByNombreGrupo(String nombreGrupo);
}
