package com.lkgroup.ecommerce.common.domain.support.events;

public class EntityWillRemoveEvent extends EntityEvent {
    public EntityWillRemoveEvent(Object source, Object entity) {
        super(source, entity);
    }
}
