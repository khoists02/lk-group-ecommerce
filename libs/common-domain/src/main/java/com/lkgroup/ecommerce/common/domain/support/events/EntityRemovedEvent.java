package com.lkgroup.ecommerce.common.domain.support.events;

public class EntityRemovedEvent extends EntityEvent {
    public EntityRemovedEvent(Object source, Object entity) {
        super(source, entity);
    }
}
