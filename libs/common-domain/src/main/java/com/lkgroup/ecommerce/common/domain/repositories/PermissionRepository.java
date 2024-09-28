package com.lkgroup.ecommerce.common.domain.repositories;

import com.lkgroup.ecommerce.common.domain.entities.Permission;
import com.lkgroup.ecommerce.common.domain.support.fragments.MyJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PermissionRepository extends MyJpaRepository<Permission, UUID> {

    @Query("""
            select distinct p from Permission p where p.name not like 'superAdmin'
            """)
    List<Permission> getAllPermissions();
}
