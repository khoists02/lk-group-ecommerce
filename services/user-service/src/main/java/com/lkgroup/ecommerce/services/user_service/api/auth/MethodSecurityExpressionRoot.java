package com.lkgroup.ecommerce.services.user_service.api.auth;

import com.lkgroup.ecommerce.services.user_service.api.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import java.util.Optional;

public class MethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private final AuthenticationService authenticationService;

    private Object filterObject;
    private Object returnObject;
    private Object target;
    private final HttpServletRequest request;

    public MethodSecurityExpressionRoot(Authentication authentication, AuthenticationService authenticationService, HttpServletRequest request) {
        super(authentication);
        this.authenticationService = authenticationService;
        this.request = request;
    }

    public boolean hasPermission(String permission)
    {
        return authenticationService.hasPermission(permission);
    }

    public boolean hasSuperAdmin()
    {
        return authenticationService.hasSuperAdmin();
    }

    public boolean hasRequestParam(String key, Optional<String> value)
    {
        if (key.isBlank() || value.isEmpty())
            return false;
        return value.get().equals(request.getParameter(key));
    }

    @Override
    public void setFilterObject(Object o) {
        this.filterObject = o;
    }

    @Override
    public Object getFilterObject() {
        return this.filterObject;
    }

    @Override
    public void setReturnObject(Object o) {
        this.returnObject = o;
    }

    @Override
    public Object getReturnObject() {
        return this.returnObject;
    }

    @Override
    public Object getThis() {
        return target;
    }
}
