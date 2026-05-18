package com.example.siaa_control_asistencia.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "alumnos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Alumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alumno")
    private Long idAlumno;

    // CORREGIDO: Cambiamos @JsonIgnore por @JsonIgnoreProperties para que se
    // serialice idUsuario, nombreCompleto, matricula, etc. (sin datos sensibles)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonIgnoreProperties({"alumno", "docente", "passwordHash", "password",
                           "fechaRegistro", "ultimaActualizacion", "fechaBaja"})
    private Usuario usuario;

    @Column(name = "numero_control", length = 20)
    private String numeroControl;

    @Column(name = "semestre")
    private Integer semestre;

    @ManyToOne
    @JoinColumn(name = "grupo_id")
    @JsonIgnoreProperties({"asistencias", "alumnos"})
    private Grupo grupo;

    @OneToMany(mappedBy = "alumno")
    @JsonIgnore
    private List<DetalleAsistencia> detalles;

    // Métodos de conveniencia para el frontend
    @JsonProperty("nombreCompleto")
    public String getNombreCompleto() {
        return usuario != null ? usuario.getNombreCompleto() : null;
    }

    @JsonProperty("matricula")
    public String getMatricula() {
        return usuario != null ? usuario.getMatricula() : numeroControl;
    }

    // NUEVO: Exponer idUsuario directamente para que el frontend pueda
    // hacer operaciones sobre el alumno usando el id del usuario
    @JsonProperty("idUsuario")
    public Long getIdUsuario() {
        return usuario != null ? usuario.getIdUsuario() : null;
    }
}
