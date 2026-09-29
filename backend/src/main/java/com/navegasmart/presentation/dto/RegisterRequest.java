package com.navegasmart.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Escribe tu nombre")
        @Size(max = 100, message = "El nombre admite máximo 100 caracteres")
        String fullName,

        @NotBlank(message = "Escribe un usuario")
        @Pattern(regexp = "^\\s*[A-Za-z0-9._-]{3,40}\\s*$",
                message = "El usuario debe tener de 3 a 40 caracteres: letras, números, punto, guion o guion bajo")
        String username,

        @NotBlank(message = "Escribe una contraseña")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
        String password) {
}
