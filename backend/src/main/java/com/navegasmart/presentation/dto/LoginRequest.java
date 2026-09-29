package com.navegasmart.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Escribe tu usuario") String username,
        @NotBlank(message = "Escribe tu contraseña") String password) {
}
