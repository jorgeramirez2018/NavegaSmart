package com.navegasmart.business.exception;

public class NodeNotFoundException extends RuntimeException {

    public NodeNotFoundException(Long nodeId) {
        super("No existe un punto con id " + nodeId + " en la red.");
    }
}
