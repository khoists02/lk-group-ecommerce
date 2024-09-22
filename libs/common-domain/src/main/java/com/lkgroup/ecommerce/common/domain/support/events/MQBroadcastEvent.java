package com.lkgroup.ecommerce.common.domain.support.events;

import org.springframework.context.ApplicationEvent;

public abstract class MQBroadcastEvent extends ApplicationEvent {
    public MQBroadcastEvent(Object source) {
        super(source);
    }

    public String getExchange() {
        return "broadcast.events";
    }

    public abstract String getRoutingKey();

    public abstract Object getBody();

}
