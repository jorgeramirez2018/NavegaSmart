package com.navegasmart.business.strategy;

import com.navegasmart.business.exception.UnknownStrategyException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Registro de estrategias disponibles.
 *
 * Spring inyecta automáticamente todas las clases que implementan RouteStrategy
 * (ordenadas por @Order). Así el sistema descubre las estrategias sin un
 * switch/if-else: principio abierto/cerrado.
 */
@Component
public class StrategyRegistry {

    private final Map<String, RouteStrategy> strategies = new LinkedHashMap<>();

    public StrategyRegistry(List<RouteStrategy> availableStrategies) {
        for (RouteStrategy strategy : availableStrategies) {
            String key = normalize(strategy.key());
            if (strategies.putIfAbsent(key, strategy) != null) {
                throw new IllegalStateException("Hay dos estrategias con la misma clave: " + key);
            }
        }
    }

    public RouteStrategy get(String key) {
        RouteStrategy strategy = key == null ? null : strategies.get(normalize(key));
        if (strategy == null) {
            throw new UnknownStrategyException(key, strategies.keySet());
        }
        return strategy;
    }

    public Collection<RouteStrategy> all() {
        return Collections.unmodifiableCollection(strategies.values());
    }

    private static String normalize(String key) {
        return key.trim().toUpperCase(Locale.ROOT);
    }
}
