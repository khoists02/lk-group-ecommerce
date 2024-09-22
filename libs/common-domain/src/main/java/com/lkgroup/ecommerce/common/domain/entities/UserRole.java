package com.lkgroup.ecommerce.common.domain.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Table(name = "user_roles")
@Entity
public class UserRole extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    public UserRole() {}

    public UserRole(User user, Role role)
    {
        this.role = role;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        UserRole that = (UserRole) o;
        return Objects.equals(user.getId(), that.user.getId()) &&
                Objects.equals(role.getId(),that.role.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, role);
    }
}
