package com.lkgroup.ecommerce.common.domain.support.events;

import java.util.UUID;

// An Entity implementing this interface will be broadcast via RabbitMQ when it is created, updated or deleted
public interface BroadcastsLifecycleEvent {
    UUID getId();
}
