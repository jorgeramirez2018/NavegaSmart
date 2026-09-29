package com.navegasmart.business.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        // Mensaje genérico: no revela si el usuario existe o si falló la contraseña.
        super("Usuario o contraseña incorrectos.");
    }
}
