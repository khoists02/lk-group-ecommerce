package com.lkgroup.ecommerce.common.domain.support;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

public class NoCountSliceImpl<T> extends SliceImpl<T> {

    public NoCountSliceImpl(List<T> content, Pageable pageable, boolean hasNext) {
        super(content, pageable, hasNext);
    }

    public NoCountSliceImpl(List<T> content) {
        super(content);
    }
}
