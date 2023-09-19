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

import org.xadisk.bridge.proxies.interfaces.XADiskBasicIOOperations;

/**
 * This interface is applicable only when invoking XADisk as a JCA Resource Adapter.
 * <p> This interface represents the connection object used inside JavaEE applications
 * for calling I/O operations on XADisk. An instance of this can be obtained from
 * {@link XADiskConnectionFactory#getConnection() getConnection} method.
 *
 * @since 1.0
 */
public interface XADiskConnection extends XADiskBasicIOOperations {

    /**
     * Returns an instance of local transaction object which can be used by JavaEE applications
     * to control the transaction on this connection by themselves (in a resource-local way, not
     * using XA transaction).
     * @return a transaction object for demarcation of local transactions on this connection.
     */
    public XADiskUserLocalTransaction getUserLocalTransaction();

    /**
     * Closes this connection. This connection object can't be used after closing it.
     */
    public void close();
}
