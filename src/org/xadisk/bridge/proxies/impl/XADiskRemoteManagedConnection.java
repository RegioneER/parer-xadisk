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
import org.xadisk.connector.outbound.XADiskManagedConnection;
import org.xadisk.filesystem.NativeXAFileSystem;

public class XADiskRemoteManagedConnection extends XADiskManagedConnection {

    public XADiskRemoteManagedConnection(String serverAddress, Integer serverPort, NativeXAFileSystem localXAFileSystem) throws IOException {
        super(new RemoteXAFileSystem(serverAddress, serverPort, localXAFileSystem), "dummy-value");
    }

    @Override
    public void cleanup() throws ResourceException {
        super.cleanup();
        ((RemoteXAFileSystem) theXAFileSystem).shutdown();
        //we shouldn't switch the xaFS object because the container might still
        //be using the "older" xaResource object which further points to the older
        //xaFS object. So, we rather keep using the "older" "xaFS" and don't reset it.
        //super.theXAFileSystem = new RemoteXAFileSystem(serverAddress, serverPort);
    }

    @Override
    public void destroy() throws ResourceException {
        super.destroy();
        ((RemoteXAFileSystem) theXAFileSystem).shutdown();
    }
}
