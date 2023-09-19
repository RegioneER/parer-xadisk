/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package org.xadisk.connector.outbound;

import javax.resource.spi.ConnectionEvent;
import org.xadisk.filesystem.exceptions.NoTransactionAssociatedException;
import org.xadisk.filesystem.FileSystemConfiguration;

/**
 * This class is applicable only when invoking XADisk as a JCA Resource Adapter.
 * <p> This represents a transaction object which can be used by JavaEE applications
 * when they want to control the transaction on an {@link XADiskConnection} by themselves
 * (in a resource-local way, not using XA transaction). Applications need to call
 * {@link XADiskConnection#getUserLocalTransaction getUserLocalTransaction} to obtain a handle
 * to this transaction object.
 *
 * @since 1.0
 */

public class XADiskUserLocalTransaction {

    private final XADiskLocalTransaction localTxnImpl;
    private final XADiskManagedConnection mc;

    XADiskUserLocalTransaction(XADiskManagedConnection mc) {
        this.localTxnImpl = new XADiskLocalTransaction(mc);
        this.mc = mc;
    }

    /**
     * Starts a local transaction on the associated connection, and binds that
     * transaction to this object.
     */
    public void beginLocalTransaction() {
        localTxnImpl._begin();
        mc.raiseUserLocalTransactionEvent(ConnectionEvent.LOCAL_TRANSACTION_STARTED);
    }

    /**
     * Commits the local transaction bound to this object.
     * @throws NoTransactionAssociatedException
     */
    public void commitLocalTransaction() throws NoTransactionAssociatedException {
        localTxnImpl._commit();
        mc.raiseUserLocalTransactionEvent(ConnectionEvent.LOCAL_TRANSACTION_COMMITTED);
    }

    /**
     * Rolls back the local transaction bound to this object.
     * @throws NoTransactionAssociatedException
     */
    public void rollbackLocalTransaction() throws NoTransactionAssociatedException {
        localTxnImpl._rollback();
        mc.raiseUserLocalTransactionEvent(ConnectionEvent.LOCAL_TRANSACTION_ROLLEDBACK);
    }

    /**
     * Gets the transaction timeout value for the current transaction.
     * <p> Default value is obtained from the {@link FileSystemConfiguration#getTransactionTimeout()
     * global-configuration}.
     * @return the transaction timeout value, in seconds.
     */
    public int getTransactionTimeOut() {
        return localTxnImpl.getTransactionTimeOut();
    }

    /**
     * Sets the transaction timeout value for the current transaction.
     * <p> Default value is obtained from the {@link FileSystemConfiguration#getTransactionTimeout()
     * global-configuration}.
     * @param transactionTimeOut the new transaction timeout value, in seconds.
     */
    public void setTransactionTimeOut(int transactionTimeOut) {
        localTxnImpl.setTransactionTimeOut(transactionTimeOut);
    }
}
