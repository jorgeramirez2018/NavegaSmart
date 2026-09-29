package com.navegasmart.business.model;

/**
 * Arista dirigida que recorren los algoritmos. Una conexión bidireccional
 * genera dos aristas (ida y vuelta) dentro del {@link Graph}.
 */
public record Edge(Long fromId, Long toId, double distanceKm, double timeMin) {
}
