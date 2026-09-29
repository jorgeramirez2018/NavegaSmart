package com.navegasmart.business.service;

import com.navegasmart.business.SampleNetwork;
import com.navegasmart.business.exception.NodeNotFoundException;
import com.navegasmart.business.model.RouteResult;
import com.navegasmart.business.strategy.FastestTimeStrategy;
import com.navegasmart.business.strategy.FewestHopsStrategy;
import com.navegasmart.business.strategy.ShortestDistanceStrategy;
import com.navegasmart.business.strategy.StrategyRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.navegasmart.business.SampleNetwork.AEROPUERTO;
import static com.navegasmart.business.SampleNetwork.TERMINAL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Prueba el Contexto del patrón Strategy sin base de datos (repositorios simulados con Mockito).
 */
class NavigationServiceTest {

    private GraphService graphService;
    private ExecutionLogService executionLogService;
    private NavigationService navigationService;

    @BeforeEach
    void setUp() {
        graphService = mock(GraphService.class);
        executionLogService = mock(ExecutionLogService.class);
        when(graphService.getGraph()).thenReturn(SampleNetwork.create());

        StrategyRegistry registry = new StrategyRegistry(List.of(
                new ShortestDistanceStrategy(), new FastestTimeStrategy(), new FewestHopsStrategy()));
        navigationService = new NavigationService(graphService, registry, executionLogService);
    }

    @Test
    void calculatesAndLogsRoute() {
        RouteResult result = navigationService.calculateRoute(TERMINAL, AEROPUERTO, "FASTEST");

        assertEquals("FASTEST", result.strategyKey());
        assertEquals(17, result.totalTimeMin(), 1e-9);
        assertTrue(result.executionTimeMs() >= 0);
        verify(executionLogService).logRoute(eq(result), eq(TERMINAL), eq(AEROPUERTO), any());
    }

    @Test
    void comparesAllStrategies() {
        List<RouteResult> results = navigationService.compareStrategies(TERMINAL, AEROPUERTO);

        assertEquals(List.of("SHORTEST", "FASTEST", "FEWEST_HOPS"),
                results.stream().map(RouteResult::strategyKey).toList());
        verify(executionLogService, times(3)).logRoute(any(), any(), any(), any());
    }

    @Test
    void rejectsUnknownNode() {
        assertThrows(NodeNotFoundException.class,
                () -> navigationService.calculateRoute(TERMINAL, 404L, "SHORTEST"));
    }

    @Test
    void rejectsSameOriginAndDestination() {
        assertThrows(IllegalArgumentException.class,
                () -> navigationService.calculateRoute(TERMINAL, TERMINAL, "SHORTEST"));
    }
}
