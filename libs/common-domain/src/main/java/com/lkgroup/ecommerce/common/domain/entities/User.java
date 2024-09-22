package com.lkgroup.ecommerce.common.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

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

}
