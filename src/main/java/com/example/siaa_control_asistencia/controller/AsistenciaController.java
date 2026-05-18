package com.example.siaa_control_asistencia.controller;

import com.example.siaa_control_asistencia.entity.Asistencia;
import com.example.siaa_control_asistencia.model.request.AsistenciaRequest;
import com.example.siaa_control_asistencia.service.AsistenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/asistencias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    /**
     * GET /asistencias - Obtener todas las asistencias
     */
    @GetMapping
    @Transactional(readOnly = true)  // ← AGREGAR ESTA LÍNEA
    public ResponseEntity<List<Asistencia>> obtenerTodas() {
        List<Asistencia> asistencias = asistenciaService.obtenerTodas();
        return ResponseEntity.ok(asistencias);
    }

    /**
     * GET /asistencias/{id} - Obtener asistencia por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Asistencia> obtenerPorId(@PathVariable Long id) {
        try {
            Asistencia asistencia = asistenciaService.obtenerPorId(id);
            return ResponseEntity.ok(asistencia);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /asistencias - Registrar nueva asistencia (CON DTO)
     */
    @PostMapping
    public ResponseEntity<?> registrar(@Valid @RequestBody AsistenciaRequest request) {
        try {
            Asistencia nuevaAsistencia = asistenciaService.registrarConDTO(request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Asistencia registrada correctamente");
            response.put("asistencia", nuevaAsistencia);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * GET /asistencias/grupo/{idGrupo} - Obtener asistencias por grupo
     */
    @GetMapping("/grupo/{idGrupo}")
    public ResponseEntity<List<Asistencia>> obtenerPorGrupo(@PathVariable Long idGrupo) {
        List<Asistencia> asistencias = asistenciaService.obtenerPorGrupo(idGrupo);
        return ResponseEntity.ok(asistencias);
    }

    /**
     * GET /asistencias/materia/{idMateria} - Obtener asistencias por materia
     */
    @GetMapping("/materia/{idMateria}")
    public ResponseEntity<List<Asistencia>> obtenerPorMateria(@PathVariable Long idMateria) {
        List<Asistencia> asistencias = asistenciaService.obtenerPorMateria(idMateria);
        return ResponseEntity.ok(asistencias);
    }

    /**
     * GET /asistencias/docente/{idDocente} - Obtener asistencias por docente
     */
    @GetMapping("/docente/{idDocente}")
    public ResponseEntity<List<Asistencia>> obtenerPorDocente(@PathVariable Long idDocente) {
        List<Asistencia> asistencias = asistenciaService.obtenerPorDocente(idDocente);
        return ResponseEntity.ok(asistencias);
    }

    /**
     * DELETE /asistencias/{id} - Eliminar asistencia
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            asistenciaService.eliminar(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Asistencia eliminada exitosamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}