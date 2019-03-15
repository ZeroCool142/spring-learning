package ru.iteco.springl.util;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;

public class RollbackInterceptor extends EmptyInterceptor {

    private boolean wasCommitted;

    @Override
    public void beforeTransactionCompletion(Transaction tx) {
        wasCommitted = true;
    }

    public boolean wasCommitted() {
        return wasCommitted;
    }
}
