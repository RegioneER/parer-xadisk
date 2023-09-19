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

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import org.xadisk.bridge.proxies.interfaces.XADiskRemoteConnectionFactory;

public class XADiskConnectionFactoryImpl implements XADiskConnectionFactory, XADiskRemoteConnectionFactory {

    private static final long serialVersionUID = 1L;
    
    private final XADiskManagedConnectionFactory mcf;
    private final ConnectionManager cm;
    private Reference ref;

    public XADiskConnectionFactoryImpl(XADiskManagedConnectionFactory mcf, ConnectionManager cm) {
        this.mcf = mcf;
        this.cm = cm;
    }

    public XADiskConnection getConnection() throws ResourceException {
        return (XADiskConnection) cm.allocateConnection(mcf, null);
    }

    public Reference getReference() throws NamingException {
        return ref;
    }

    public void setReference(Reference ref) {
        this.ref = ref;
    }
}
