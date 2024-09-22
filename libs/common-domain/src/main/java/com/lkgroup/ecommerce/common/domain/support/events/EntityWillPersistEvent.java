package com.lkgroup.ecommerce.common.domain.support.events;

public class EntityWillPersistEvent extends EntityEvent {
    public EntityWillPersistEvent(Object source, Object entity) {
        super(source, entity);
    }
}
