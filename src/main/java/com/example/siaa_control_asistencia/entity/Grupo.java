package com.example.siaa_control_asistencia.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "grupos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_grupo")
    private Long idGrupo;

    @Column(name = "nombre_grupo", nullable = false, length = 50)
    private String nombreGrupo;

    @Column(name = "descripcion", length = 200)
    private String descripcion;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "grupo")
    @JsonIgnore  // ← CRÍTICO: No serializar lista de asistencias
    private List<Asistencia> asistencias;

    @OneToMany(mappedBy = "grupo")
    @JsonIgnore  // ← CRÍTICO: No serializar lista de alumnos
    private List<Alumno> alumnos;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}