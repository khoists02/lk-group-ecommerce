package com.lkgroup.ecommerce.common.domain.support;

import com.lkgroup.ecommerce.common.domain.support.fragments.DynamicEntityGraph;
import com.lkgroup.ecommerce.common.domain.support.fragments.DynamicEntityGraphImpl;
import com.lkgroup.ecommerce.common.domain.support.fragments.MyJpaSpecificationExecutor;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition.RepositoryFragments;
import org.springframework.data.repository.core.support.RepositoryFragment;

import java.io.Serializable;

public class MyJpaRepositoryFactory extends JpaRepositoryFactory {

    private static final Logger logger = LoggerFactory.getLogger(MyJpaRepositoryFactory.class);

    private EntityManager entityManager;

    public MyJpaRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
        this.entityManager = entityManager;
    }

    @Override
    protected RepositoryFragments getRepositoryFragments(RepositoryMetadata metadata) {
        RepositoryFragments fragments = super.getRepositoryFragments(metadata);

        if(DynamicEntityGraph.class.isAssignableFrom(metadata.getRepositoryInterface()))
        {
            JpaEntityInformation<?, Serializable> entityInformation = this.getEntityInformation(metadata.getDomainType());
            fragments = fragments.append(RepositoryFragment.implemented(DynamicEntityGraph.class, new DynamicEntityGraphImpl(entityInformation, entityManager)));
        }

        return fragments;
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        if(MyJpaSpecificationExecutor.class.isAssignableFrom(metadata.getRepositoryInterface()))
        {
            return MySimpleJpaRepository.class;
        }
        return super.getRepositoryBaseClass(metadata);
    }
}
