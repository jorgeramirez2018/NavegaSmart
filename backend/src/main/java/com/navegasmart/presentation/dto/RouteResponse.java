package com.navegasmart.presentation.dto;

import com.navegasmart.business.model.RouteResult;

import java.util.List;

public record RouteResponse(String strategyKey,
                            String strategyName,
                            List<NodeDto> path,
                            double totalDistanceKm,
                            double totalTimeMin,
                            int hops,
                            int nodesExplored,
                            double executionTimeMs) {

    public static RouteResponse from(RouteResult result) {
        return new RouteResponse(
                result.strategyKey(),
                result.strategyName(),
                result.path().stream().map(NodeDto::from).toList(),
                result.totalDistanceKm(),
                result.totalTimeMin(),
                result.hops(),
                result.nodesExplored(),
                result.executionTimeMs());
    }
}
