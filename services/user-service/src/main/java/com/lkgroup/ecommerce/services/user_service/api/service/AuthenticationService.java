package com.lkgroup.ecommerce.services.user_service.api.service;

import com.lkgroup.ecommerce.common.domain.entities.User;
import com.lkgroup.ecommerce.common.domain.repositories.UserRepository;
import com.lkgroup.ecommerce.services.user_service.api.exceptions.UnauthenticatedException;
import com.lkgroup.ecommerce.services.user_service.utils.UserUtils;
import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;

@Service
public class AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final HttpServletResponse response;
    private final JwtTokenService jwtTokenService;
    private final ConfigurationService configurationService;
    private Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    public AuthenticationService(PasswordEncoder passwordEncoder, UserRepository userRepository, HttpServletRequest request, HttpServletResponse response, JwtTokenService jwtTokenService, ConfigurationService configurationService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.response = response;
        this.jwtTokenService = jwtTokenService;
        this.configurationService = configurationService;
    }


    @Getter
    @Builder
    public static class AuthenticationContext {
        private String username;
        private String password;
    }

    public void authenticate(AuthenticationContext context) {
        User user = userRepository.findOneByUsername(context.getUsername()).orElseThrow(() -> UnauthenticatedException.INVALID_CREDENTIALS);
        //Check the credentials, if not a SAML auth request
        if (!passwordEncoder.matches(context.getPassword(), user.getPassword())) {
            throw UnauthenticatedException.INVALID_CREDENTIALS;
        }

        if (!user.isEnabled()) {
            throw UnauthenticatedException.USER_DISABLED; // TODO: should enabled account by admin.
        }
        //Create the JWT and Refresh Token
        String accessToken = this.generateAccessToken(context, user);
        String refreshToken = this.generateRefreshToken(context, user);

        //Set the cookies
        this.injectAuthenticationTokenCookie(response,  accessToken);
        this.injectRefreshTokenCookie(response, refreshToken);
    }

    public String generateAccessToken(AuthenticationContext context, User user) {
        return this.jwtTokenService.generateAccessToken(user);
    }

    public String generateRefreshToken(AuthenticationContext context, User user) {
        return this.jwtTokenService.generateRefreshToken(user);
    }

    public void injectAuthenticationTokenCookie(HttpServletResponse response, String cookieValue) {
        Cookie tokenCookie = new Cookie("secret" + ".token", cookieValue);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setPath("/");
        tokenCookie.setSecure(true);
        // TODO: move to docker ENV
        tokenCookie.setDomain(configurationService.getRootDomain());
        response.addCookie(tokenCookie);
    }

    public void injectRefreshTokenCookie(HttpServletResponse response,  String cookieValue) {
        Cookie refreshCookie = new Cookie("secret" + ".refresh", cookieValue);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/auth");
        // TODO: move to docker ENV
        refreshCookie.setDomain(configurationService.getRootDomain());
        refreshCookie.setSecure(true);
        response.addCookie(refreshCookie);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() == null || (request.getCookies() != null &&
                Arrays.stream(request.getCookies())
                        .filter(c -> c.getName().contains("secret" + ".token") || c.getName().contains("secret" + ".refresh"))
                        .findFirst().isEmpty())) {
            throw UnauthenticatedException.UNAUTHENTICATED;
        }

        Cookie refreshTokenCookie = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("secret" + ".refresh"))
                .findFirst().orElseThrow(() -> UnauthenticatedException.UNAUTHENTICATED);
        if (refreshTokenCookie.getValue().isBlank())
            throw UnauthenticatedException.UNAUTHENTICATED;

        this.injectAuthenticationTokenCookie(response, "");
        this.injectRefreshTokenCookie(response, "");

        Jws<Claims> refreshTokenParsed;
        try {
            refreshTokenParsed = jwtTokenService.parseJwt(refreshTokenCookie.getValue());
            if (!Optional.ofNullable(refreshTokenParsed.getPayload().get("type", String.class)).orElseThrow(() -> new JwtException("Missing required claim: typ")).equals("refresh")) {
                throw new JwtException("Invalid value for claim: typ");
            }
//            UUID sessionId = UUID.fromString(Optional.ofNullable(refreshTokenParsed.getPayload().get("ses", String.class)).orElseThrow(() -> new JwtException("Missing required claim: ses")));
//            logger.info("Logged out session: {}", sessionId);
        } catch (JwtException e) {
            logger.error("There was an exception parsing the refresh token during logout. The user will be logged out but there session will not be removed", e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public boolean hasPermission(User user, String permission) {
        return userRepository.hasPermission(user, permission);
    }

    @Transactional(readOnly = true)
    public boolean hasPermission(String permission) {
        return hasPermission(UserUtils.getUser(), permission);
    }


    @Transactional(readOnly = true)
    public boolean hasSuperAdmin() {
        return userRepository.hasSuperAdmin(UserUtils.getUser());
    }

}
