package com.lkgroup.ecommerce.common.domain.exceptions;

import lombok.Getter;
import org.springframework.context.NoSuchMessageException;

import java.util.HashMap;
import java.util.Map;

public class ApplicationException extends RuntimeException{
    protected Map<String, String> params = new HashMap<>();
    public ApplicationException() {
        super();
    }

    public ApplicationException(String messageKey) {
        super();
        this.messageKey = messageKey;
    }

    public ApplicationException(String messageKey, String code) {
        super();
        this.messageKey = messageKey;
        this.code = code;
    }

    public ApplicationException(String messageKey, String code, Integer status) {
        super();
        this.messageKey = messageKey;
        this.code = code;
        this.status = status;
    }

    @Getter
    protected String code = "500";
    @Getter
    protected Integer status = 500;
    @Getter
    protected String messageKey = "Application Exception";

    @Override
    public String getMessage() {
        try {
            return this.getMessageKey();
        } catch (NoSuchMessageException e) {
            return this.getMessageKey();
        }
    }

    public Map<String, String> getParams() {
        return params;
    }

}