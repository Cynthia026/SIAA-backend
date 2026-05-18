package com.example.siaa_control_asistencia.service;

import com.example.siaa_control_asistencia.entity.*;
import com.example.siaa_control_asistencia.model.request.AsistenciaRequest;
import com.example.siaa_control_asistencia.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final MateriaRepository materiaRepository;
    private final GrupoRepository grupoRepository;
    private final DocenteRepository docenteRepository;
    private final AlumnoRepository alumnoRepository;

    public List<Asistencia> obtenerTodas() {
        return asistenciaRepository.findAll();
    }

    public Asistencia obtenerPorId(Long id) {
        return asistenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asistencia no encontrada con ID: " + id));
    }

    public List<Asistencia> obtenerPorGrupo(Long idGrupo) {
        return asistenciaRepository.findByGrupo_IdGrupo(idGrupo);
    }

    public List<Asistencia> obtenerPorMateria(Long idMateria) {
        return asistenciaRepository.findByMateria_IdMateria(idMateria);
    }

    public List<Asistencia> obtenerPorDocente(Long idDocente) {
        return asistenciaRepository.findByDocente_IdDocente(idDocente);
    }

    @Transactional
    public Asistencia registrarConDTO(AsistenciaRequest request) {
        Materia materia = materiaRepository.findById(request.getIdMateria())
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

        Grupo grupo = grupoRepository.findById(request.getIdGrupo())
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        Docente docente = docenteRepository.findById(request.getIdDocente())
                .orElseThrow(() -> new RuntimeException("Docente no encontrado"));

        Asistencia asistencia = new Asistencia();
        asistencia.setMateria(materia);
        asistencia.setGrupo(grupo);
        asistencia.setDocente(docente);
        asistencia.setFechaClase(request.getFechaClase());
        asistencia.setObservaciones(request.getObservaciones());
        asistencia.setDetalles(new ArrayList<>());

        int presentes = 0;
        int ausentes = 0;

        if (request.getAlumnos() != null && !request.getAlumnos().isEmpty()) {
            for (AsistenciaRequest.DetalleAlumno detalleDTO : request.getAlumnos()) {
                Alumno alumno = alumnoRepository.findById(detalleDTO.getIdAlumno())
                        .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

                DetalleAsistencia detalle = new DetalleAsistencia();
                detalle.setAsistencia(asistencia);
                detalle.setAlumno(alumno);
                detalle.setPresente(detalleDTO.getPresente());
                detalle.setComentario(detalleDTO.getComentario());

                asistencia.getDetalles().add(detalle);

                if (detalleDTO.getPresente()) {
                    presentes++;
                } else {
                    ausentes++;
                }
            }
        }

        asistencia.setTotalPresentes(presentes);
        asistencia.setTotalAusentes(ausentes);

        return asistenciaRepository.save(asistencia);
    }

    @Transactional
    public void eliminar(Long id) {
        Asistencia asistencia = obtenerPorId(id);
        asistenciaRepository.delete(asistencia);
    }
}