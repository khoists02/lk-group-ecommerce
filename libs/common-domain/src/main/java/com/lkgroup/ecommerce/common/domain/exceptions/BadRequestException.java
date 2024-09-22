package com.lkgroup.ecommerce.common.domain.exceptions;

public class BadRequestException extends  ApplicationException {
    public static final BadRequestException OFFLINE_STORAGE_CLASS_INSTANCE = new BadRequestException("offline_storage_class_instance_exception");

    public BadRequestException()
    {
        this("bad_request_exception");
    }

    public BadRequestException(String messageKey)
    {
        this(messageKey, "400");
    }

    public BadRequestException(String messageKey, String code)
    {
        this(messageKey, code, 400);
    }

    public BadRequestException(String messageKey, String code, Integer status)
    {
        super(messageKey, code, status);
    }
}
