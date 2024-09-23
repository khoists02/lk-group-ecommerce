package com.lkgroup.ecommerce.services.user_service.api.filters;

import com.lkgroup.ecommerce.services.user_service.api.auth.AuthenticatedUser;
import com.lkgroup.ecommerce.services.user_service.api.exceptions.UnauthenticatedException;
import com.lkgroup.ecommerce.services.user_service.api.service.AuthenticationService;
import com.lkgroup.ecommerce.services.user_service.api.service.JwtTokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

public class CookieAuthFilter extends OncePerRequestFilter {
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Autowired
    private JwtTokenService jwtTokenService;
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return pathMatcher.match("/csrf", request.getRequestURI())
                || pathMatcher.match("/auth/**", request.getRequestURI())
                || pathMatcher.match("/languages", request.getRequestURI())
                || pathMatcher.match("/studies/shares", request.getRequestURI())
                || pathMatcher.match("/actuator/**", request.getRequestURI())
                || pathMatcher.match("/public/**", request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        Jws<Claims> parsedJwt;

        Optional<Cookie> authCookie = resolveAuthenticationCookieForSubdomain(httpServletRequest);

        if (authCookie.isEmpty() || authCookie.get().getValue().isEmpty()) {
            logger.info("No cookie found in request");
            throw UnauthenticatedException.UNAUTHENTICATED;
        }
        try {
            parsedJwt = jwtTokenService.parseJwt(authCookie.get().getValue());
        } catch (UnsupportedJwtException | MalformedJwtException | CompressionException | IllegalArgumentException e) {
            throw UnauthenticatedException.MALFORMED_TOKEN;
        } catch (ExpiredJwtException e) {
            throw UnauthenticatedException.EXPIRED_TOKEN;
        } catch (SignatureException | NoSuchElementException e) {
            throw UnauthenticatedException.INVALID_TOKEN;
        }

        String tokenType = Optional.ofNullable(parsedJwt.getPayload().get("type", String.class))
                .orElseThrow(() -> UnauthenticatedException.INVALID_TOKEN);

        if (!tokenType.equals("access"))
            throw UnauthenticatedException.INVALID_CREDENTIALS;
        try {
            SecurityContextHolder.getContext().setAuthentication(new AuthenticatedUser(parsedJwt)); // Set whole context.
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private Optional<Cookie> resolveAuthenticationCookieForSubdomain(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("secret" + ".token")) {
                return Optional.of(cookie);
            }
        }
        return Optional.empty();
    }
}
