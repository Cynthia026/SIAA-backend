package com.example.siaa_control_asistencia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Clase principal de la aplicación SIAA (Sistema Integral de Asistencias Académicas)
 * 
 * @author SIAA Development Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class SiaaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SiaaApplication.class, args);
        System.out.println("\n" +
            "╔═══════════════════════════════════════════════════════════╗\n" +
            "║                                                           ║\n" +
            "║              SIAA Backend API Started!                   ║\n" +
            "║    Sistema Integral de Asistencias Académicas            ║\n" +
            "║                                                           ║\n" +
            "║    API Base URL: http://localhost:8080/api               ║\n" +
            "║    Swagger UI: http://localhost:8080/api/swagger-ui.html ║\n" +
            "║                                                           ║\n" +
            "║    Status: ✅ RUNNING                                     ║\n" +
            "║                                                           ║\n" +
            "╚═══════════════════════════════════════════════════════════╝\n"
        );
    }
}
