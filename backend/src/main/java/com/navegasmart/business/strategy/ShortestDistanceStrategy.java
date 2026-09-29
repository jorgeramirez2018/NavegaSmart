package com.navegasmart.business.strategy;

import com.navegasmart.business.model.Edge;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Estrategia concreta: ruta más corta (minimiza la distancia total en km).
 */
@Component
@Order(1)
public class ShortestDistanceStrategy extends AbstractDijkstraStrategy {

    @Override
    public String key() {
        return "SHORTEST";
    }

    @Override
    public String displayName() {
        return "Ruta más corta";
    }

    @Override
    public String description() {
        return "Minimiza la distancia total recorrida (Dijkstra sobre km).";
    }

    @Override
    protected double cost(Edge edge) {
        return edge.distanceKm();
    }
}
