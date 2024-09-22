package com.lkgroup.ecommerce.common.domain.support.fragments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface MyJpaRepository<T, ID> extends MyJpaSpecificationExecutor<T>, DynamicEntityGraph<T, ID>, JpaRepository<T, ID> {
}
