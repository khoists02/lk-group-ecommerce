package com.lkgroup.ecommerce.common.domain.support.fragments;

import jakarta.persistence.EntityGraph;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;
import java.util.stream.Stream;

@NoRepositoryBean
public interface DynamicEntityGraph<T, S> {

    EntityGraph<T> createEntityGraph();
    Stream<T> findAll(EntityGraph<T> entityGraph);
    Optional<T> findById(S id, EntityGraph<T> entityGraph);

}
