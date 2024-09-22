package com.lkgroup.ecommerce.common.domain.exceptions;
import lombok.Getter;

public class RegionNotFoundException extends BadRequestException{

    @Getter
    protected String code = "2001";
    @Getter
    private String messageKey = "customer_not_found_exception";

}
