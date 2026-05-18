package com.example.siaa_control_asistencia.service;

import com.example.siaa_control_asistencia.entity.Alumno;
import com.example.siaa_control_asistencia.entity.Docente;
import com.example.siaa_control_asistencia.entity.Usuario;
import com.example.siaa_control_asistencia.repository.AlumnoRepository;
import com.example.siaa_control_asistencia.repository.DocenteRepository;
import com.example.siaa_control_asistencia.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de Usuarios
 * Lógica de negocio para gestión de usuarios
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final AlumnoRepository alumnoRepository;
    private final DocenteRepository docenteRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Obtener todos los usuarios
     */
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * Obtener usuario por ID
     */
    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    /**
     * Obtener usuario por matrícula
     */
    public Usuario obtenerPorMatricula(String matricula) {
        return usuarioRepository.findByMatricula(matricula)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con matrícula: " + matricula));
    }

    /**
     * Crear nuevo usuario
     */
    @Transactional
    public Usuario crear(Usuario usuario) {
        // Validar que no exista la matrícula
        if (usuarioRepository.existsByMatricula(usuario.getMatricula())) {
            throw new RuntimeException("La matrícula ya está registrada");
        }

        // Validar que no exista el email (solo si viene)
        if (usuario.getEmail() != null && !usuario.getEmail().isEmpty() &&
                usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Encriptar contraseña
        usuario.setPasswordHash(passwordEncoder.encode(usuario.getPasswordHash()));

        // Establecer estado por defecto
        if (usuario.getEstado() == null || usuario.getEstado().isEmpty()) {
            usuario.setEstado("ACTIVO");
        }

        // Guardar usuario
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // Si es alumno, crear registro de alumno
        if ("Alumno".equals(usuario.getRol()) && usuario.getAlumno() != null) {
            Alumno alumno = usuario.getAlumno();
            alumno.setUsuario(usuarioGuardado);
            alumnoRepository.save(alumno);
        }

        // Si es docente, crear registro de docente
        if ("Docente".equals(usuario.getRol()) && usuario.getDocente() != null) {
            Docente docente = usuario.getDocente();
            docente.setUsuario(usuarioGuardado);
            docenteRepository.save(docente);
        }

        return usuarioGuardado;
    }

    /**
     * Actualizar usuario
     */
    @Transactional
    public Usuario actualizar(Long id, Usuario usuarioActualizado) {
        Usuario usuario = obtenerPorId(id);

        // Actualizar campos
        if (usuarioActualizado.getNombreCompleto() != null) {
            usuario.setNombreCompleto(usuarioActualizado.getNombreCompleto());
        }
        if (usuarioActualizado.getEmail() != null && !usuarioActualizado.getEmail().isEmpty()) {
            // Validar que el email no esté en uso por otro usuario
            if (!usuario.getEmail().equals(usuarioActualizado.getEmail()) &&
                    usuarioRepository.existsByEmail(usuarioActualizado.getEmail())) {
                throw new RuntimeException("El email ya está en uso");
            }
            usuario.setEmail(usuarioActualizado.getEmail());
        }
        if (usuarioActualizado.getFotoPerfil() != null) {
            usuario.setFotoPerfil(usuarioActualizado.getFotoPerfil());
        }

        // Si se proporciona nueva contraseña
        if (usuarioActualizado.getPasswordHash() != null &&
                !usuarioActualizado.getPasswordHash().isEmpty()) {
            usuario.setPasswordHash(passwordEncoder.encode(usuarioActualizado.getPasswordHash()));
        }

        return usuarioRepository.save(usuario);
    }

    /**
     * Dar de baja a un usuario
     */
    @Transactional
    public Usuario darDeBaja(Long id) {
        Usuario usuario = obtenerPorId(id);
        usuario.setEstado("INACTIVO");
        usuario.setFechaBaja(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }

    /**
     * Dar de alta a un usuario
     */
    @Transactional
    public Usuario darDeAlta(Long id) {
        Usuario usuario = obtenerPorId(id);
        usuario.setEstado("ACTIVO");
        usuario.setFechaBaja(null);
        return usuarioRepository.save(usuario);
    }

    /**
     * Eliminar usuario
     */
    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = obtenerPorId(id);
        usuarioRepository.delete(usuario);
    }

    /**
     * Obtener usuarios por rol
     */
    public List<Usuario> obtenerPorRol(String rol) {
        return usuarioRepository.findByRol(rol);
    }

    /**
     * Obtener usuarios por estado
     */
    public List<Usuario> obtenerPorEstado(String estado) {
        return usuarioRepository.findByEstado(estado);
    }

    /**
     * Asignar alumno a grupo
     */
    @Transactional
    public Alumno asignarGrupo(Long idUsuario, Long idGrupo) {
        Alumno alumno = alumnoRepository.findByUsuario_IdUsuario(idUsuario)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        // Si idGrupo es null, remover del grupo
        if (idGrupo == null) {
            alumno.setGrupo(null);
        } else {
            // Aquí se debería buscar el grupo y asignarlo
            // Por ahora dejamos que JPA lo maneje por la relación
        }

        return alumnoRepository.save(alumno);
    }
}