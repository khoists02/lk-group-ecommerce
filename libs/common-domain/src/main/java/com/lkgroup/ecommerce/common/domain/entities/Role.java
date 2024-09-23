package com.lkgroup.ecommerce.common.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Table(name = "roles")
@Entity
@Getter
@Setter
public class Role extends BaseEntity {
    private String name;
    private String description;

    @OneToMany(
            mappedBy = "role",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    Set<RolePermission> rolePermissions = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = {@JoinColumn(name = "role_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private Set<User> users = new HashSet<>();

    public void addRolePermission(RolePermission rolePermission) {
        rolePermission.setRole(this);
        this.rolePermissions.add(rolePermission);
    }

    public void setPermissions(Set<Permission> permissions) {
        this.rolePermissions.retainAll(permissions.stream().map(p -> new RolePermission(this, p)).collect(Collectors.toSet()));
        // Because we are binding 2 ways one-many in role and permission, it will auto add to set when new RolePermission, so don't need to call addPermission.
    }

    public void addPermissions(Set<Permission> permissions) {
        this.rolePermissions.addAll(permissions.stream().map(permission ->new RolePermission(this, permission)).collect(Collectors.toSet()));
    }

    public Set<Permission> getPermissions() {
        return this.rolePermissions.stream().map(x -> x.getPermission()).collect(Collectors.toSet());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Role that = (Role) o;
        return this.getId().equals(that.getId());
    }

}
