package com.example.siaa_control_asistencia.repository;

import com.example.siaa_control_asistencia.entity.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, Long> {

    Optional<Materia> findByCodigoMateria(String codigoMateria);

    Boolean existsByCodigoMateria(String codigoMateria);

    // Buscar materias asignadas a un docente específico (por id_docente en la tabla materias)
    List<Materia> findByDocente_IdDocente(Long idDocente);

    // Buscar materias asignadas al usuario que es docente
    List<Materia> findByDocente_Usuario_IdUsuario(Long idUsuario);
}
