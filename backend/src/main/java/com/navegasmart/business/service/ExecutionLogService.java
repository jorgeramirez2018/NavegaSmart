package com.navegasmart.business.service;

import com.navegasmart.business.model.BenchmarkMeasurement;
import com.navegasmart.business.model.ExecutionRecord;
import com.navegasmart.business.model.Graph;
import com.navegasmart.business.model.RouteResult;
import com.navegasmart.data.entity.ExecutionLogEntity;
import com.navegasmart.data.repository.ExecutionLogRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Guarda y consulta el registro de resultados y tiempos de ejecución.
 */
@Service
public class ExecutionLogService {

    private final ExecutionLogRepository repository;

    public ExecutionLogService(ExecutionLogRepository repository) {
        this.repository = repository;
    }

    public void logRoute(RouteResult result, Long originId, Long destinationId, Graph graph) {
        repository.save(new ExecutionLogEntity(
                ExecutionLogEntity.RUN_TYPE_ROUTE,
                result.strategyKey(),
                originId,
                destinationId,
                graph.nodeCount(),
                graph.connectionCount(),
                result.hops(),
                result.nodesExplored(),
                result.totalDistanceKm(),
                result.totalTimeMin(),
                result.executionTimeMs()));
    }

    public void logBenchmark(BenchmarkMeasurement measurement) {
        repository.save(new ExecutionLogEntity(
                ExecutionLogEntity.RUN_TYPE_BENCHMARK,
                measurement.strategyKey(),
                null,
                null,
                measurement.nodeCount(),
                measurement.connectionCount(),
                measurement.hops(),
                measurement.nodesExplored(),
                measurement.totalDistanceKm(),
                measurement.totalTimeMin(),
                measurement.averageMs()));
    }

    public List<ExecutionRecord> recent(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 500));
        return repository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, safeLimit)).stream()
                .map(entity -> new ExecutionRecord(
                        entity.getId(),
                        entity.getRunType(),
                        entity.getStrategyKey(),
                        entity.getOriginId(),
                        entity.getDestinationId(),
                        entity.getNodeCount(),
                        entity.getEdgeCount(),
                        entity.getHops(),
                        entity.getNodesExplored(),
                        entity.getTotalDistanceKm(),
                        entity.getTotalTimeMin(),
                        entity.getExecutionTimeMs(),
                        entity.getCreatedAt()))
                .toList();
    }
}
