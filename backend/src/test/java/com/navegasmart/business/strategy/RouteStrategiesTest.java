package com.navegasmart.business.strategy;

import com.navegasmart.business.SampleNetwork;
import com.navegasmart.business.model.Graph;
import com.navegasmart.business.model.PathResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.navegasmart.business.SampleNetwork.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas funcionales y de estrategias (sección 11 del documento):
 * el mismo origen y destino produce rutas distintas según el criterio.
 */
class RouteStrategiesTest {

    private final RouteStrategy shortest = new ShortestDistanceStrategy();
    private final RouteStrategy fastest = new FastestTimeStrategy();
    private final RouteStrategy fewestHops = new FewestHopsStrategy();
    private Graph graph;

    @BeforeEach
    void setUp() {
        graph = SampleNetwork.create();
    }

    @Test
    @DisplayName("Ruta más corta: minimiza la distancia")
    void shortestMinimizesDistance() {
        PathResult route = shortest.findRoute(graph, TERMINAL, AEROPUERTO).orElseThrow();

        assertEquals(List.of(TERMINAL, PLAZA_CENTRAL, UNIVERSIDAD, ESTADIO, AEROPUERTO), route.nodeIds());
        assertEquals(9.7, route.totalDistanceKm(), 1e-9);
        assertEquals(40, route.totalTimeMin(), 1e-9);
    }

    @Test
    @DisplayName("Ruta más rápida: minimiza el tiempo")
    void fastestMinimizesTime() {
        PathResult route = fastest.findRoute(graph, TERMINAL, AEROPUERTO).orElseThrow();

        assertEquals(List.of(TERMINAL, MERCADO, PARQUE, MUSEO, ZONA_INDUSTRIAL, AEROPUERTO), route.nodeIds());
        assertEquals(14.6, route.totalDistanceKm(), 1e-9);
        assertEquals(17, route.totalTimeMin(), 1e-9);
    }

    @Test
    @DisplayName("Menos conexiones: minimiza los tramos")
    void fewestHopsMinimizesSegments() {
        PathResult route = fewestHops.findRoute(graph, TERMINAL, AEROPUERTO).orElseThrow();

        assertEquals(List.of(TERMINAL, HOSPITAL, AEROPUERTO), route.nodeIds());
        assertEquals(2, route.hops());
    }

    @Test
    @DisplayName("Las tres estrategias producen rutas diferentes para el mismo origen y destino")
    void strategiesProduceDifferentRoutes() {
        List<Long> a = shortest.findRoute(graph, TERMINAL, AEROPUERTO).orElseThrow().nodeIds();
        List<Long> b = fastest.findRoute(graph, TERMINAL, AEROPUERTO).orElseThrow().nodeIds();
        List<Long> c = fewestHops.findRoute(graph, TERMINAL, AEROPUERTO).orElseThrow().nodeIds();

        assertNotEquals(a, b);
        assertNotEquals(b, c);
        assertNotEquals(a, c);
    }

    @Test
    @DisplayName("Respeta las vías de un solo sentido")
    void respectsOneWayConnections() {
        // Biblioteca -> Centro Comercial es de un solo sentido: de vuelta hay que rodear.
        PathResult forward = shortest.findRoute(graph, BIBLIOTECA, CENTRO_COMERCIAL).orElseThrow();
        PathResult backward = shortest.findRoute(graph, CENTRO_COMERCIAL, BIBLIOTECA).orElseThrow();

        assertEquals(1, forward.hops());
        assertTrue(backward.hops() > 1);
    }

    @Test
    @DisplayName("Devuelve vacío cuando el destino no es alcanzable")
    void returnsEmptyWhenUnreachable() {
        graph.addNode(new com.navegasmart.business.model.NodeInfo(99L, "Isla", 0, 0));

        assertEquals(Optional.empty(), shortest.findRoute(graph, TERMINAL, 99L));
        assertEquals(Optional.empty(), fastest.findRoute(graph, TERMINAL, 99L));
        assertEquals(Optional.empty(), fewestHops.findRoute(graph, TERMINAL, 99L));
    }
}
