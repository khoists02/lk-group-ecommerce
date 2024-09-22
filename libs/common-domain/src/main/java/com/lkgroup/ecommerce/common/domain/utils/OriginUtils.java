package com.lkgroup.ecommerce.common.domain.utils;


import com.lkgroup.ecommerce.common.domain.exceptions.BadRequestException;
import com.lkgroup.ecommerce.common.domain.exceptions.MissingOriginHeaderException;

import java.net.URI;
import java.util.Arrays;
import java.util.Optional;

public class OriginUtils {
    private OriginUtils(){}
    public static String getSubdomain() {
        return Arrays.stream(URI.create(
                Optional.ofNullable(com.lkgroup.ecommerce.utils.RequestUtils.getCustomerOrigin()).orElseThrow(MissingOriginHeaderException::new)
        ).getHost().split("\\.")).findFirst().orElseThrow(BadRequestException::new);
    }
}
