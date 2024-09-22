package com.lkgroup.ecommerce.services.user_service.api.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import java.util.*;

public class AuthenticatedUser implements Authentication {
    private UUID userId;
    private String username;
    private UUID sessionId;
    private final Jws<Claims> parsedJwt;
    private List<? extends GrantedAuthority> grantedAuthorities = new ArrayList<>();

    public AuthenticatedUser(Jws<Claims> parsedJwt) {
        this.parsedJwt = parsedJwt;
        this.userId = UUID.fromString(parsedJwt.getPayload().get("sub", String.class));
        this.username = parsedJwt.getPayload().get("un", String.class);
        this.sessionId = UUID.fromString(parsedJwt.getPayload().get("ses", String.class));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new IllegalArgumentException();
    }

    public String getAllowedOrigin() {
        return this.parsedJwt.getPayload().get("dom", String.class);
    }
    public UUID getSessionId() {
        return this.sessionId;
    }

    public UUID getUserId() {
        return this.userId;
    }

    public String getUsername() {
        return this.username;
    }

    public <T> T getClaimValue(String claim, Class<T> clazz) {
        return this.parsedJwt.getPayload().get(claim, clazz);
    }



    @Override
    public String getName() {
        return "%s".formatted(this.userId);
    }
}
