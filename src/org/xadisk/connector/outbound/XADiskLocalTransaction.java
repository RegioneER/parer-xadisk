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

import javax.resource.ResourceException;
import javax.resource.spi.LocalTransaction;
import org.xadisk.filesystem.XAFileSystemCommonness;
import org.xadisk.filesystem.exceptions.NoTransactionAssociatedException;

public class XADiskLocalTransaction implements LocalTransaction {

    private final XADiskManagedConnection mc;
    private int transactionTimeOut;

    public XADiskLocalTransaction(XADiskManagedConnection mc) {
        this.mc = mc;
        XAFileSystemCommonness xaFileSystem = (XAFileSystemCommonness) mc.getUnderlyingXAFileSystem();
        this.transactionTimeOut = xaFileSystem.getDefaultTransactionTimeout();
    }

    void _begin() {
        mc.setTypeOfCurrentTransaction(XADiskManagedConnection.LOCAL_TRANSACTION);
        mc.refreshSessionForBeginLocalTransaction().setTransactionTimeout(transactionTimeOut);
    }

    public void begin() throws ResourceException {
        _begin();
    }

    void _rollback() throws NoTransactionAssociatedException {
        mc.setTypeOfCurrentTransaction(XADiskManagedConnection.NO_TRANSACTION);
        mc.getSessionOfLocalTransaction().rollback();
    }

    public void rollback() throws ResourceException {
        try {
            _rollback();
        } catch (NoTransactionAssociatedException note) {
            throw new ResourceException(note);
        }
    }

    void _commit() throws NoTransactionAssociatedException {
        mc.setTypeOfCurrentTransaction(XADiskManagedConnection.NO_TRANSACTION);
        mc.getSessionOfLocalTransaction().commit();
    }

    public void commit() throws ResourceException {
        try {
            _commit();
        } catch (NoTransactionAssociatedException note) {
            throw new ResourceException(note);
        }
    }

    int getTransactionTimeOut() {
        return transactionTimeOut;
    }

    void setTransactionTimeOut(int transactionTimeOut) {
        this.transactionTimeOut = transactionTimeOut;
    }
}
