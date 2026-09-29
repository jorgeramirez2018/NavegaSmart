package com.navegasmart.presentation.dto;

import java.time.Instant;

/**
 * Respuesta de inicio de sesión o registro: el token se envía luego en la cabecera
 * {@code Authorization: Bearer <token>} de cada solicitud.
 */
public record AuthResponse(String token, Instant expiresAt, UserDto user) {
}
