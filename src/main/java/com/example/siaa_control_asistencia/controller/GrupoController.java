package com.example.siaa_control_asistencia.controller;

import com.example.siaa_control_asistencia.entity.Grupo;
import com.example.siaa_control_asistencia.repository.GrupoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/grupos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class GrupoController {

    private final GrupoRepository grupoRepository;

    /**
     * GET /grupos - Obtener todos los grupos
     */
    @GetMapping
    public ResponseEntity<List<Grupo>> obtenerTodos() {
        List<Grupo> grupos = grupoRepository.findAll();
        return ResponseEntity.ok(grupos);
    }

    /**
     * GET /grupos/{id} - Obtener grupo por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado con ID: " + id));
            return ResponseEntity.ok(grupo);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * POST /grupos - Crear nuevo grupo
     */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> grupoData) {
        try {
            // Validar nombre obligatorio
            if (!grupoData.containsKey("nombreGrupo") ||
                    grupoData.get("nombreGrupo") == null ||
                    grupoData.get("nombreGrupo").toString().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "El nombre del grupo es obligatorio");
                return ResponseEntity.badRequest().body(error);
            }

            String nombreGrupo = grupoData.get("nombreGrupo").toString().trim();

            // Verificar que el nombre no exista
            if (grupoRepository.existsByNombreGrupo(nombreGrupo)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Ya existe un grupo con ese nombre");
                return ResponseEntity.badRequest().body(error);
            }

            Grupo grupo = new Grupo();
            grupo.setNombreGrupo(nombreGrupo);

            if (grupoData.containsKey("descripcion") && grupoData.get("descripcion") != null) {
                grupo.setDescripcion(grupoData.get("descripcion").toString());
            }

            Grupo grupoGuardado = grupoRepository.save(grupo);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Grupo creado exitosamente");
            response.put("grupo", grupoGuardado);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al crear grupo: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * PUT /grupos/{id} - Actualizar grupo
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {

        try {
            Grupo grupo = grupoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Grupo no encontrado con ID: " + id));

            if (updates.containsKey("nombreGrupo") && updates.get("nombreGrupo") != null) {
                String nuevoNombre = updates.get("nombreGrupo").toString().trim();

                // Verificar que el nuevo nombre no esté en uso por otro grupo
                grupoRepository.findByNombreGrupo(nuevoNombre).ifPresent(g -> {
                    if (!g.getIdGrupo().equals(id)) {
                        throw new RuntimeException("Ya existe un grupo con ese nombre");
                    }
                });

                grupo.setNombreGrupo(nuevoNombre);
            }

            if (updates.containsKey("descripcion")) {
                Object desc = updates.get("descripcion");
                grupo.setDescripcion(desc != null ? desc.toString() : null);
            }

            Grupo grupoActualizado = grupoRepository.save(grupo);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Grupo actualizado exitosamente");
            response.put("grupo", grupoActualizado);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al actualizar grupo: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * DELETE /grupos/{id} - Eliminar grupo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            Grupo grupo = grupoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Grupo no encontrado con ID: " + id));

            grupoRepository.delete(grupo);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Grupo eliminado exitosamente");

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al eliminar grupo: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
