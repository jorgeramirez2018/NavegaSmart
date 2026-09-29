package com.navegasmart.presentation.dto;

import com.navegasmart.business.model.BenchmarkMeasurement;

public record BenchmarkResultDto(String strategyKey,
                                 String strategyName,
                                 int nodeCount,
                                 int connectionCount,
                                 int repetitions,
                                 double averageMs,
                                 double minMs,
                                 double maxMs,
                                 int nodesExplored,
                                 int hops,
                                 double totalDistanceKm,
                                 double totalTimeMin) {

    public static BenchmarkResultDto from(BenchmarkMeasurement m) {
        return new BenchmarkResultDto(m.strategyKey(), m.strategyName(), m.nodeCount(), m.connectionCount(),
                m.repetitions(), m.averageMs(), m.minMs(), m.maxMs(), m.nodesExplored(), m.hops(),
                m.totalDistanceKm(), m.totalTimeMin());
    }
}
