package com.example.siaa_control_asistencia.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "docentes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Docente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_docente")
    private Long idDocente;

    // CORREGIDO: Ignoramos solo los campos que causan ciclos o son sensibles.
    // Se expone idUsuario, nombreCompleto, matricula, email, rol, estado
    // para que el admin pueda ver información del docente.
    @OneToOne
    @JoinColumn(name = "id_usuario", unique = true, nullable = false)
    @JsonIgnoreProperties({"alumno", "docente", "passwordHash", "password",
                           "fechaRegistro", "ultimaActualizacion", "fechaBaja"})
    private Usuario usuario;

    @Column(name = "especialidad", length = 100)
    private String especialidad;

    @Column(name = "titulo", length = 100)
    private String titulo;

    @OneToMany(mappedBy = "docente")
    @JsonIgnore
    private List<Asistencia> asistencias;
}
