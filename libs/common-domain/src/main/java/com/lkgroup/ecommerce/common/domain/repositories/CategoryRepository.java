package com.lkgroup.ecommerce.common.domain.repositories;

import com.lkgroup.ecommerce.common.domain.entities.Category;
import com.lkgroup.ecommerce.common.domain.support.fragments.MyJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoryRepository extends MyJpaRepository<Category, UUID> {
}
