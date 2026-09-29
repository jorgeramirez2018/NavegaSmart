package com.navegasmart.presentation.dto;

import com.navegasmart.business.model.Graph;

import java.util.List;

public record GraphResponse(List<NodeDto> nodes, List<ConnectionDto> connections) {

    public static GraphResponse from(Graph graph) {
        return new GraphResponse(
                graph.nodes().stream().map(NodeDto::from).toList(),
                graph.connections().stream().map(ConnectionDto::from).toList());
    }
}
