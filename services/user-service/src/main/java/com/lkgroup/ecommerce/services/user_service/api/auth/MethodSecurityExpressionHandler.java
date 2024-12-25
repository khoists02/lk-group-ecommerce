package com.lkgroup.ecommerce.services.user_service.api.auth;

import com.lkgroup.ecommerce.services.user_service.api.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

public class MethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    private final AuthenticationService authenticationService;
    private final HttpServletRequest request;

    public MethodSecurityExpressionHandler(AuthenticationService authenticationService, HttpServletRequest request)
    {
        this.authenticationService = authenticationService;
        this.request = request;
    }

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
        MethodSecurityExpressionRoot root = new MethodSecurityExpressionRoot(authentication, authenticationService, request);
        root.setPermissionEvaluator(this.getPermissionEvaluator());
        root.setTrustResolver(this.getTrustResolver());
        root.setRoleHierarchy(this.getRoleHierarchy());
        root.setDefaultRolePrefix(this.getDefaultRolePrefix());
        return root;
    }
}
