package com.lkgroup.ecommerce.services.user_service.api.exceptions;

import com.lkgroup.ecommerce.common.domain.exceptions.ApplicationException;
import com.lkgroup.ecommerce.common.validation.validators.PathUUID;
import com.lkgroup.ecommerce.protobuf.userproto.GenericProtos;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Optional;
import java.util.stream.Collectors;

@ControllerAdvice
public class WebExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<GenericProtos.ErrorResponse> handleApiException(ApplicationException exception) {
        return this.handle(exception);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException constraintViolationException) {

        if(constraintViolationException.getConstraintViolations().stream().anyMatch(v->v.getConstraintDescriptor().getAnnotation().annotationType().equals(PathUUID.class)))
        {
            return this.handle(new NotFoundException());
        }
        return new ResponseEntity<>(GenericProtos.ValidationErrorResponse.newBuilder()
                .setType(constraintViolationException.getClass().getSimpleName())
                .addAllErrors(
                        constraintViolationException.getConstraintViolations().stream().map(
                                v -> GenericProtos.ValidationError.newBuilder()
                                        .setField(v.getPropertyPath().toString())
                                        .setMessage(v.getMessage())
                                        .setValue(Optional.ofNullable(v.getInvalidValue()).orElse("").toString()).build()).collect(Collectors.toSet())
                )
                .setCode("422")
                .build(),
                null,
                422);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<GenericProtos.ErrorResponse> handleGenericError(Throwable throwable) {
        logger.error("Unhandled exception", throwable);
        //We don't want to throw anything non-specific as it could leak information to the user
        ApplicationException exception = new ApplicationException();
        return this.handle(exception);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<GenericProtos.ErrorResponse> handleResponseStatusException(ResponseStatusException exception) {
        return switch (exception.getStatusCode().value()) {
            case 404 -> handle(new NotFoundException());
            default -> {
                logger.error("Unexpected exception", exception);
                yield  this.handle(new ApplicationException());
            }
        };
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<GenericProtos.ErrorResponse> handleNotFoundException(NoHandlerFoundException noHandlerFoundException) {
        if(SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated())
        {
            return this.handle(new NotFoundException());
        }
        return this.handle(UnauthenticatedException.UNAUTHENTICATED);
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<GenericProtos.ErrorResponse> handleAccessDeniedException(AccessDeniedException accessDeniedException) {
        UnauthorizedException exception = UnauthorizedException.ACCESS_DENIED;
        return this.handle(exception);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<GenericProtos.ErrorResponse> handleInsufficientAuthenticationException(InsufficientAuthenticationException insufficientAuthenticationException) {
        UnauthenticatedException exception = UnauthenticatedException.UNAUTHENTICATED;
        return this.handle(exception);
    }

    @ExceptionHandler(value = {MissingCsrfTokenException.class, InvalidCsrfTokenException.class})
    public ResponseEntity<GenericProtos.ErrorResponse> handleMissingCsrfTokenException(CsrfException csrfException) {
        UnauthorizedException exception = UnauthorizedException.INVALID_CSRF_TOKEN;
        return this.handle(exception);
    }

    private ResponseEntity<GenericProtos.ErrorResponse> handle(ApplicationException exception) {
        return new ResponseEntity<>(
                GenericProtos.ErrorResponse.newBuilder()
                        .setCode(exception.getCode())
                        .setType(exception.getClass().getSimpleName())
                        .setMessage(exception.getLocalizedMessage())
                        .putAllParams(exception.getParams())
                        .build(),
                null,
                exception.getStatus()
        );
    }
}
