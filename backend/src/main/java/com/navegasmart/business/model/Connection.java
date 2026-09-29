package com.navegasmart.business.model;

/**
 * Conexión tal como está definida en la red (se usa para dibujar el mapa).
 */
public record Connection(Long fromId,
                         Long toId,
                         double distanceKm,
                         double timeMin,
                         String roadType,
                         boolean bidirectional) {
}
