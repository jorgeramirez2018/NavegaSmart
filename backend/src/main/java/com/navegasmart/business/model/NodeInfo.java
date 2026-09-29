package com.navegasmart.business.model;

/**
 * Punto de la red de navegación (modelo de dominio, independiente de JPA).
 */
public record NodeInfo(Long id, String name, double x, double y) {
}
