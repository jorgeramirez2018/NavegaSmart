package com.navegasmart.business.model;

import java.time.LocalDateTime;

/**
 * Vista de negocio de un registro de ejecución (evita exponer entidades JPA a la capa de presentación).
 */
public record ExecutionRecord(Long id,
                              String runType,
                              String strategyKey,
                              Long originId,
                              Long destinationId,
                              int nodeCount,
                              int edgeCount,
                              Integer hops,
                              Integer nodesExplored,
                              Double totalDistanceKm,
                              Double totalTimeMin,
                              double executionTimeMs,
                              LocalDateTime createdAt) {
}
