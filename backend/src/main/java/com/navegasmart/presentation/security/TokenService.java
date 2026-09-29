package com.navegasmart.presentation.security;

import com.navegasmart.business.model.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

/**
 * Emite y lee los tokens JWT de sesión. El servidor no guarda sesiones:
 * el token firmado basta para saber quién hace cada solicitud.
 */
@Component
public class TokenService {

    static final String ISSUER = "navegasmart";
    private static final String CLAIM_USER_ID = "uid";
    private static final String CLAIM_FULL_NAME = "name";

    private final JwtEncoder encoder;
    private final Duration validity;

    public TokenService(JwtEncoder encoder, @Value("${navegasmart.auth.token-validity}") Duration validity) {
        this.encoder = encoder;
        this.validity = validity;
    }

    public IssuedToken issue(AuthenticatedUser user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(validity);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(user.username())
                .claim(CLAIM_USER_ID, user.id())
                .claim(CLAIM_FULL_NAME, user.fullName())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new IssuedToken(token, expiresAt);
    }

    public AuthenticatedUser userOf(Jwt jwt) {
        Number id = jwt.getClaim(CLAIM_USER_ID);
        return new AuthenticatedUser(id.longValue(), jwt.getSubject(), jwt.getClaimAsString(CLAIM_FULL_NAME));
    }

    public record IssuedToken(String value, Instant expiresAt) {
    }
}
