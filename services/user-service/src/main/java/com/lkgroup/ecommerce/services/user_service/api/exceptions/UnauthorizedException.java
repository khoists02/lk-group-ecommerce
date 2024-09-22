package com.lkgroup.ecommerce.services.user_service.api.exceptions;


import com.lkgroup.ecommerce.common.domain.exceptions.ApplicationException;

public class UnauthorizedException extends ApplicationException {

    public static UnauthorizedException DELETION_PROTECTION_ENABLED = new UnauthorizedException("deletion_protection_enabled", "1016");
    public static UnauthorizedException OBJECT_IMMUTABLE = new UnauthorizedException("object_immutable", "1019");
    public static UnauthorizedException INVALID_CSRF_TOKEN = new UnauthorizedException("invalid_csrf_token_exception", "1002");
    public static UnauthorizedException INVALID_CORS_REQUEST = new UnauthorizedException("invalid_cors_request_exception", "TODO");
    public static UnauthorizedException OPERATION_TOKEN_INVALID = new UnauthorizedException("invalid_operation_token_exception", "1011");
    public static UnauthorizedException FORBIDDEN_IP = new UnauthorizedException("forbidden_ip_exception", "1009");
    public static UnauthorizedException ACCESS_DENIED = new UnauthorizedException("access_denied_exception", "403");
    public UnauthorizedException(String messageKey, String code) {
        super(messageKey, code, 403);
    }

}
