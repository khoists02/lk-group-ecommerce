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

package com.lkgroup.ecommerce.common.domain.support.fragments;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class DynamicEntityGraphImpl<T, S> implements DynamicEntityGraph<T, S> {

    private static final Logger logger = LoggerFactory.getLogger(DynamicEntityGraphImpl.class);

    final JpaEntityInformation<T, ?> entityInformation;
    EntityManager em;
    Class<T> entityType;

    public DynamicEntityGraphImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        this.entityInformation = entityInformation;
        this.em = entityManager;
        this.entityType = entityInformation.getJavaType();
    }

    public EntityGraph<T> createEntityGraph()
    {
        return em.createEntityGraph(this.entityType);
    }

    public Stream<T> findAll(EntityGraph<T> entityGraph) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(this.entityType);
        Root<T> root = criteriaQuery.from(this.entityType);
        criteriaQuery.select(root);
        TypedQuery<T> query = em.createQuery(criteriaQuery);
        this.getProperties(entityGraph).forEach(query::setHint);
        return query.getResultStream();
    }

    public Optional<T> findById(S id, EntityGraph<T> entityGraph) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(this.entityType);
        Root<T> root = criteriaQuery.from(this.entityType);
        criteriaQuery.select(root);
        criteriaQuery.where(criteriaBuilder.equal(root.get(this.entityInformation.getIdAttribute()), id));
        TypedQuery<T> query = em.createQuery(criteriaQuery);
        this.getProperties(entityGraph).forEach(query::setHint);
        try{
            return Optional.of(query.getSingleResult());
        }catch (NoResultException e)
        {
            return Optional.empty();
        }
    }

    private Map<String, Object> getProperties(EntityGraph<T> entityGraph)
    {
        Map<String, Object> properties = new HashMap<>();
        properties.put("jakarta.persistence.fetchgraph", entityGraph);
        return properties;
    }
}
