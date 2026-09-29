package com.navegasmart.business.model;

import java.util.List;

/**
 * Resultado completo que entrega la capa de negocio: ruta, métricas y tiempo de ejecución.
 */
public record RouteResult(String strategyKey,
                          String strategyName,
                          List<NodeInfo> path,
                          double totalDistanceKm,
                          double totalTimeMin,
                          int hops,
                          int nodesExplored,
                          double executionTimeMs) {

    public static RouteResult of(String strategyKey,
                                 String strategyName,
                                 Graph graph,
                                 PathResult pathResult,
                                 double executionTimeMs) {
        List<NodeInfo> path = pathResult.nodeIds().stream().map(graph::node).toList();
        return new RouteResult(
                strategyKey,
                strategyName,
                path,
                pathResult.totalDistanceKm(),
                pathResult.totalTimeMin(),
                pathResult.hops(),
                pathResult.nodesExplored(),
                executionTimeMs);
    }
}
