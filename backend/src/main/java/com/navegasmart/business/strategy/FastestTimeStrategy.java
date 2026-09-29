package com.navegasmart.business.strategy;

import com.navegasmart.business.model.Edge;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Estrategia concreta: ruta más rápida (minimiza el tiempo estimado en minutos).
 */
@Component
@Order(2)
public class FastestTimeStrategy extends AbstractDijkstraStrategy {

    @Override
    public String key() {
        return "FASTEST";
    }

    @Override
    public String displayName() {
        return "Ruta más rápida";
    }

    @Override
    public String description() {
        return "Minimiza el tiempo estimado de viaje (Dijkstra sobre minutos).";
    }

    @Override
    protected double cost(Edge edge) {
        return edge.timeMin();
    }
}
