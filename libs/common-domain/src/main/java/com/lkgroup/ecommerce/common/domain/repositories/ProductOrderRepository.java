package com.lkgroup.ecommerce.common.domain.repositories;

import com.lkgroup.ecommerce.common.domain.entities.ProductOrder;
import com.lkgroup.ecommerce.common.domain.support.fragments.MyJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductOrderRepository extends MyJpaRepository<ProductOrder, UUID> {
}
