package com.example.siaa_control_asistencia.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {

    private String token;
    private String type = "Bearer";
    private Long idUsuario;
    private String matricula;
    private String nombreCompleto;
    private String email;
    private String rol;  // String en lugar de Usuario.Rol
    private String estado;  // String en lugar de Usuario.Estado

    // Constructor sin tipo (Bearer por defecto)
    public JwtResponse(String token, Long idUsuario, String matricula, String nombreCompleto,
                       String email, String rol, String estado) {
        this.token = token;
        this.type = "Bearer";
        this.idUsuario = idUsuario;
        this.matricula = matricula;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.rol = rol;
        this.estado = estado;
    }
}