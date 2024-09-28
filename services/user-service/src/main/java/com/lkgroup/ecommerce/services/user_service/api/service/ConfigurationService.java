package com.lkgroup.ecommerce.services.user_service.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationService {
    @Value("${ECOMMERCE_USERSERVICE_HOSTNAME}")
    private String rootDomain;

    public String getRootDomain() {
        return rootDomain;
    }
}
