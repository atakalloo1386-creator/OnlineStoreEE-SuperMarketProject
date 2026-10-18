package com.mftplus.mftonlinestoreee.integration.tomee;

import jakarta.annotation.Nonnull;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.UserTransaction;
import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class TomEEJtaPlatform extends AbstractJtaPlatform {

    private static final String TRANSACTION_MANAGER_NAME = "java:comp/TransactionManager";
    private static final String USER_TRANSACTION_NAME = "java:comp/UserTransaction";


    @Nonnull
    @Override
    protected TransactionManager locateTransactionManager() {
        return (TransactionManager) lookup(TRANSACTION_MANAGER_NAME);
    }

    @Nonnull
    @Override
    protected UserTransaction locateUserTransaction() {
        return (UserTransaction) lookup(USER_TRANSACTION_NAME);
    }

    private Object lookup(String name) {
        try {
            return new InitialContext().lookup(name);
        } catch (NamingException e) {
            throw new IllegalStateException("Could not look up " + name + " from TomEE", e);
        }
    }
}
