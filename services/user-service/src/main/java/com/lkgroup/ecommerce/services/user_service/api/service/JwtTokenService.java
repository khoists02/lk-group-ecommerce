package com.lkgroup.ecommerce.services.user_service.api.service;

import com.lkgroup.ecommerce.common.domain.entities.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtTokenService {
    // TODO: move to docker ENV
    private String secretKey = "NllmZHptNVVrNG9RRUs3NllmZHptNVVrNG9RRUs3NllmZHptNVVrNG9RRUs3NllmZHptNVVrNG9RRUs3NllmZHptNVVrNG9RRUs3NllmZHptNVVrNG9RRUs3NllmZHptNVVrNG9RRUs3Nl";
    private static final long ACCESS_TOKEN_VALIDITY_SECONDS = 15*60*60;
    private static final long REFRESH_OKEN_VALIDITY_SECONDS = 5*60*60;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private JwtParser jwtParser;

    @PostConstruct
    protected void init() {
        this.jwtParser = Jwts.parser()
                .requireIssuer("TTC")
                .verifyWith(getSecretKey())
                .build();
    }

    public String generateAccessToken(User user) {
        Date expirationDate = new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS*1000);
        logger.info(expirationDate.toString());
        return Jwts.builder().claims()
                .add("type", "access")
                .add("un", user.getUsername())
                .subject(user.getId().toString())
                .issuer("TTC")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(expirationDate)
                .and()
                .signWith(getSecretKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(User user) {
        Date expirationDate = new Date(System.currentTimeMillis() + REFRESH_OKEN_VALIDITY_SECONDS*1000);
        logger.info(expirationDate.toString());
        return Jwts.builder().claims()
                .add("type", "refresh")
                .add("un", user.getUsername())
                .subject(user.getId().toString())
                .issuer("TTC")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(expirationDate)
                .and()
                .signWith(getSecretKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String extractUsernameFromToken(String token) {
        if (isTokenExpired(token)) {
            return null;
        }
        return getClaims(token, Claims::getSubject);
    }

    public <T> T getClaims(String token, Function<Claims, T> resolver) {
        return resolver.apply(Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload());
    }

    public Jws<Claims> parseJwt(String token) {
        return this.jwtParser.parseClaimsJws(token);
    }

    public boolean isTokenExpired(String token) {
        Date expiration = getClaims(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
