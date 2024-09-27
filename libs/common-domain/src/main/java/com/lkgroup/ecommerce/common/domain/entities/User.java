package com.lkgroup.ecommerce.common.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    @NotNull
    private String username;
    @NotNull
    private String password;
    private String name;
    private String email;
    private String address;
    private String phone;
    @Column(name = "image_path")
    private String imagePath;
    private boolean enabled;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = Orders_.USER, cascade = {CascadeType.ALL})
    private List<Orders> orders;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = UserRole_.USER, cascade = {CascadeType.ALL},
            orphanRemoval = true)
    private Set<UserRole> userRoles = new HashSet<>();

    public void addRole(Role role) {
        this.userRoles.add(new UserRole(this, role));
    }

    public void removeRole(Role role) {
        this.userRoles.remove(new UserRole(this, role));
    }

    public void addRoles(Set<Role> roles) {
        this.userRoles.addAll(
                roles.stream().map(r -> new UserRole(this, r)).collect(Collectors.toSet())
        );
    }

    public void setRoles(Set<Role> roles) {
        this.userRoles.retainAll(roles.stream().map(r -> new UserRole(this, r)).collect(Collectors.toSet()));
        this.addRoles(roles);
    }

    public Set<Role> getRoles() {
        return this.userRoles.stream().map(UserRole::getRole).collect(Collectors.toSet());
    }

}
