package com.lkgroup.ecommerce.common.domain.repositories;

import com.lkgroup.ecommerce.common.domain.entities.Orders;
import com.lkgroup.ecommerce.common.domain.support.fragments.MyJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends MyJpaRepository<Orders, UUID> {
}
