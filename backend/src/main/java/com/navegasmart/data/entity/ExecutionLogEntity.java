package com.navegasmart.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Registro persistente de cada cálculo (ruta del usuario o prueba de rendimiento).
 * Sirve como evidencia para la sección "Pruebas y resultados" del informe.
 */
@Entity
@Table(name = "execution_logs")
public class ExecutionLogEntity {

    public static final String RUN_TYPE_ROUTE = "ROUTE";
    public static final String RUN_TYPE_BENCHMARK = "BENCHMARK";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "run_type", nullable = false, length = 20)
    private String runType;

    @Column(name = "strategy_key", nullable = false, length = 40)
    private String strategyKey;

    @Column(name = "origin_id")
    private Long originId;

    @Column(name = "destination_id")
    private Long destinationId;

    @Column(name = "node_count", nullable = false)
    private Integer nodeCount;

    @Column(name = "edge_count", nullable = false)
    private Integer edgeCount;

    @Column(name = "hops")
    private Integer hops;

    @Column(name = "nodes_explored")
    private Integer nodesExplored;

    @Column(name = "total_distance_km")
    private Double totalDistanceKm;

    @Column(name = "total_time_min")
    private Double totalTimeMin;

    @Column(name = "execution_time_ms", nullable = false)
    private Double executionTimeMs;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected ExecutionLogEntity() {
        // requerido por JPA
    }

    public ExecutionLogEntity(String runType, String strategyKey, Long originId, Long destinationId,
                              Integer nodeCount, Integer edgeCount, Integer hops, Integer nodesExplored,
                              Double totalDistanceKm, Double totalTimeMin, Double executionTimeMs) {
        this.runType = runType;
        this.strategyKey = strategyKey;
        this.originId = originId;
        this.destinationId = destinationId;
        this.nodeCount = nodeCount;
        this.edgeCount = edgeCount;
        this.hops = hops;
        this.nodesExplored = nodesExplored;
        this.totalDistanceKm = totalDistanceKm;
        this.totalTimeMin = totalTimeMin;
        this.executionTimeMs = executionTimeMs;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public String getRunType() { return runType; }
    public String getStrategyKey() { return strategyKey; }
    public Long getOriginId() { return originId; }
    public Long getDestinationId() { return destinationId; }
    public Integer getNodeCount() { return nodeCount; }
    public Integer getEdgeCount() { return edgeCount; }
    public Integer getHops() { return hops; }
    public Integer getNodesExplored() { return nodesExplored; }
    public Double getTotalDistanceKm() { return totalDistanceKm; }
    public Double getTotalTimeMin() { return totalTimeMin; }
    public Double getExecutionTimeMs() { return executionTimeMs; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
