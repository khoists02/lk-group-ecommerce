package com.lkgroup.ecommerce.common.domain.support;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public  class NoCountPageRequest extends PageRequest {
    protected NoCountPageRequest(int page, int size, Sort sort) {
        super(page, size, sort);
    }

    public static NoCountPageRequest of(int page, int size, Sort sort) {
        return new NoCountPageRequest(page, size, sort);
    }

}
