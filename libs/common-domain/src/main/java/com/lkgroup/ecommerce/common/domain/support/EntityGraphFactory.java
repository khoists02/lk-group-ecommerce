package com.lkgroup.ecommerce.common.domain.support;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class EntityGraphFactory {

    @Autowired
    private ApplicationContext context;
    private static EntityGraphFactory self;

    @PostConstruct
    private void link()
    {
        self = this;
    }

    public static <T> EntityGraph<T> create(Class<T> clazz)
    {
        return self.context.getBean(EntityManager.class).createEntityGraph(clazz);
    }

}
