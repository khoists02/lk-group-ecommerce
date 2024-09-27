package com.lkgroup.ecommerce.common.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product extends BaseEntity {
    @Column(name = "product_name")
    private String productName;
    private Long price;
    @Column(name = "path_image")
    private String pathImage;
    private String description;
    @Column(name = "sub_description")
    private String subDescription;

    @OneToOne
    @JoinColumn(name = "category_id")
    private Category category;

}
