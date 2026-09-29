package com.navegasmart.presentation.dto;

import jakarta.validation.constraints.NotNull;

public record CompareRequest(
        @NotNull(message = "Selecciona un punto de origen") Long originId,
        @NotNull(message = "Selecciona un punto de destino") Long destinationId) {
}
