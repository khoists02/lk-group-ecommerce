package com.lkgroup.ecommerce.services.user_service.api.service;

import com.lkgroup.ecommerce.common.domain.entities.User;
import com.lkgroup.ecommerce.common.domain.repositories.UserRepository;
import com.lkgroup.ecommerce.services.user_service.api.exceptions.UnauthenticatedException;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private JwtParser jwtParser;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final JwtTokenService jwtTokenService;

    public AuthenticationService(PasswordEncoder passwordEncoder, UserRepository userRepository, HttpServletRequest request, HttpServletResponse response, JwtTokenService jwtTokenService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.request = request;
        this.response = response;
        this.jwtTokenService = jwtTokenService;
    }

    @PostConstruct
    protected void init() {
        this.jwtParser = Jwts.parser()
                .requireAudience("TTC")
                .requireIssuer("TTC")
                .build();
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
        //Create the JWT and Refresh Token
        String accessToken = this.generateAccessToken(context, user);
        String refreshToken = this.generateRefreshToken(context, user);

        //Set the cookies
        this.injectAuthenticationTokenCookie(response,  accessToken);
        this.injectRefreshTokenCookie(response, refreshToken);
    }

    public String generateAccessToken(AuthenticationContext context, User user) {
        return this.jwtTokenService.generateAccessToken(user.getUsername());
    }

    public String generateRefreshToken(AuthenticationContext context, User user) {
        return this.jwtTokenService.generateRefreshToken(user.getUsername());
    }

    public void injectAuthenticationTokenCookie(HttpServletResponse response, String cookieValue) {
        Cookie tokenCookie = new Cookie("secret" + ".token", cookieValue);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setPath("/");
        tokenCookie.setSecure(true);
        tokenCookie.setDomain("ecommerce");
        response.addCookie(tokenCookie);
    }

    public void injectRefreshTokenCookie(HttpServletResponse response,  String cookieValue) {
        Cookie refreshCookie = new Cookie("secret" + ".refresh", cookieValue);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/auth");
        refreshCookie.setDomain("ecommerce");
        refreshCookie.setSecure(true);
        response.addCookie(refreshCookie);
    }

    public Jws<Claims> parseJwt(String token) {
        return this.jwtParser.parseClaimsJws(token);
    }

}
