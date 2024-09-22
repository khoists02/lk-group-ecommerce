package com.lkgroup.ecommerce.services.user_service.api.exceptions;


import com.lkgroup.ecommerce.common.domain.exceptions.ApplicationException;

public class NotFoundException extends ApplicationException {

    public NotFoundException()
    {
        this("not_found_exception");
    }

    public NotFoundException(String messageKey)
    {
        this(messageKey, "404");
    }

    public NotFoundException(String messageKey, String code)
    {
        this(messageKey, code, 404);
    }

    public NotFoundException(String messageKey, String code, Integer status)
    {
        super(messageKey, code, status);
    }
}
