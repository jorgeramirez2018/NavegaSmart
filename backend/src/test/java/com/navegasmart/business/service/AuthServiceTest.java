package com.navegasmart.business.service;

import com.navegasmart.business.exception.InvalidCredentialsException;
import com.navegasmart.business.exception.UsernameTakenException;
import com.navegasmart.business.model.AuthenticatedUser;
import com.navegasmart.data.entity.UserEntity;
import com.navegasmart.data.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Prueba el registro y la verificación de credenciales sin base de datos (repositorio simulado).
 */
class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = new BCryptPasswordEncoder(4); // costo bajo: solo para que la prueba sea rápida
        authService = new AuthService(userRepository, passwordEncoder);
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void registerStoresHashedPasswordAndNormalizedUsername() {
        AuthenticatedUser user = authService.register("  Ana Pérez ", "  Ana.Perez ", "secreta123");

        ArgumentCaptor<UserEntity> saved = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(saved.capture());
        assertEquals("ana.perez", saved.getValue().getUsername());
        assertEquals("Ana Pérez", saved.getValue().getFullName());
        assertNotEquals("secreta123", saved.getValue().getPasswordHash());
        assertTrue(passwordEncoder.matches("secreta123", saved.getValue().getPasswordHash()));
        assertEquals("ana.perez", user.username());
    }

    @Test
    void registerRejectsExistingUsername() {
        when(userRepository.existsByUsername("ana")).thenReturn(true);

        assertThrows(UsernameTakenException.class, () -> authService.register("Ana", "ANA", "secreta123"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void authenticateAcceptsCorrectPassword() {
        storedUser("ana", "secreta123");

        AuthenticatedUser user = authService.authenticate("Ana", "secreta123");

        assertEquals("ana", user.username());
    }

    @Test
    void authenticateRejectsWrongPasswordAndUnknownUser() {
        storedUser("ana", "secreta123");

        assertThrows(InvalidCredentialsException.class, () -> authService.authenticate("ana", "otra-clave"));
        assertThrows(InvalidCredentialsException.class, () -> authService.authenticate("nadie", "secreta123"));
    }

    private void storedUser(String username, String rawPassword) {
        UserEntity entity = new UserEntity(username, "Ana", passwordEncoder.encode(rawPassword));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(entity));
    }
}
