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

package org.xadisk.bridge.proxies.impl;

import java.io.IOException;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.security.auth.Subject;
import org.xadisk.connector.outbound.XADiskConnectionFactoryImpl;
import org.xadisk.connector.outbound.XADiskManagedConnectionFactory;
import org.xadisk.filesystem.NativeXAFileSystem;

public class XADiskRemoteManagedConnectionFactory extends XADiskManagedConnectionFactory {

    private static final long serialVersionUID = 1L;
    
    private String serverAddress;
    private Integer serverPort;

    public XADiskRemoteManagedConnectionFactory() {
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo cri)
            throws ResourceException {
        try {
            return new XADiskRemoteManagedConnection(serverAddress, serverPort, NativeXAFileSystem.getXAFileSystem(super.getInstanceId()));
        } catch (IOException ioe) {
            throw new ResourceException(ioe);
        }
    }

    @Override
    public Object createConnectionFactory(ConnectionManager cm) throws ResourceException {
        return new XADiskConnectionFactoryImpl(this, cm);
    }

    @Override
    public int hashCode() {
        return serverAddress.hashCode() + serverPort.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof XADiskRemoteManagedConnectionFactory) {
            XADiskRemoteManagedConnectionFactory that = (XADiskRemoteManagedConnectionFactory) obj;
            return (that.serverAddress == null ? this.serverAddress == null : that.serverAddress.equalsIgnoreCase(this.serverAddress)) && that.serverPort.equals(this.serverPort);
        }
        return false;
    }
}
