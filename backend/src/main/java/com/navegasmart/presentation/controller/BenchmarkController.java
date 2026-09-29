package com.navegasmart.presentation.controller;

import com.navegasmart.business.service.BenchmarkService;
import com.navegasmart.business.service.ExecutionLogService;
import com.navegasmart.presentation.dto.BenchmarkRequest;
import com.navegasmart.presentation.dto.BenchmarkResultDto;
import com.navegasmart.presentation.dto.ExecutionLogDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BenchmarkController {

    private final BenchmarkService benchmarkService;
    private final ExecutionLogService executionLogService;

    public BenchmarkController(BenchmarkService benchmarkService, ExecutionLogService executionLogService) {
        this.benchmarkService = benchmarkService;
        this.executionLogService = executionLogService;
    }

    /** Prueba de rendimiento/escalabilidad sobre grafos sintéticos de varios tamaños. */
    @PostMapping("/benchmarks")
    public List<BenchmarkResultDto> run(@Valid @RequestBody BenchmarkRequest request) {
        return benchmarkService.run(request.sizes(), request.repetitionsOrDefault(), request.warmupRunsOrDefault())
                .stream()
                .map(BenchmarkResultDto::from)
                .toList();
    }

    /** Historial de ejecuciones registradas en PostgreSQL. */
    @GetMapping("/executions")
    public List<ExecutionLogDto> executions(@RequestParam(defaultValue = "50") int limit) {
        return executionLogService.recent(limit).stream().map(ExecutionLogDto::from).toList();
    }
}
