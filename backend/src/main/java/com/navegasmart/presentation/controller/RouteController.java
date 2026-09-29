package com.navegasmart.presentation.controller;

import com.navegasmart.business.service.NavigationService;
import com.navegasmart.presentation.dto.CompareRequest;
import com.navegasmart.presentation.dto.RouteRequest;
import com.navegasmart.presentation.dto.RouteResponse;
import com.navegasmart.presentation.dto.StrategyDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RouteController {

    private final NavigationService navigationService;

    public RouteController(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    /** Estrategias disponibles: la interfaz web las lista dinámicamente. */
    @GetMapping("/strategies")
    public List<StrategyDto> strategies() {
        return navigationService.availableStrategies().stream().map(StrategyDto::from).toList();
    }

    /** Calcula una ruta con la estrategia elegida. */
    @PostMapping("/routes")
    public RouteResponse calculate(@Valid @RequestBody RouteRequest request) {
        return RouteResponse.from(
                navigationService.calculateRoute(request.originId(), request.destinationId(), request.strategy()));
    }

    /** Calcula la ruta con todas las estrategias para comparar resultados. */
    @PostMapping("/routes/compare")
    public List<RouteResponse> compare(@Valid @RequestBody CompareRequest request) {
        return navigationService.compareStrategies(request.originId(), request.destinationId()).stream()
                .map(RouteResponse::from)
                .toList();
    }
}
