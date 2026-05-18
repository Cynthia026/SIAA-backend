package com.example.siaa_control_asistencia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Clase principal - SIAA Control de Asistencias
 * Sistema Integral de Asistencias Académicas
 */
@SpringBootApplication
@EnableJpaAuditing
public class SiaaControlAsistenciaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SiaaControlAsistenciaApplication.class, args);
        System.out.println("\n" +
                "╔══════════════════════════W═════════════════════════════════╗\n" +
                "║                                                           ║\n" +
                "║         SIAA - Control de Asistencias Started!           ║\n" +
                "║    Sistema Integral de Asistencias Académicas            ║\n" +
                "║                                                           ║\n" +
                "║    API Base URL: http://localhost:8081/api               ║\n" +
                "║                                                           ║\n" +
                "║    Status: ✅ RUNNING                                     ║\n" +
                "║                                                           ║\n" +
                "╚═══════════════════════════════════════════════════════════╝\n"
        );
    }
}
