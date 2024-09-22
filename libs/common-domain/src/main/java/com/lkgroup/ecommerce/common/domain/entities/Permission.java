package com.lkgroup.ecommerce.common.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Table(name = "permissions")
@Entity
@Getter
@Setter
public class Permission extends BaseEntity {
    private String name;
    private String description;
    private String group;
}
