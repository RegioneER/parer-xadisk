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

import javax.resource.ResourceException;
import org.xadisk.connector.outbound.XADiskConnection;
import org.xadisk.connector.outbound.XADiskConnectionFactory;

/**
 * This interface is applicable only when invoking XADisk as a JCA Resource Adapter.
 * <p> This interface is a marker for connection factories to connect to remote (running on remote JVMs)
 * XADisk instances. Specifying name of this interface is normally required when creating a connection factory
 * in a JavaEE server to connect to remote XADisk instances.
 *
 * <p> It is recommended that code inside JavaEE application use the interface {@link XADiskConnectionFactory}
 * (and not this one) for interacting with instances of both {@link XADiskConnectionFactory} and
 * {@link XADiskRemoteConnectionFactory}.
 *
 * @since 1.0
 */

public interface XADiskRemoteConnectionFactory extends XADiskConnectionFactory {

    /**
     * Retrieves a new connection handle to interact with the remote XADisk instance.
     * @return a new connection handle.
     * @throws ResourceException
     */
    public XADiskConnection getConnection() throws ResourceException;
    
}
