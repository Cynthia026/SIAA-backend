package com.example.siaa_control_asistencia.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalle_asistencias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleAsistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Long idDetalle;

    @ManyToOne
    @JoinColumn(name = "id_asistencia", nullable = false)
    @JsonIgnore  // ← CRÍTICO: Evita loop infinito
    private Asistencia asistencia;

    @ManyToOne
    @JoinColumn(name = "id_alumno", nullable = false)
    private Alumno alumno;

    @Column(nullable = false)
    private Boolean presente;

    @Column(length = 500)
    private String comentario;
}