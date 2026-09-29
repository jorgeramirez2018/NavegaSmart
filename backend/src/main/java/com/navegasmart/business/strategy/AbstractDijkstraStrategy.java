package com.navegasmart.business.strategy;

import com.navegasmart.business.model.Edge;
import com.navegasmart.business.model.Graph;
import com.navegasmart.business.model.PathResult;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Patrón Template Method (complementa a Strategy): el algoritmo de Dijkstra
 * se escribe una sola vez y cada subclase define únicamente el costo de una
 * arista (distancia, tiempo, ...).
 *
 * Complejidad: O((V + E) log V) usando una cola de prioridad.
 */
public abstract class AbstractDijkstraStrategy implements RouteStrategy {

    /** Paso variable del Template Method: costo de recorrer una arista (debe ser >= 0). */
    protected abstract double cost(Edge edge);

    @Override
    public final Optional<PathResult> findRoute(Graph graph, Long originId, Long destinationId) {
        Map<Long, Double> bestCost = new HashMap<>();
        Map<Long, Edge> previousEdge = new HashMap<>();
        Set<Long> settled = new HashSet<>();
        PriorityQueue<QueueEntry> queue = new PriorityQueue<>(Comparator.comparingDouble(QueueEntry::cost));

        bestCost.put(originId, 0.0);
        queue.add(new QueueEntry(originId, 0.0));
        int nodesExplored = 0;

        while (!queue.isEmpty()) {
            QueueEntry current = queue.poll();
            if (!settled.add(current.nodeId())) {
                continue; // entrada obsoleta: el nodo ya se cerró con un costo menor
            }
            nodesExplored++;

            if (current.nodeId().equals(destinationId)) {
                return Optional.of(PathResult.fromPredecessors(originId, destinationId, previousEdge, nodesExplored));
            }

            for (Edge edge : graph.neighbors(current.nodeId())) {
                if (settled.contains(edge.toId())) {
                    continue;
                }
                double candidate = current.cost() + cost(edge);
                if (candidate < bestCost.getOrDefault(edge.toId(), Double.POSITIVE_INFINITY)) {
                    bestCost.put(edge.toId(), candidate);
                    previousEdge.put(edge.toId(), edge);
                    queue.add(new QueueEntry(edge.toId(), candidate));
                }
            }
        }
        return Optional.empty();
    }

    private record QueueEntry(Long nodeId, double cost) {
    }
}
