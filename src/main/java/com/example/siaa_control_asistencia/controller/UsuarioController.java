package com.example.siaa_control_asistencia.controller;

import com.example.siaa_control_asistencia.entity.Alumno;
import com.example.siaa_control_asistencia.entity.Docente;
import com.example.siaa_control_asistencia.entity.Usuario;
import com.example.siaa_control_asistencia.repository.AlumnoRepository;
import com.example.siaa_control_asistencia.repository.DocenteRepository;
import com.example.siaa_control_asistencia.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final DocenteRepository docenteRepository;
    private final AlumnoRepository alumnoRepository;

    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerTodos() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @PostMapping
    public ResponseEntity<?> crearUsuario(@RequestBody Map<String, Object> usuarioData) {
        try {
            if (!usuarioData.containsKey("nombreCompleto") ||
                    !usuarioData.containsKey("matricula") ||
                    !usuarioData.containsKey("rol") ||
                    !usuarioData.containsKey("password")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Faltan campos obligatorios: nombreCompleto, matricula, rol, password");
                return ResponseEntity.badRequest().body(error);
            }

            String matricula = (String) usuarioData.get("matricula");
            if (usuarioRepository.findByMatricula(matricula).isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "La matrícula ya está registrada");
                return ResponseEntity.badRequest().body(error);
            }

            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombreCompleto((String) usuarioData.get("nombreCompleto"));
            nuevoUsuario.setMatricula(matricula);

            if (usuarioData.containsKey("email") && usuarioData.get("email") != null) {
                String email = usuarioData.get("email").toString().trim();
                if (!email.isEmpty()) nuevoUsuario.setEmail(email);
            }

            nuevoUsuario.setPassword(passwordEncoder.encode((String) usuarioData.get("password")));

            String rol = (String) usuarioData.get("rol");
            nuevoUsuario.setRol(rol);

            if (usuarioData.containsKey("estado") && usuarioData.get("estado") != null) {
                nuevoUsuario.setEstado((String) usuarioData.get("estado"));
            } else {
                nuevoUsuario.setEstado("ACTIVO");
            }

            Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

            // AUTO-CREAR REGISTRO SEGÚN EL ROL
            if ("Docente".equals(rol)) {
                if (docenteRepository.findByUsuario_IdUsuario(usuarioGuardado.getIdUsuario()).isEmpty()) {
                    Docente docente = new Docente();
                    docente.setUsuario(usuarioGuardado);
                    docenteRepository.save(docente);
                }
            } else if ("Alumno".equals(rol)) {
                if (alumnoRepository.findByUsuario_IdUsuario(usuarioGuardado.getIdUsuario()).isEmpty()) {
                    Alumno alumno = new Alumno();
                    alumno.setUsuario(usuarioGuardado);
                    alumno.setNumeroControl(matricula);
                    alumnoRepository.save(alumno);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuario creado exitosamente");
            response.put("usuario", usuarioGuardado);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al crear usuario: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id,
                                               @RequestBody Map<String, Object> updates) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

            if (updates.containsKey("nombreCompleto"))
                usuario.setNombreCompleto((String) updates.get("nombreCompleto"));

            if (updates.containsKey("matricula")) {
                String nuevaMatricula = (String) updates.get("matricula");
                usuarioRepository.findByMatricula(nuevaMatricula).ifPresent(u -> {
                    if (!u.getIdUsuario().equals(id))
                        throw new RuntimeException("La matrícula ya está en uso");
                });
                usuario.setMatricula(nuevaMatricula);
            }

            if (updates.containsKey("email")) {
                Object emailObj = updates.get("email");
                usuario.setEmail((emailObj != null && !emailObj.toString().isEmpty())
                        ? (String) emailObj : null);
            }

            String rolAnterior = usuario.getRol();
            if (updates.containsKey("rol")) {
                String nuevoRol = (String) updates.get("rol");
                usuario.setRol(nuevoRol);

                if ("Docente".equals(nuevoRol) && !"Docente".equals(rolAnterior)) {
                    if (docenteRepository.findByUsuario_IdUsuario(id).isEmpty()) {
                        Docente d = new Docente();
                        d.setUsuario(usuario);
                        docenteRepository.save(d);
                    }
                }
                if ("Alumno".equals(nuevoRol) && !"Alumno".equals(rolAnterior)) {
                    if (alumnoRepository.findByUsuario_IdUsuario(id).isEmpty()) {
                        Alumno a = new Alumno();
                        a.setUsuario(usuario);
                        a.setNumeroControl(usuario.getMatricula());
                        alumnoRepository.save(a);
                    }
                }
            }

            if (updates.containsKey("estado"))
                usuario.setEstado((String) updates.get("estado"));

            if (updates.containsKey("password")) {
                Object pwd = updates.get("password");
                if (pwd != null && !pwd.toString().isEmpty())
                    usuario.setPassword(passwordEncoder.encode((String) pwd));
            }

            Usuario usuarioActualizado = usuarioRepository.save(usuario);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuario actualizado exitosamente");
            response.put("usuario", usuarioActualizado);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al actualizar usuario: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            usuarioRepository.delete(usuario);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuario eliminado exitosamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al eliminar usuario: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}