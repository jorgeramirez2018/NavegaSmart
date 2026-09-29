package com.navegasmart.presentation.controller;

import com.navegasmart.business.model.AuthenticatedUser;
import com.navegasmart.business.service.AuthService;
import com.navegasmart.presentation.dto.AuthResponse;
import com.navegasmart.presentation.dto.LoginRequest;
import com.navegasmart.presentation.dto.RegisterRequest;
import com.navegasmart.presentation.dto.UserDto;
import com.navegasmart.presentation.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    public AuthController(AuthService authService, TokenService tokenService) {
        this.authService = authService;
        this.tokenService = tokenService;
    }

    /** Verifica usuario y contraseña y devuelve un token de sesión. */
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return respond(authService.authenticate(request.username(), request.password()));
    }

    /** Crea una cuenta nueva e inicia sesión con ella. */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return respond(authService.register(request.fullName(), request.username(), request.password()));
    }

    /** Usuario dueño del token (permite a la interfaz validar una sesión guardada). */
    @GetMapping("/me")
    public UserDto me(@AuthenticationPrincipal Jwt jwt) {
        return UserDto.from(tokenService.userOf(jwt));
    }

    private AuthResponse respond(AuthenticatedUser user) {
        TokenService.IssuedToken token = tokenService.issue(user);
        return new AuthResponse(token.value(), token.expiresAt(), UserDto.from(user));
    }
}
