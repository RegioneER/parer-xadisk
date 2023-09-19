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

package org.xadisk.bridge.proxies.interfaces;

import org.xadisk.connector.outbound.XADiskConnection;
import org.xadisk.filesystem.FileSystemConfiguration;
import org.xadisk.filesystem.exceptions.NoTransactionAssociatedException;

/**
 * This interface is used to invoke i/o operations on XADisk and to control the transaction 
 * associated with this session (use {@link XADiskConnection} instead of this interface when XADisk is 
 * used as a JCA Resource Adapter. For XA transactions in non-JCA environments, use {@link XASession}).
 * An instance of this interface can be obtained from
 * {@link XAFileSystem#createSessionForLocalTransaction() createSessionForLocalTransaction}.
 * <p> Before the session object is returned, a new transaction is associated with it. Once a
 * session completes its associated transaction using {@link #commit()} or
 * {@link #rollback()} or automatically via implicit rollback (e.g. due to transaction time out),
 * it can no more be used. To start another transaction, one needs to obtain another
 * session object.
 *
 * @since 1.0
 */

public interface Session extends XADiskBasicIOOperations {
    
    /**
     * Sets the transaction timeout value for the transaction associated with this session.
     * <p> Default value is obtained from the {@link FileSystemConfiguration#getTransactionTimeout()
     * global-configuration}.
     * @param transactionTimeout the new transaction timeout value, in seconds.
     * @return true, if the operation succeeds.
     */
    public boolean setTransactionTimeout(int transactionTimeout);

    /**
     * Returns the current transaction timeout value.
     * <p> Default value is obtained from the {@link FileSystemConfiguration#getTransactionTimeout()
     * global-configuration}.
     * @return the transaction timeout value, in seconds.
     */
    public int getTransactionTimeout();

    /**
     * Rolls back the transaction associated with this Session.
     * @throws NoTransactionAssociatedException
     */
    public void rollback() throws NoTransactionAssociatedException;

    /**
     * Commits the transaction associated with this Session.
     * @throws NoTransactionAssociatedException
     */
    public void commit() throws NoTransactionAssociatedException;
}
