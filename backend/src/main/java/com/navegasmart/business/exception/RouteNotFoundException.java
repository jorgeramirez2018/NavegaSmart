package com.navegasmart.business.exception;

public class RouteNotFoundException extends RuntimeException {

    public RouteNotFoundException(String originName, String destinationName) {
        super("No hay ninguna ruta desde " + originName + " hasta " + destinationName
                + ". Revisa las conexiones de un solo sentido.");
    }
}
