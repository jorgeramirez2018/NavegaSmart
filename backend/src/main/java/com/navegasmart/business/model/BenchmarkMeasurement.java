package com.navegasmart.business.model;

/**
 * Resumen de la prueba de rendimiento de una estrategia sobre un grafo de cierto tamaño.
 */
public record BenchmarkMeasurement(String strategyKey,
                                   String strategyName,
                                   int nodeCount,
                                   int connectionCount,
                                   int repetitions,
                                   double averageMs,
                                   double minMs,
                                   double maxMs,
                                   int nodesExplored,
                                   int hops,
                                   double totalDistanceKm,
                                   double totalTimeMin) {
}
