package com.example.siaa_control_asistencia.repository;

import com.example.siaa_control_asistencia.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByMatricula(String matricula);

    Optional<Usuario> findByEmail(String email);

    Boolean existsByMatricula(String matricula);

    Boolean existsByEmail(String email);

    List<Usuario> findByRol(String rol);

    List<Usuario> findByEstado(String estado);

    List<Usuario> findByRolAndEstado(String rol, String estado);
}