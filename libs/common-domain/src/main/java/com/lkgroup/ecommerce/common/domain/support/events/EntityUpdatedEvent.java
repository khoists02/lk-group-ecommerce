package com.lkgroup.ecommerce.common.domain.support.events;

public class EntityUpdatedEvent extends EntityEvent {
    public EntityUpdatedEvent(Object source, Object entity) {
        super(source, entity);
    }
}
