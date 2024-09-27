package com.lkgroup.ecommerce.common.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "products_orders")
public class ProductOrder extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders orders;

    public ProductOrder() {

    }

    public ProductOrder(Product product, Orders orders) {
        this.product = product;
        this.orders = orders;
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, orders);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        ProductOrder that = (ProductOrder) o;
        return Objects.equals(product.getId(), that.product.getId()) &&
                Objects.equals(orders.getId(), that.orders.getId());
    }
}
