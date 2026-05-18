package com.example.siaa_control_asistencia.controller;

import com.example.siaa_control_asistencia.entity.Docente;
import com.example.siaa_control_asistencia.entity.Materia;
import com.example.siaa_control_asistencia.repository.DocenteRepository;
import com.example.siaa_control_asistencia.repository.MateriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/materias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class MateriaController {

    private final MateriaRepository materiaRepository;
    private final DocenteRepository docenteRepository;

    /**
     * GET /materias - Obtener todas las materias (admin)
     */
    @GetMapping
    public ResponseEntity<List<Materia>> obtenerTodas() {
        List<Materia> materias = materiaRepository.findAll();
        return ResponseEntity.ok(materias);
    }

    /**
     * GET /materias/{id} - Obtener materia por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            Materia materia = materiaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Materia no encontrada"));
            return ResponseEntity.ok(materia);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * GET /materias/docente/{idUsuario}
     * Si no existe registro de docente, devuelve TODAS las materias
     * como fallback en lugar de lanzar 404.
     */
    @GetMapping("/docente/{idUsuario}")
    public ResponseEntity<?> obtenerPorDocente(@PathVariable Long idUsuario) {
        try {
            Optional<Docente> docenteOpt = docenteRepository.findByUsuario_IdUsuario(idUsuario);

            List<Materia> materias;
            if (docenteOpt.isPresent()) {
                materias = materiaRepository.findByDocente_IdDocente(docenteOpt.get().getIdDocente());
            } else {
                // Fallback: sin registro de docente → devolver todas
                materias = materiaRepository.findAll();
            }

            return ResponseEntity.ok(materias);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener materias del docente: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * POST /materias - Crear nueva materia
     */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> materiaData) {
        try {
            if (!materiaData.containsKey("nombreMateria") ||
                    materiaData.get("nombreMateria") == null ||
                    materiaData.get("nombreMateria").toString().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "El nombre de la materia es obligatorio");
                return ResponseEntity.badRequest().body(error);
            }

            Materia materia = new Materia();
            materia.setNombreMateria((String) materiaData.get("nombreMateria"));

            if (materiaData.containsKey("idDocente") && materiaData.get("idDocente") != null) {
                Long idDocente = Long.valueOf(materiaData.get("idDocente").toString());
                Docente docente = docenteRepository.findById(idDocente)
                        .orElseThrow(() -> new RuntimeException("Docente no encontrado"));
                materia.setDocente(docente);
            }

            if (materiaData.containsKey("codigoMateria")) {
                materia.setCodigoMateria((String) materiaData.get("codigoMateria"));
            }

            if (materiaData.containsKey("creditos")) {
                materia.setCreditos(Integer.valueOf(materiaData.get("creditos").toString()));
            }

            if (materiaData.containsKey("descripcion")) {
                materia.setDescripcion((String) materiaData.get("descripcion"));
            }

            Materia materiaGuardada = materiaRepository.save(materia);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Materia creada exitosamente");
            response.put("materia", materiaGuardada);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al crear materia: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * PUT /materias/{id} - Actualizar materia
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {

        try {
            Materia materia = materiaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

            if (updates.containsKey("nombreMateria")) {
                materia.setNombreMateria((String) updates.get("nombreMateria"));
            }

            if (updates.containsKey("idDocente")) {
                Object idDocenteObj = updates.get("idDocente");
                if (idDocenteObj != null && !idDocenteObj.toString().isEmpty()) {
                    Long idDocente = Long.valueOf(idDocenteObj.toString());
                    Docente docente = docenteRepository.findById(idDocente)
                            .orElseThrow(() -> new RuntimeException("Docente no encontrado"));
                    materia.setDocente(docente);
                } else {
                    materia.setDocente(null);
                }
            }

            if (updates.containsKey("codigoMateria")) {
                Object codigo = updates.get("codigoMateria");
                materia.setCodigoMateria(codigo != null ? codigo.toString() : null);
            }

            if (updates.containsKey("creditos")) {
                Object creditos = updates.get("creditos");
                materia.setCreditos(creditos != null ? Integer.valueOf(creditos.toString()) : null);
            }

            if (updates.containsKey("descripcion")) {
                Object desc = updates.get("descripcion");
                materia.setDescripcion(desc != null ? desc.toString() : null);
            }

            Materia materiaActualizada = materiaRepository.save(materia);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Materia actualizada exitosamente");
            response.put("materia", materiaActualizada);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al actualizar materia: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * DELETE /materias/{id} - Eliminar materia
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            Materia materia = materiaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

            materiaRepository.delete(materia);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Materia eliminada exitosamente");

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al eliminar materia: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
