package com.example.siaa_control_asistencia.controller;

import com.example.siaa_control_asistencia.entity.Docente;
import com.example.siaa_control_asistencia.entity.Usuario;
import com.example.siaa_control_asistencia.repository.DocenteRepository;
import com.example.siaa_control_asistencia.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/docentes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class DocenteController {

    private final DocenteRepository docenteRepository;
    private final UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<Docente>> obtenerTodos() {
        return ResponseEntity.ok(docenteRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            Docente docente = docenteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Docente no encontrado con ID: " + id));
            return ResponseEntity.ok(docente);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * GET /docentes/usuario/{idUsuario}
     * Si no existe registro de docente pero el usuario tiene rol Docente,
     * lo crea automáticamente (cubre usuarios creados antes del fix).
     */
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<?> obtenerPorUsuario(@PathVariable Long idUsuario) {
        try {
            Optional<Docente> docenteOpt = docenteRepository.findByUsuario_IdUsuario(idUsuario);

            if (docenteOpt.isPresent()) {
                return ResponseEntity.ok(docenteOpt.get());
            }

            // No existe → intentar auto-crear si el usuario es Docente
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
            if (usuarioOpt.isPresent() && "Docente".equals(usuarioOpt.get().getRol())) {
                Docente nuevoDocente = new Docente();
                nuevoDocente.setUsuario(usuarioOpt.get());
                Docente guardado = docenteRepository.save(nuevoDocente);
                return ResponseEntity.ok(guardado);
            }

            Map<String, String> error = new HashMap<>();
            error.put("error", "No se encontró registro de docente para el usuario ID: " + idUsuario);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al buscar docente: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> docenteData) {
        try {
            if (!docenteData.containsKey("idUsuario") || docenteData.get("idUsuario") == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "El idUsuario es obligatorio");
                return ResponseEntity.badRequest().body(error);
            }

            Long idUsuario = Long.valueOf(docenteData.get("idUsuario").toString());
            Usuario usuario = usuarioRepository.findById(idUsuario)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));

            if (!"Docente".equals(usuario.getRol())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "El usuario no tiene rol de Docente");
                return ResponseEntity.badRequest().body(error);
            }

            if (docenteRepository.findByUsuario_IdUsuario(idUsuario).isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "El usuario ya tiene un registro de docente");
                return ResponseEntity.badRequest().body(error);
            }

            Docente docente = new Docente();
            docente.setUsuario(usuario);
            if (docenteData.containsKey("especialidad") && docenteData.get("especialidad") != null)
                docente.setEspecialidad(docenteData.get("especialidad").toString());
            if (docenteData.containsKey("titulo") && docenteData.get("titulo") != null)
                docente.setTitulo(docenteData.get("titulo").toString());

            Docente guardado = docenteRepository.save(docente);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Docente creado exitosamente");
            response.put("docente", guardado);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al crear docente: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @RequestBody Map<String, Object> updates) {
        try {
            Docente docente = docenteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Docente no encontrado con ID: " + id));

            if (updates.containsKey("especialidad"))
                docente.setEspecialidad(updates.get("especialidad") != null ? updates.get("especialidad").toString() : null);
            if (updates.containsKey("titulo"))
                docente.setTitulo(updates.get("titulo") != null ? updates.get("titulo").toString() : null);

            Docente actualizado = docenteRepository.save(docente);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Docente actualizado exitosamente");
            response.put("docente", actualizado);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al actualizar docente: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            Docente docente = docenteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Docente no encontrado con ID: " + id));
            docenteRepository.delete(docente);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Docente eliminado exitosamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al eliminar docente: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}