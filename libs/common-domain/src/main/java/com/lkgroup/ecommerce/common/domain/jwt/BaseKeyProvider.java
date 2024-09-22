package com.lkgroup.ecommerce.common.domain.jwt;

import io.jsonwebtoken.SigningKeyResolver;
import jakarta.annotation.Nullable;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Optional;

public abstract class BaseKeyProvider implements KeyProvider, SigningKeyResolver {
    public String getCurrentKid(String tokenIssuer, @Nullable ZonedDateTime notBefore) {
        String dateString = LocalDate.now().atStartOfDay().format(DateTimeFormatter.BASIC_ISO_DATE);
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(md.digest((
                tokenIssuer
                        + dateString
                        + Optional.ofNullable(notBefore).map(ZonedDateTime::toString).orElse("")).getBytes(StandardCharsets.UTF_8)
        ));
    }
}
