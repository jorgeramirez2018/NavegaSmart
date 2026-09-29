package com.navegasmart.presentation.dto;

import com.navegasmart.business.model.NodeInfo;

public record NodeDto(Long id, String name, double x, double y) {

    public static NodeDto from(NodeInfo node) {
        return new NodeDto(node.id(), node.name(), node.x(), node.y());
    }
}
