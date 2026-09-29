package com.navegasmart.business.model;

/**
 * Vista de negocio de un usuario autenticado (nunca incluye la contraseña ni su hash).
 */
public record AuthenticatedUser(Long id, String username, String fullName) {
}
