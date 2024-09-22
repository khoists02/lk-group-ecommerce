package com.lkgroup.ecommerce.common.domain.support.fragments;

import com.lkgroup.ecommerce.common.domain.support.NoCountPageRequest;
import jakarta.persistence.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;

import java.util.Optional;
import java.util.stream.Stream;

public interface MyJpaSpecificationExecutor<T> extends JpaSpecificationExecutor<T> {

    Optional<T> findOne(@Nullable Specification<T> specification, EntityGraph<T> entityGraph);

    Stream<T> findAll(@Nullable Specification<T> specification, EntityGraph<T> entityGraph);

    Page<T> findAll(@Nullable Specification<T> specification, Pageable pageable, EntityGraph<T> entityGraph);

    Slice<T> findAll(@Nullable Specification<T> specification, NoCountPageRequest pageable, EntityGraph<T> entityGraph);

    Stream<T> findAll(@Nullable Specification<T> specification, Sort sort, EntityGraph<T> entityGraph);

}
