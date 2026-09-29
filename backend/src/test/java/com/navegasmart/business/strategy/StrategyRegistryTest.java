package com.navegasmart.business.strategy;

import com.navegasmart.business.exception.UnknownStrategyException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StrategyRegistryTest {

    private final StrategyRegistry registry = new StrategyRegistry(List.of(
            new ShortestDistanceStrategy(), new FastestTimeStrategy(), new FewestHopsStrategy()));

    @Test
    void findsStrategyIgnoringCase() {
        assertInstanceOf(FastestTimeStrategy.class, registry.get("fastest"));
    }

    @Test
    void rejectsUnknownStrategy() {
        assertThrows(UnknownStrategyException.class, () -> registry.get("SCENIC"));
    }

    @Test
    void rejectsDuplicatedKeys() {
        assertThrows(IllegalStateException.class,
                () -> new StrategyRegistry(List.of(new ShortestDistanceStrategy(), new ShortestDistanceStrategy())));
    }
}
