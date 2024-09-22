package com.lkgroup.ecommerce.services.user_service.api.exceptions;


import com.lkgroup.ecommerce.common.domain.exceptions.ApplicationException;

public class UnauthenticatedException extends ApplicationException {
    public static UnauthenticatedException UNAUTHENTICATED = new UnauthenticatedException("unauthenticated_exception", "1000");
    public static UnauthenticatedException USER_DISABLED = new UnauthenticatedException("user_disabled_exception", "1003");
    public static UnauthenticatedException INVALID_CREDENTIALS = new UnauthenticatedException("invalid_credentials_exception", "1004");
    public static UnauthenticatedException INVALID_TWO_FACTOR = new UnauthenticatedException("invalid_multi_factor_exception", "1005");
    public static UnauthenticatedException MALFORMED_TOKEN = new UnauthenticatedException("malformed_token_exception", "1006");
    public static UnauthenticatedException EXPIRED_TOKEN = new UnauthenticatedException("expired_token_exception", "1007");
    public static UnauthenticatedException INVALID_TOKEN = new UnauthenticatedException("invalid_token_exception", "1008");
    public static UnauthenticatedException INVALID_GWI = new UnauthenticatedException("invalid_gwi_exception", "1009");
    public static UnauthenticatedException MFA_ENROLLMENT_REQUIRED = new UnauthenticatedException("mfa_enrollment_required", "1014");
    public static UnauthenticatedException NOT_COOKIE_FOUND = new UnauthenticatedException("cookie_is_empty", "1015");

    private UnauthenticatedException(String messageKey, String code) {
        super(messageKey, code, 401);
    }
}
