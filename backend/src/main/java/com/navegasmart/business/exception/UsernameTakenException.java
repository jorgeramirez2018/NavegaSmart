package com.navegasmart.business.exception;

public class UsernameTakenException extends RuntimeException {

    public UsernameTakenException(String username) {
        super("El usuario \"" + username + "\" ya está registrado.");
    }
}
