package com.lkgroup.ecommerce.common.domain.repositories;

import com.lkgroup.ecommerce.common.domain.entities.Permission;
import com.lkgroup.ecommerce.common.domain.support.fragments.MyJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PermissionRepository extends MyJpaRepository<Permission, UUID> {
}
