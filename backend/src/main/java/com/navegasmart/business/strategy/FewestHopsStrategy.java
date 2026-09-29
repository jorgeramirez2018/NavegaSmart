package com.navegasmart.business.strategy;

import com.navegasmart.business.model.Edge;
import com.navegasmart.business.model.Graph;
import com.navegasmart.business.model.PathResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Estrategia concreta (extensión del alcance): menor número de conexiones.
 *
 * Usa un algoritmo distinto (búsqueda en anchura, BFS), lo que demuestra que el
 * patrón Strategy permite intercambiar algoritmos completos y no solo pesos.
 * Complejidad: O(V + E).
 */
@Component
@Order(3)
public class FewestHopsStrategy implements RouteStrategy {

    @Override
    public String key() {
        return "FEWEST_HOPS";
    }

    @Override
    public String displayName() {
        return "Menos conexiones";
    }

    @Override
    public String description() {
        return "Minimiza la cantidad de tramos o transbordos (búsqueda en anchura).";
    }

    @Override
    public Optional<PathResult> findRoute(Graph graph, Long originId, Long destinationId) {
        Map<Long, Edge> previousEdge = new HashMap<>();
        Set<Long> visited = new HashSet<>();
        Deque<Long> queue = new ArrayDeque<>();

        visited.add(originId);
        queue.add(originId);
        int nodesExplored = 0;

        while (!queue.isEmpty()) {
            Long current = queue.poll();
            nodesExplored++;

            if (current.equals(destinationId)) {
                return Optional.of(PathResult.fromPredecessors(originId, destinationId, previousEdge, nodesExplored));
            }

            for (Edge edge : graph.neighbors(current)) {
                if (visited.add(edge.toId())) {
                    previousEdge.put(edge.toId(), edge);
                    queue.add(edge.toId());
                }
            }
        }
        return Optional.empty();
    }
}
