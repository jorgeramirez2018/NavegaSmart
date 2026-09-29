package com.navegasmart.presentation.dto;

import com.navegasmart.business.strategy.RouteStrategy;

public record StrategyDto(String key, String name, String description) {

    public static StrategyDto from(RouteStrategy strategy) {
        return new StrategyDto(strategy.key(), strategy.displayName(), strategy.description());
    }
}
