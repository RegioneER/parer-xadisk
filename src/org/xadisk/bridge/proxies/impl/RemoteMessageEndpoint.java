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
import org.xadisk.bridge.proxies.facilitators.RemoteMethodInvoker;
import org.xadisk.bridge.proxies.facilitators.RemoteObjectProxy;
import java.lang.reflect.Method;
import javax.resource.ResourceException;
import javax.resource.spi.endpoint.MessageEndpoint;
import org.xadisk.bridge.proxies.facilitators.MethodSerializabler;
import org.xadisk.bridge.proxies.facilitators.SerializedMethod;
import org.xadisk.connector.inbound.FileSystemEventListener;
import org.xadisk.filesystem.FileSystemStateChangeEvent;

public class RemoteMessageEndpoint extends RemoteObjectProxy implements MessageEndpoint, FileSystemEventListener {

    private static final long serialVersionUID = 1L;
    
    public RemoteMessageEndpoint(long objectId, RemoteMethodInvoker invoker) {
        super(objectId, invoker);
    }

    public void beforeDelivery(Method method) throws NoSuchMethodException, ResourceException {
        try {
            SerializedMethod serializableMethod = new MethodSerializabler().serialize(method);
            invokeRemoteMethod("beforeDelivery", serializableMethod);
        } catch (NoSuchMethodException nsme) {
            throw nsme;
        } catch (ResourceException re) {
            throw re;
        } catch (Throwable th) {
            throw assertExceptionHandling(th);
        }
    }

    public void onFileSystemEvent(FileSystemStateChangeEvent event) {
        try {
            invokeRemoteMethod("onFileSystemEvent", event);
        } catch (Throwable th) {
            throw assertExceptionHandling(th);
        }
    }

    public void afterDelivery() throws ResourceException {
        try {
            invokeRemoteMethod("afterDelivery");
        } catch (ResourceException re) {
            throw re;
        } catch (Throwable th) {
            throw assertExceptionHandling(th);
        }
    }

    public void release() {
        try {
            invokeRemoteMethod("release");
        } catch (Throwable th) {
            throw assertExceptionHandling(th);
        }
    }

    public void shutdown() {
        disconnect();
    }
}
