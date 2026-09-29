package com.navegasmart.business.exception;

import java.util.Collection;

public class UnknownStrategyException extends RuntimeException {

    public UnknownStrategyException(String key, Collection<String> availableKeys) {
        super("La estrategia '" + key + "' no existe. Estrategias disponibles: " + String.join(", ", availableKeys));
    }
}
