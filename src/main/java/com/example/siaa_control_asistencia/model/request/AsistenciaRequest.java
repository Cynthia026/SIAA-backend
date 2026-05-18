package com.example.siaa_control_asistencia.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AsistenciaRequest {
    
    @NotNull(message = "El ID de la materia es obligatorio")
    private Long idMateria;
    
    @NotNull(message = "El ID del grupo es obligatorio")
    private Long idGrupo;
    
    @NotNull(message = "El ID del docente es obligatorio")
    private Long idDocente;
    
    @NotNull(message = "La fecha de clase es obligatoria")
    private LocalDate fechaClase;
    
    private String observaciones;
    
    @NotNull(message = "La lista de alumnos es obligatoria")
    private List<DetalleAlumno> alumnos;
    
    @Data
    public static class DetalleAlumno {
        @NotNull(message = "El ID del alumno es obligatorio")
        private Long idAlumno;
        
        @NotNull(message = "El estado de asistencia es obligatorio")
        private Boolean presente;
        
        private String comentario;
    }
}
