package com.navegasmart.business.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Resultado "crudo" de una estrategia: la secuencia de nodos, las aristas
 * recorridas y cuántos nodos tuvo que explorar el algoritmo.
 */
public record PathResult(List<Long> nodeIds, List<Edge> edges, int nodesExplored) {

    public double totalDistanceKm() {
        return edges.stream().mapToDouble(Edge::distanceKm).sum();
    }

    public double totalTimeMin() {
        return edges.stream().mapToDouble(Edge::timeMin).sum();
    }

    public int hops() {
        return edges.size();
    }

    /**
     * Reconstruye el camino desde el destino hacia el origen usando el mapa
     * de predecesores que construyen Dijkstra y BFS.
     */
    public static PathResult fromPredecessors(Long originId,
                                              Long destinationId,
                                              Map<Long, Edge> previousEdge,
                                              int nodesExplored) {
        LinkedList<Edge> edges = new LinkedList<>();
        Long current = destinationId;
        while (!current.equals(originId)) {
            Edge edge = previousEdge.get(current);
            if (edge == null) {
                throw new IllegalStateException("Camino incompleto al reconstruir la ruta en el nodo " + current);
            }
            edges.addFirst(edge);
            current = edge.fromId();
        }
        List<Long> nodeIds = new ArrayList<>(edges.size() + 1);
        nodeIds.add(originId);
        edges.forEach(edge -> nodeIds.add(edge.toId()));
        return new PathResult(List.copyOf(nodeIds), List.copyOf(edges), nodesExplored);
    }
}
