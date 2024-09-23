package com.lkgroup.ecommerce.common.domain.repositories;

import com.lkgroup.ecommerce.common.domain.entities.User;
import com.lkgroup.ecommerce.common.domain.support.fragments.MyJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface UserRepository extends MyJpaRepository<User, UUID> {
    Optional<User> findOneByUsername(String username);

    @Query("""
            select distinct p.permission.name
            from UserRole ur
            join ur.role role
            join role.rolePermissions p
            where ur.user = :user
            """)
    Stream<String> getPermissionCodesForUser(@Param("user") User user);

    @Query("""
            select case when (count(ur.id) > 0) then true else false end
            from UserRole ur
            join ur.role role
            join role.rolePermissions rolePermission
            where ur.user = :user and rolePermission.permission.name = :permission
            """)
    boolean hasPermission(@Param("user") User user, @Param("permission") String permission);


    @Query("""
            select case when (count(ur.id) > 0) then true else false end
            from UserRole ur
            join ur.role role
            join role.rolePermissions rolePermission
            where ur.user = :user and rolePermission.permission.name = 'superAdmin'
            """)
    boolean hasSuperAdmin(@Param("user") User user);
}
