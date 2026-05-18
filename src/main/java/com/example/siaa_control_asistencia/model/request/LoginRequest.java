package com.example.siaa_control_asistencia.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    
    @NotBlank(message = "La matrícula es obligatoria")
    private String matricula;
    
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
