package com.lkgroup.ecommerce.common.domain.support.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public abstract class EntityEvent extends ApplicationEvent {

    @Getter
    protected Object entity;

    public EntityEvent(Object source, Object entity) {
        super(source);
        this.entity = entity;
    }
}
