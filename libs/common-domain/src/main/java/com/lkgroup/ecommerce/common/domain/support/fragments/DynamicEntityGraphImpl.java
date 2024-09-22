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
