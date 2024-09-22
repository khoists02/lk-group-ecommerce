package com.lkgroup.ecommerce.common.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "roles_permissions")
@Getter
@Setter
public class RolePermission extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "permission_id")
    private Permission permission;

    public RolePermission() {
    }

    public RolePermission(Role role, Permission permission) {
        this.permission = permission;
        this.role = role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(permission, role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        RolePermission that = (RolePermission) o;
        return Objects.equals(role.getId(), that.role.getId()) &&
                Objects.equals(permission.getId(), that.permission.getId());
    }

}
