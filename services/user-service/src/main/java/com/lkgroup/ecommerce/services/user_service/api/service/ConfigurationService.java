package com.lkgroup.ecommerce.services.user_service.api.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationService {
    @Getter
    @Value("${ecommerce.userservice.rootdomain}")
    private String rootDomain;
}
