package com.navegasmart.business.service;

import com.navegasmart.business.model.BenchmarkMeasurement;
import com.navegasmart.business.model.Graph;
import com.navegasmart.business.model.PathResult;
import com.navegasmart.business.strategy.RouteStrategy;
import com.navegasmart.business.strategy.StrategyRegistry;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Pruebas de rendimiento y escalabilidad: mide cada estrategia sobre grafos
 * sintéticos de distintos tamaños (tiempo vs. número de nodos).
 *
 * Se hacen algunas ejecuciones de calentamiento antes de medir, porque la JVM
 * optimiza el código (JIT) durante las primeras ejecuciones y eso distorsiona los tiempos.
 */
@Service
public class BenchmarkService {

    static final long SEED = 42L;

    private final StrategyRegistry strategyRegistry;
    private final ExecutionLogService executionLogService;

    public BenchmarkService(StrategyRegistry strategyRegistry, ExecutionLogService executionLogService) {
        this.strategyRegistry = strategyRegistry;
        this.executionLogService = executionLogService;
    }

    public List<BenchmarkMeasurement> run(List<Integer> sizes, int repetitions, int warmupRuns) {
        List<BenchmarkMeasurement> measurements = new ArrayList<>();
        for (int size : sizes.stream().distinct().sorted().toList()) {
            Graph graph = SyntheticGraphGenerator.generate(size, SEED);
            Long originId = 0L;                   // esquina superior izquierda
            Long destinationId = (long) size - 1; // esquina opuesta: fuerza recorridos largos

            for (RouteStrategy strategy : strategyRegistry.all()) {
                BenchmarkMeasurement measurement = measure(strategy, graph, originId, destinationId, repetitions, warmupRuns);
                executionLogService.logBenchmark(measurement);
                measurements.add(measurement);
            }
        }
        return measurements;
    }

    private BenchmarkMeasurement measure(RouteStrategy strategy, Graph graph, Long originId, Long destinationId,
                                         int repetitions, int warmupRuns) {
        for (int i = 0; i < warmupRuns; i++) {
            strategy.findRoute(graph, originId, destinationId);
        }

        double totalMs = 0;
        double minMs = Double.MAX_VALUE;
        double maxMs = 0;
        PathResult path = null;
        for (int i = 0; i < repetitions; i++) {
            long start = System.nanoTime();
            path = strategy.findRoute(graph, originId, destinationId)
                    .orElseThrow(() -> new IllegalStateException("El grafo sintético debería ser conexo."));
            double elapsedMs = (System.nanoTime() - start) / 1_000_000.0;
            totalMs += elapsedMs;
            minMs = Math.min(minMs, elapsedMs);
            maxMs = Math.max(maxMs, elapsedMs);
        }

        return new BenchmarkMeasurement(
                strategy.key(),
                strategy.displayName(),
                graph.nodeCount(),
                graph.connectionCount(),
                repetitions,
                totalMs / repetitions,
                minMs,
                maxMs,
                path.nodesExplored(),
                path.hops(),
                path.totalDistanceKm(),
                path.totalTimeMin());
    }
}
