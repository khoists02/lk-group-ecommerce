package com.lkgroup.ecommerce.services.user_service.config;

import com.lkgroup.ecommerce.services.user_service.api.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true) // TODO: preAuthorize
public class MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private HttpServletRequest request;

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return new com.lkgroup.ecommerce.services.user_service.api.auth.MethodSecurityExpressionHandler(authenticationService, request);
    }
}
