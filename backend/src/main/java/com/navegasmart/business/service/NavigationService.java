package com.navegasmart.business.service;

import com.navegasmart.business.exception.NodeNotFoundException;
import com.navegasmart.business.exception.RouteNotFoundException;
import com.navegasmart.business.model.Graph;
import com.navegasmart.business.model.PathResult;
import com.navegasmart.business.model.RouteResult;
import com.navegasmart.business.strategy.RouteStrategy;
import com.navegasmart.business.strategy.StrategyRegistry;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Contexto del patrón Strategy.
 *
 * Recibe la solicitud, obtiene la estrategia elegida desde el registro y la ejecuta
 * sobre el grafo. No sabe CÓMO calcula cada estrategia; solo mide el tiempo,
 * arma el resultado y lo registra.
 */
@Service
public class NavigationService {

    private final GraphService graphService;
    private final StrategyRegistry strategyRegistry;
    private final ExecutionLogService executionLogService;

    public NavigationService(GraphService graphService,
                             StrategyRegistry strategyRegistry,
                             ExecutionLogService executionLogService) {
        this.graphService = graphService;
        this.strategyRegistry = strategyRegistry;
        this.executionLogService = executionLogService;
    }

    public List<RouteStrategy> availableStrategies() {
        return List.copyOf(strategyRegistry.all());
    }

    public RouteResult calculateRoute(Long originId, Long destinationId, String strategyKey) {
        RouteStrategy strategy = strategyRegistry.get(strategyKey);
        Graph graph = graphService.getGraph();
        validateEndpoints(graph, originId, destinationId);

        RouteResult result = execute(strategy, graph, originId, destinationId);
        executionLogService.logRoute(result, originId, destinationId, graph);
        return result;
    }

    /** Ejecuta todas las estrategias con el mismo origen y destino (prueba de estrategias). */
    public List<RouteResult> compareStrategies(Long originId, Long destinationId) {
        Graph graph = graphService.getGraph();
        validateEndpoints(graph, originId, destinationId);

        List<RouteResult> results = new ArrayList<>();
        for (RouteStrategy strategy : strategyRegistry.all()) {
            RouteResult result = execute(strategy, graph, originId, destinationId);
            executionLogService.logRoute(result, originId, destinationId, graph);
            results.add(result);
        }
        return results;
    }

    private RouteResult execute(RouteStrategy strategy, Graph graph, Long originId, Long destinationId) {
        long start = System.nanoTime();
        PathResult path = strategy.findRoute(graph, originId, destinationId)
                .orElseThrow(() -> new RouteNotFoundException(
                        graph.node(originId).name(), graph.node(destinationId).name()));
        double elapsedMs = (System.nanoTime() - start) / 1_000_000.0;
        return RouteResult.of(strategy.key(), strategy.displayName(), graph, path, elapsedMs);
    }

    private void validateEndpoints(Graph graph, Long originId, Long destinationId) {
        if (!graph.contains(originId)) {
            throw new NodeNotFoundException(originId);
        }
        if (!graph.contains(destinationId)) {
            throw new NodeNotFoundException(destinationId);
        }
        if (Objects.equals(originId, destinationId)) {
            throw new IllegalArgumentException("El origen y el destino deben ser puntos diferentes.");
        }
    }
}
