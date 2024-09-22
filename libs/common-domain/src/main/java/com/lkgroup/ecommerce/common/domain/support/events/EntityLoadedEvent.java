package com.lkgroup.ecommerce.common.domain.support.events;

public class EntityLoadedEvent extends EntityEvent {
    public EntityLoadedEvent(Object source, Object entity) {
        super(source, entity);
    }
}
