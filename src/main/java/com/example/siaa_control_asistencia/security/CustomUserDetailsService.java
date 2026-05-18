package com.example.siaa_control_asistencia.security;

import com.example.siaa_control_asistencia.entity.Usuario;
import com.example.siaa_control_asistencia.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de UserDetailsService para Spring Security
 * Carga los datos del usuario desde la base de datos
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String matricula) throws UsernameNotFoundException {

        // Buscar usuario por matrícula
        Usuario usuario = usuarioRepository.findByMatricula(matricula)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado con matrícula: " + matricula)
                );

        // Verificar que el usuario esté activo (comparación con String)
        if (!"ACTIVO".equals(usuario.getEstado())) {
            throw new UsernameNotFoundException("Usuario dado de baja: " + matricula);
        }

        // Crear lista de autoridades (roles)
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toUpperCase()));

        // Retornar UserDetails de Spring Security
        return User.builder()
                .username(usuario.getMatricula())
                .password(usuario.getPasswordHash())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!"ACTIVO".equals(usuario.getEstado()))
                .build();
    }
}