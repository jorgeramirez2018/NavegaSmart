package com.navegasmart.business.strategy;

import com.navegasmart.business.model.Graph;
import com.navegasmart.business.model.PathResult;

import java.util.Optional;

/**
 * Patrón Strategy: interfaz común para todos los criterios de navegación.
 *
 * El servicio de navegación (Contexto) solo conoce esta interfaz, por lo que
 * agregar un nuevo criterio consiste en crear una nueva clase que la implemente
 * y anotarla con @Component; no hay que modificar el servicio, los controladores
 * ni la interfaz web.
 */
public interface RouteStrategy {

    /** Identificador estable usado por la API, p. ej. "SHORTEST". */
    String key();

    /** Nombre legible para el usuario. */
    String displayName();

    /** Qué optimiza la estrategia. */
    String description();

    /**
     * Calcula la ruta entre origen y destino.
     *
     * @return la ruta encontrada, o vacío si el destino no es alcanzable.
     */
    Optional<PathResult> findRoute(Graph graph, Long originId, Long destinationId);
}
