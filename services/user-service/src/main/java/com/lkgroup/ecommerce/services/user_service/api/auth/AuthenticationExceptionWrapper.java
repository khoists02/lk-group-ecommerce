package com.lkgroup.ecommerce.services.user_service.api.auth;

import org.springframework.security.core.AuthenticationException;

public class AuthenticationExceptionWrapper extends AuthenticationException {

    AuthenticationExceptionWrapper(Throwable cause)
    {
        super("", cause);
    }
}
