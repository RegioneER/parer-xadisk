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

import javax.transaction.Transaction;
import javax.transaction.xa.XAResource;
import org.xadisk.connector.outbound.XADiskConnection;

/**
 * This interface, like {@link Session}, is used by applications running in non-JCA
 * environments. While {@link Session} does not support XA/JTA Transactions, this
 * interface allows applications to use a JTA Transaction Manager to bind
 * XADisk and other XA-enabled resources with a single XA/JTA Transaction. This is
 * possible due to the {@link XAResource} implementation returned by the
 * {@link #getXAResource() getXAResource} method of this interface.
 * <p> An instance of this interface can be obtained from
 * {@link XAFileSystem#createSessionForXATransaction() createSessionForXATransaction}.
 * <p> For applications in JCA environments, use of {@link XADiskConnection} interface
 * should be preferable instead of this interface.
 * @since 1.1
 */
public interface XASession extends XADiskBasicIOOperations {

    /**
     * Returns the XADisk implementation of the standard {@link XAResource} interface.
     * <p> This {@link XAResource} can be enlisted into an XA/JTA Transaction
     * using {@link Transaction#enlistResource(XAResource) enlistResource} and hence allows the
     * {@link XASession} to participate in the XA/JTA Transaction.
     * <p> This {@link XAResource} implementation is fully compliant with JTA
     * and hence supports features like suspend/resume, one-phase commit
     * optimization, crash recovery, transaction time-out etc.
     * @return the XADisk implementation of {@link XAResource}.
     */
    public XAResource getXAResource();
}
