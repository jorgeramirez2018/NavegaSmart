package com.navegasmart.business.service;

import com.navegasmart.business.exception.InvalidCredentialsException;
import com.navegasmart.business.exception.UsernameTakenException;
import com.navegasmart.business.model.AuthenticatedUser;
import com.navegasmart.data.entity.UserEntity;
import com.navegasmart.data.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/**
 * Registro de usuarios y verificación de credenciales.
 * La emisión del token de sesión es responsabilidad de la capa de presentación.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthenticatedUser register(String fullName, String username, String rawPassword) {
        String normalized = normalize(username);
        if (userRepository.existsByUsername(normalized)) {
            throw new UsernameTakenException(normalized);
        }
        UserEntity saved = userRepository.save(
                new UserEntity(normalized, fullName.trim(), passwordEncoder.encode(rawPassword)));
        return toModel(saved);
    }

    @Transactional(readOnly = true)
    public AuthenticatedUser authenticate(String username, String rawPassword) {
        return userRepository.findByUsername(normalize(username))
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPasswordHash()))
                .map(AuthService::toModel)
                .orElseThrow(InvalidCredentialsException::new);
    }

    @Transactional(readOnly = true)
    public boolean hasUsers() {
        return userRepository.count() > 0;
    }

    /** Los nombres de usuario no distinguen mayúsculas ni espacios al inicio o al final. */
    private static String normalize(String username) {
        return username.trim().toLowerCase(Locale.ROOT);
    }

    private static AuthenticatedUser toModel(UserEntity entity) {
        return new AuthenticatedUser(entity.getId(), entity.getUsername(), entity.getFullName());
    }
}
