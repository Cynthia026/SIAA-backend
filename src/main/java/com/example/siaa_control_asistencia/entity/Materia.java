package com.example.siaa_control_asistencia.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "materias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_materia")
    private Long idMateria;

    @Column(name = "nombre_materia", nullable = false, length = 100)
    private String nombreMateria;

    @Column(name = "codigo_materia", unique = true, length = 20)
    private String codigoMateria;

    @Column(name = "creditos")
    private Integer creditos;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    // CORREGIDO: Solo ignoramos las listas que causarían ciclos.
    // idDocente y especialidad SI se serializan para que el frontend pueda
    // seleccionar el docente correctamente en el formulario de edición.
    @ManyToOne
    @JoinColumn(name = "id_docente")
    @JsonIgnoreProperties({"asistencias", "materias"})
    private Docente docente;

    @OneToMany(mappedBy = "materia")
    @JsonIgnore
    private List<Asistencia> asistencias;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}
