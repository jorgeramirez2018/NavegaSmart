package com.navegasmart.config;

import com.navegasmart.business.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Crea un usuario de demostración cuando la tabla de usuarios está vacía,
 * para poder entrar a la aplicación sin registrarse primero.
 */
@Component
@ConditionalOnProperty(name = "navegasmart.auth.demo-user.enabled", havingValue = "true")
public class DemoUserInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoUserInitializer.class);

    private final AuthService authService;
    private final String username;
    private final String password;

    public DemoUserInitializer(AuthService authService,
                               @Value("${navegasmart.auth.demo-user.username}") String username,
                               @Value("${navegasmart.auth.demo-user.password}") String password) {
        this.authService = authService;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!authService.hasUsers()) {
            authService.register("Usuario de demostración", username, password);
            log.info("Usuario de demostración creado: {}", username);
        }
    }
}
