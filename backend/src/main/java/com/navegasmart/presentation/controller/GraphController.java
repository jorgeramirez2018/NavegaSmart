package com.navegasmart.presentation.controller;

import com.navegasmart.business.service.GraphService;
import com.navegasmart.presentation.dto.GraphResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/graph")
public class GraphController {

    private final GraphService graphService;

    public GraphController(GraphService graphService) {
        this.graphService = graphService;
    }

    /** Red completa (nodos y conexiones) para dibujar el mapa. */
    @GetMapping
    public GraphResponse getGraph() {
        return GraphResponse.from(graphService.getGraph());
    }

    /** Vuelve a leer la red desde PostgreSQL (útil si se editan los datos). */
    @PostMapping("/reload")
    public GraphResponse reload() {
        return GraphResponse.from(graphService.reload());
    }
}
