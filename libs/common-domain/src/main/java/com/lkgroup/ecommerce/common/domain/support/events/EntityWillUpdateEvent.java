package com.lkgroup.ecommerce.common.domain.support.events;

public class EntityWillUpdateEvent extends EntityEvent {
    public EntityWillUpdateEvent(Object source, Object entity) {
        super(source, entity);
    }
}
