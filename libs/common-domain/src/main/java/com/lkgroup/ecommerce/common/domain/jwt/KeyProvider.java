package com.lkgroup.ecommerce.common.domain.jwt;


import com.lkgroup.ecommerce.common.domain.entities.AuthenticationKeyIssuer;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

public interface KeyProvider {

    KeyWithId getSigningKey(String tokenIssuer);

    List<KeyWithId> getPublicKeys(AuthenticationKeyIssuer issuer);

    PublicKey getPublicKey(String kid);

    PrivateKey getPrivateKey(String kid);

}
