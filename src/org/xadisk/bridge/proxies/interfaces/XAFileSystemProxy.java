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

import org.xadisk.bridge.proxies.impl.RemoteXAFileSystem;
import org.xadisk.filesystem.NativeXAFileSystem;
import org.xadisk.filesystem.standalone.StandaloneFileSystemConfiguration;

/**
 * This is a utility class for booting new XADisk instances and obtaining
 * references to existing XADisk instances on the same JVM or remote JVMs.
 * <p> There are three utility methods in this class. All these
 * three methods return an object implementing {@link XAFileSystem} interface,
 * which is a reference to either a native XADisk instance, or a remote XADisk instance
 * depending on the method used.
 *
 * @since 1.0
 */

public abstract class XAFileSystemProxy {

    /**
     * If an application wants to connect to an already-booted XADisk instance in the same
     * JVM (native XADisk instance), it can call this method.
     * @param instanceId the instance-id of the XADisk instance.
     * @return a reference to the native XADisk instance if an XADisk instance has already
     * been booted in the same JVM; otherwise <code> null </code>.
     */
    public static XAFileSystem getNativeXAFileSystemReference(String instanceId) {
        return NativeXAFileSystem.getXAFileSystem(instanceId);
    }

    /**
     * If an application wants to boot an XADisk instance in the same JVM (native XADisk instance),
     * it can call this method.
     * @param configuration the configuration object specifying the settings with which the
     * XADisk instance should be booted.
     * @return a reference to the native XADisk instance.
     */
    public static XAFileSystem bootNativeXAFileSystem(StandaloneFileSystemConfiguration configuration) {
        return NativeXAFileSystem.bootXAFileSystemStandAlone(configuration);
    }

    /**
     * If an application wants to connect to an XADisk instance running on a remote
     * JVM, it can call this method.
	 * Such reference to remote XADisk instance and all of the subsequently derived
	 * {@link Session} objects share a common communication channel with the remote
	 * XADisk instance, and hence cannot be used by multiple threads.
     * @param serverAddres the network address/name of the remote machine on which the
     * XADisk instance is running.
     * @param serverPort the network port of the remote machine on which the XADisk
     * instance is listening for requests.
     * @return a reference to the remote XADisk instance.
     */
    public static XAFileSystem getRemoteXAFileSystemReference(String serverAddres, int serverPort) {
        return new RemoteXAFileSystem(serverAddres, serverPort);
    }
}
