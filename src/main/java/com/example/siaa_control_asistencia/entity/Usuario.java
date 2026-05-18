package com.example.siaa_control_asistencia.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Size(max = 20)
    @Column(unique = true, nullable = false, length = 20)
    private String matricula;

    @Size(max = 100)
    @Column(name = "nombre_completo", nullable = false, length = 100)
    private String nombreCompleto;

    @Email(message = "Email debe ser válido")
    @Size(max = 100)
    @Column(unique = true, length = 100)
    private String email;

    @JsonIgnore
    @Size(max = 255)
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 20)
    private String rol;  // "Alumno", "Docente", "Administrador"

    @Column(nullable = false, length = 20)
    private String estado;  // "ACTIVO", "INACTIVO"

    @Column(name = "foto_perfil", columnDefinition = "TEXT")
    private String fotoPerfil;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_baja")
    private LocalDateTime fechaBaja;

    @Column(name = "ultima_actualizacion")
    private LocalDateTime ultimaActualizacion;

    @JsonIgnore
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Alumno alumno;

    @JsonIgnore
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Docente docente;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
        ultimaActualizacion = LocalDateTime.now();
        if (estado == null || estado.isEmpty()) {
            estado = "ACTIVO";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        ultimaActualizacion = LocalDateTime.now();
    }

    // ===================================
    // MÉTODOS DE COMPATIBILIDAD
    // ===================================

    /**
     * Getter de password que mapea a passwordHash
     */
    public String getPassword() {
        return this.passwordHash;
    }

    /**
     * Setter de password que mapea a passwordHash
     */
    public void setPassword(String password) {
        this.passwordHash = password;
    }
}