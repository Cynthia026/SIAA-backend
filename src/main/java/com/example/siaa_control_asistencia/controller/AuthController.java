package com.example.siaa_control_asistencia.controller;

import com.example.siaa_control_asistencia.entity.Usuario;
import com.example.siaa_control_asistencia.model.response.JwtResponse;
import com.example.siaa_control_asistencia.model.request.LoginRequest;
import com.example.siaa_control_asistencia.repository.UsuarioRepository;
import com.example.siaa_control_asistencia.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de Autenticación
 * Maneja login y registro de usuarios con JWT
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Autenticar usuario
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getMatricula(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generar token JWT
            String token = tokenProvider.generateTokenFromAuthentication(authentication);

            // Buscar usuario
            Usuario usuario = usuarioRepository.findByMatricula(loginRequest.getMatricula())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Crear response JWT con los datos del usuario
            JwtResponse response = new JwtResponse(
                    token,
                    usuario.getIdUsuario(),
                    usuario.getMatricula(),
                    usuario.getNombreCompleto(),
                    usuario.getEmail(),
                    usuario.getRol(),      // String directo
                    usuario.getEstado()    // String directo
            );

            // Retornar respuesta con token
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error en autenticación: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody Usuario usuario) {
        // Verificar si la matrícula ya existe
        if (usuarioRepository.existsByMatricula(usuario.getMatricula())) {
            return ResponseEntity.badRequest()
                    .body("Error: La matrícula ya está registrada");
        }

        // Verificar si el email ya existe (solo si viene)
        if (usuario.getEmail() != null && !usuario.getEmail().isEmpty() &&
                usuarioRepository.existsByEmail(usuario.getEmail())) {
            return ResponseEntity.badRequest()
                    .body("Error: El email ya está registrado");
        }

        // Encriptar contraseña
        usuario.setPasswordHash(passwordEncoder.encode(usuario.getPasswordHash()));

        // Establecer estado activo por defecto (String, no ENUM)
        if (usuario.getEstado() == null || usuario.getEstado().isEmpty()) {
            usuario.setEstado("ACTIVO");
        }

        // Guardar usuario
        Usuario nuevoUsuario = usuarioRepository.save(usuario);

        return ResponseEntity.ok()
                .body("Usuario registrado exitosamente con ID: " + nuevoUsuario.getIdUsuario());
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String bearerToken) {
        try {
            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                String token = bearerToken.substring(7);

                if (tokenProvider.validateToken(token)) {
                    String matricula = tokenProvider.getMatriculaFromToken(token);
                    String newToken = tokenProvider.generateToken(matricula);

                    Usuario usuario = usuarioRepository.findByMatricula(matricula)
                            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                    // Crear response JWT con los datos del usuario
                    JwtResponse response = new JwtResponse(
                            newToken,
                            usuario.getIdUsuario(),
                            usuario.getMatricula(),
                            usuario.getNombreCompleto(),
                            usuario.getEmail(),
                            usuario.getRol(),      // String directo
                            usuario.getEstado()    // String directo
                    );

                    return ResponseEntity.ok(response);
                }
            }
            return ResponseEntity.badRequest().body("Token inválido");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al renovar token: " + e.getMessage());
        }
    }
}