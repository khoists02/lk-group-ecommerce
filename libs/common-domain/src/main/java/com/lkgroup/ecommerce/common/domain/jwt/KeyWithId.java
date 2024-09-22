package com.lkgroup.ecommerce.common.domain.jwt;

import lombok.Data;

import java.io.Serializable;
import java.security.Key;
import java.time.ZonedDateTime;

@Data
public class KeyWithId implements Serializable {

    private Key key;
    private String kid;
    private ZonedDateTime notBefore;

    public KeyWithId(String kid, Key key, ZonedDateTime notBefore)
    {
        this.key = key;
        this.kid = kid;
        this.notBefore = notBefore;
    }

    public static KeyWithId from(String kid, Key key, ZonedDateTime notBefore)
    {
        return new KeyWithId(kid, key, notBefore);
    }

    public static KeyWithId from(String kid, Key key)
    {
        return new KeyWithId(kid, key, null);
    }

}
