package com.example.siaa_control_asistencia.controller;

import com.example.siaa_control_asistencia.entity.Alumno;
import com.example.siaa_control_asistencia.entity.Grupo;
import com.example.siaa_control_asistencia.entity.Usuario;
import com.example.siaa_control_asistencia.repository.AlumnoRepository;
import com.example.siaa_control_asistencia.repository.GrupoRepository;
import com.example.siaa_control_asistencia.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/alumnos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AlumnoController {

    private final AlumnoRepository alumnoRepository;
    private final GrupoRepository grupoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * GET /alumnos - Obtener todos los alumnos
     */
    @GetMapping
    public ResponseEntity<List<Alumno>> obtenerTodos() {
        List<Alumno> alumnos = alumnoRepository.findAll();
        return ResponseEntity.ok(alumnos);
    }

    /**
     * GET /alumnos/{id} - Obtener alumno por ID de alumno (PK de la tabla alumnos)
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            Alumno alumno = alumnoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Alumno no encontrado con ID: " + id));
            return ResponseEntity.ok(alumno);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * GET /alumnos/usuario/{idUsuario}
     * Obtener alumno por ID de usuario (útil para el panel admin y alumno)
     */
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<?> obtenerPorUsuario(@PathVariable Long idUsuario) {
        try {
            Alumno alumno = alumnoRepository.findByUsuario_IdUsuario(idUsuario)
                    .orElseThrow(() -> new RuntimeException("Alumno no encontrado para usuario ID: " + idUsuario));
            return ResponseEntity.ok(alumno);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * GET /alumnos/grupo/{idGrupo}
     * Obtener todos los alumnos de un grupo específico.
     * CRÍTICO para el panel de registro de asistencias del docente.
     */
    @GetMapping("/grupo/{idGrupo}")
    public ResponseEntity<?> obtenerPorGrupo(@PathVariable Long idGrupo) {
        try {
            // Verificar que el grupo existe
            grupoRepository.findById(idGrupo)
                    .orElseThrow(() -> new RuntimeException("Grupo no encontrado con ID: " + idGrupo));

            List<Alumno> alumnos = alumnoRepository.findByGrupo_IdGrupo(idGrupo);
            return ResponseEntity.ok(alumnos);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * PUT /alumnos/{idUsuario}/asignar-grupo
     * Asignar alumno a un grupo (busca por idUsuario).
     */
    @PutMapping("/{idUsuario}/asignar-grupo")
    public ResponseEntity<?> asignarGrupo(
            @PathVariable Long idUsuario,
            @RequestBody Map<String, Object> request) {

        try {
            // Buscar o crear registro de alumno
            Alumno alumno = alumnoRepository.findByUsuario_IdUsuario(idUsuario)
                    .orElseGet(() -> {
                        Usuario usuario = usuarioRepository.findById(idUsuario)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));

                        if (!"Alumno".equals(usuario.getRol())) {
                            throw new RuntimeException("El usuario no tiene rol de Alumno");
                        }

                        Alumno nuevoAlumno = new Alumno();
                        nuevoAlumno.setUsuario(usuario);
                        nuevoAlumno.setNumeroControl(usuario.getMatricula());

                        return alumnoRepository.save(nuevoAlumno);
                    });

            Object idGrupoObj = request.get("idGrupo");

            if (idGrupoObj == null) {
                // Remover del grupo
                alumno.setGrupo(null);
            } else {
                Long idGrupo = Long.valueOf(idGrupoObj.toString());
                Grupo grupo = grupoRepository.findById(idGrupo)
                        .orElseThrow(() -> new RuntimeException("Grupo no encontrado con ID: " + idGrupo));
                alumno.setGrupo(grupo);
            }

            Alumno alumnoActualizado = alumnoRepository.save(alumno);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", idGrupoObj != null ? "Grupo asignado correctamente" : "Alumno removido del grupo");
            response.put("alumno", alumnoActualizado);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al asignar grupo: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
