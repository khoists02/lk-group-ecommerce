package com.lkgroup.ecommerce.common.domain.support.events;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

public class EventDispatchingEntityListener {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PreUpdate
    protected void preUpdate(Object entity)
    {
        eventPublisher.publishEvent(new EntityWillUpdateEvent(this, entity));
    }

    @PrePersist
    protected void prePersist(Object entity)
    {
        eventPublisher.publishEvent(new EntityWillPersistEvent(this, entity));
    }

    @PreRemove
    protected void preRemove(Object entity)
    {
        eventPublisher.publishEvent(new EntityWillRemoveEvent(this, entity));
    }

    @PostLoad
    protected void postLoad(Object entity)
    {
        eventPublisher.publishEvent(new EntityLoadedEvent(this, entity));
    }

    @PostPersist
    protected void postPersist(Object entity)
    {
        eventPublisher.publishEvent(new EntityPersistedEvent(this, entity));
    }

    @PostRemove
    protected void postRemove(Object entity)
    {
        eventPublisher.publishEvent(new EntityRemovedEvent(this, entity));
    }

    @PostUpdate
    protected void postUpdate(Object entity)
    {
        eventPublisher.publishEvent(new EntityUpdatedEvent(this, entity));
    }

}
