package com.lkgroup.ecommerce.common.validation.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("ValidationApplicationContextHolder")
public class ApplicationContextHolder implements ApplicationContextAware {

    private static ApplicationContext context;

    public static <T extends Object> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

    public static <T extends Object> Optional<T> tryGetBean(Class<T> beanClass) {
        if(context.getBeanNamesForType(beanClass).length == 0)
            return Optional.empty();
        return Optional.of(context.getBean(beanClass));
    }

    public static ApplicationContext getContext()
    {
        return context;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHolder.context = applicationContext;
    }
}
