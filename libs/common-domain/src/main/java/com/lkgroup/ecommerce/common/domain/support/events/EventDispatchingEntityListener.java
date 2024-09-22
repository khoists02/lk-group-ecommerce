/*
 * AdvaHealth Solutions Pty. Ltd. ("AHS") CONFIDENTIAL
 * Copyright (c) 2022 AdvaHealth Solutions Pty. Ltd. All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property of AHS. The intellectual and technical concepts contained
 * herein are proprietary to AHS and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 * from AHS.  Access to the source code contained herein is hereby forbidden to anyone except current AHS employees, managers or contractors who have executed
 * Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 * The copyright notice above does not evidence any actual or intended publication or disclosure  of  this source code, which includes
 * information that is confidential and/or proprietary, and is a trade secret, of AHS. ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC  PERFORMANCE,
 * OR PUBLIC DISPLAY OF OR THROUGH USE  OF THIS  SOURCE CODE  WITHOUT  THE EXPRESS WRITTEN CONSENT OF COMPANY IS STRICTLY PROHIBITED, AND IN VIOLATION OF APPLICABLE
 * LAWS AND INTERNATIONAL TREATIES.  THE RECEIPT OR POSSESSION OF  THIS SOURCE CODE AND/OR RELATED INFORMATION DOES NOT CONVEY OR IMPLY ANY RIGHTS
 * TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO MANUFACTURE, USE, OR SELL ANYTHING THAT IT  MAY DESCRIBE, IN WHOLE OR IN PART.
 */

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
