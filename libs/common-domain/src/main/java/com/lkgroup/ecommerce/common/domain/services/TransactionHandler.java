package com.lkgroup.ecommerce.common.domain.services;

import jakarta.persistence.EntityManager;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

@Service
public class TransactionHandler {
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRED)
    public <T> T runInTransaction(Supplier<T> supplier) {
        return supplier.get();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public <T> T runInTransaction(Callable<T> callable) throws Exception {
        return callable.call();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public <T> T runInReadOnlyTransaction(Callable<T> callable) throws Exception {
        return callable.call();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public <T> T runInReadOnlyTransaction(Supplier<T> supplier) {
        return supplier.get();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public void runInReadOnlyTransaction(Runnable runnable) {
        runnable.run();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void runInTransaction(Runnable runnable) {
        runnable.run();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T> T runInNewTransaction(Supplier<T> supplier) {
        return supplier.get();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void runInNewTransaction(Runnable runnable) {
        runnable.run();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public void runInNewReadOnlyTransaction(Runnable runnable) {
        runnable.run();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T> T runInNewTransaction(Callable<T> callable) throws Exception {
        return callable.call();
    }

    public void executeAfterCommit(Runnable runnable) {
        eventPublisher.publishEvent(new AfterCommitEvent(this, runnable));
    }

    @Getter
    public static class AfterCommitEvent extends ApplicationEvent {
        private final Runnable runnable;

        public AfterCommitEvent(Object source, Runnable runnable) {
            super(source);
            this.runnable = runnable;
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void executeAfterCommit(AfterCommitEvent event) {
        event.getRunnable().run();
    }

    public void flush() {
        entityManager.flush();
    }
}
