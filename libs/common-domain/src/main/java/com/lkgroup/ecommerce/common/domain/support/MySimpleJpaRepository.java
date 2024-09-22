package com.lkgroup.ecommerce.common.domain.support;

import com.lkgroup.ecommerce.common.domain.support.fragments.MyJpaSpecificationExecutor;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class MySimpleJpaRepository<T, ID> extends SimpleJpaRepository<T, ID> implements MyJpaSpecificationExecutor<T> {

    private static Logger logger = LoggerFactory.getLogger(MySimpleJpaRepository.class);

    private EntityManager em;
    private final JpaEntityInformation<T, ?> entityInformation;


    public MySimpleJpaRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
        this.em = entityManager;
    }

    public MySimpleJpaRepository(Class<T> domainClass, EntityManager em) {
        this(JpaEntityInformationSupport.getEntityInformation(domainClass, em), em);
    }

    @Override
    @Transactional
    public void deleteById(ID id) {
        //We did this other delete method previously to ensure the customerId was include as part of the delete clause
        //This has had a side effect meaning the entity isn't loaded and events aren't triggered when its deleted
        //We should use our findById method that will include the customerId in the query, then we can use the standard
        //delete() function to remove it from the entity context.
        Assert.notNull(id, "The given id must not be null!");
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaDelete<T> criteriaQuery = criteriaBuilder.createCriteriaDelete(this.getDomainClass());
        Root<T> root = criteriaQuery.from(this.getDomainClass());
        criteriaQuery.where(criteriaBuilder.equal(root.get(this.entityInformation.getIdAttribute()), id));
        Query query = em.createQuery(criteriaQuery);
        query.executeUpdate();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<T> findById(ID id) {
        Assert.notNull(id, "The given id must not be null!");
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(this.getDomainClass());
        Root<T> root = criteriaQuery.from(this.getDomainClass());
        criteriaQuery.select(root);
        criteriaQuery.where(criteriaBuilder.equal(root.get(this.entityInformation.getIdAttribute()), id));

        TypedQuery<T> query = em.createQuery(criteriaQuery);
        CrudMethodMetadata methodMetadata = this.getRepositoryMethodMetadata();
        if (methodMetadata != null) {
            LockModeType lockModeType = methodMetadata.getLockModeType();
            if (lockModeType != null) {
                query.setLockMode(lockModeType);
            }
            this.getQueryHints().withFetchGraphs(this.em).forEach(query::setHint);
        }
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }


    @Override
    public Optional<T> findOne(Specification<T> specification, EntityGraph<T> entityGraph) {
        try {
            TypedQuery<T> query = this.getQuery(specification, Sort.unsorted());
            this.setEntityGraphHint(query, entityGraph);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException var3) {
            return Optional.empty();
        }
    }

    @Override
    protected <S extends T> TypedQuery<S> getQuery(Specification<S> spec, Class<S> domainClass, Sort sort) {

        Specification<S> scopedSpec = spec;

        /**
         * Before generating the TypedQuery we can add our additional restriction of Organisations for anything implementing
         * the interface.
         * Any Authentication principal can make use of this
         */
        return super.getQuery(scopedSpec, domainClass, sort);
    }

    @Override
    public Stream<T> findAll(Specification<T> specification, EntityGraph<T> entityGraph) {
        return this.findAll(specification, Sort.unsorted(), entityGraph);
    }

    public NoCountSliceImpl<T> findAll(Specification<T> specification, NoCountPageRequest pageable, EntityGraph<T> entityGraph) {
        TypedQuery<T> query = this.getQuery(specification, pageable);
        this.setEntityGraphHint(query, entityGraph);
        return (pageable.isUnpaged() ? new NoCountSliceImpl<>(query.getResultList()) : this.readSlice(query, pageable));
    }

    protected <S extends T> NoCountSliceImpl<T> readSlice(TypedQuery<T> query, NoCountPageRequest pageable) {
        if (pageable.isPaged()) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize() + 1);
        }

        List<T> resultsList = query.getResultList();
        boolean hasNext = resultsList.size() == pageable.getPageSize() + 1;
        if (hasNext)
            resultsList.remove(resultsList.size() - 1);
        return new NoCountSliceImpl<>(resultsList, pageable, hasNext);
    }

    @Override
    public Page<T> findAll(Specification<T> specification, Pageable pageable, EntityGraph<T> entityGraph) {
        TypedQuery<T> query = this.getQuery(specification, pageable);
        this.setEntityGraphHint(query, entityGraph);
        return (pageable.isUnpaged() ? new PageImpl(query.getResultList()) : this.readPage(query, this.getDomainClass(), pageable, specification));
    }

    @Override
    public Stream<T> findAll(Specification<T> specification, Sort sort, EntityGraph<T> entityGraph) {
        TypedQuery<T> query = this.getQuery(specification, sort);
        this.setEntityGraphHint(query, entityGraph);
        return query.getResultStream();
    }

    protected void setEntityGraphHint(Query query, EntityGraph<T> entityGraph) {
        if (entityGraph != null)
            query.setHint("jakarta.persistence.fetchgraph", entityGraph);
    }
}
