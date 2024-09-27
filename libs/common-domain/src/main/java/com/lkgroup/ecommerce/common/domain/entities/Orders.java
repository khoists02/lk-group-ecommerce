package com.lkgroup.ecommerce.common.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Orders extends BaseEntity{
    private int quality;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(
            mappedBy = "orders",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    Set<ProductOrder> productOrders = new HashSet<>();


    public void addProductOrders(ProductOrder productOrder) {
        productOrder.setOrders(this);
        this.productOrders.add(productOrder);
    }

}
