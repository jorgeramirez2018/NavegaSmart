package com.navegasmart.presentation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BenchmarkRequest(
        @NotEmpty(message = "Indica al menos un tamaño de grafo")
        @Size(max = 8, message = "Máximo 8 tamaños por prueba")
        List<@NotNull @Min(value = 10, message = "Cada grafo debe tener al menos 10 nodos")
             @Max(value = 200000, message = "Cada grafo puede tener máximo 200000 nodos") Integer> sizes,

        @Min(value = 1, message = "Mínimo 1 repetición")
        @Max(value = 50, message = "Máximo 50 repeticiones")
        Integer repetitions,

        @Min(value = 0, message = "El calentamiento no puede ser negativo")
        @Max(value = 20, message = "Máximo 20 ejecuciones de calentamiento")
        Integer warmupRuns) {

    public int repetitionsOrDefault() {
        return repetitions == null ? 10 : repetitions;
    }

    public int warmupRunsOrDefault() {
        return warmupRuns == null ? 3 : warmupRuns;
    }
}
