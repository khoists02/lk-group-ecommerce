package com.lkgroup.ecommerce.common.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "authentication_key_issuers")
public class AuthenticationKeyIssuer extends BaseEntity {
    private String issuer;
    private ZonedDateTime notBefore;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
