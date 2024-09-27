package com.lkgroup.ecommerce.common.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "categories")
public class Category extends BaseEntity {
    public enum CategoryType {
        SPORT,
    }

    @Enumerated(EnumType.STRING)
    private CategoryType type;
}
