package com.navegasmart.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RouteRequest(
        @NotNull(message = "Selecciona un punto de origen") Long originId,
        @NotNull(message = "Selecciona un punto de destino") Long destinationId,
        @NotBlank(message = "Selecciona una estrategia") String strategy) {
}
