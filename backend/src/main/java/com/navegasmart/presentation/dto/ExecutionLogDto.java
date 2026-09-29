package com.navegasmart.presentation.dto;

import com.navegasmart.business.model.ExecutionRecord;

import java.time.LocalDateTime;

public record ExecutionLogDto(Long id,
                              String runType,
                              String strategyKey,
                              Long originId,
                              Long destinationId,
                              int nodeCount,
                              int edgeCount,
                              Integer hops,
                              Integer nodesExplored,
                              Double totalDistanceKm,
                              Double totalTimeMin,
                              double executionTimeMs,
                              LocalDateTime createdAt) {

    public static ExecutionLogDto from(ExecutionRecord r) {
        return new ExecutionLogDto(r.id(), r.runType(), r.strategyKey(), r.originId(), r.destinationId(),
                r.nodeCount(), r.edgeCount(), r.hops(), r.nodesExplored(), r.totalDistanceKm(),
                r.totalTimeMin(), r.executionTimeMs(), r.createdAt());
    }
}
