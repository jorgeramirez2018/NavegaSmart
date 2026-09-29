package com.navegasmart.business.service;

import com.navegasmart.business.model.Graph;
import com.navegasmart.business.strategy.FewestHopsStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SyntheticGraphGeneratorTest {

    @Test
    void generatesRequestedNodeCountAndIsConnected() {
        Graph graph = SyntheticGraphGenerator.generate(1000, 42L);

        assertEquals(1000, graph.nodeCount());
        assertTrue(graph.connectionCount() >= 999);
        assertTrue(new FewestHopsStrategy().findRoute(graph, 0L, 999L).isPresent());
    }

    @Test
    void sameSeedProducesSameGraph() {
        Graph a = SyntheticGraphGenerator.generate(500, 7L);
        Graph b = SyntheticGraphGenerator.generate(500, 7L);

        assertEquals(a.connections(), b.connections());
    }
}
