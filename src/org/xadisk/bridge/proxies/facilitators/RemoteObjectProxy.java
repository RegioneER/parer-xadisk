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

package org.xadisk.bridge.proxies.facilitators;

import java.io.IOException;
import java.io.Serializable;

public class RemoteObjectProxy implements Serializable {
    
    private static final long serialVersionUID = 1L;

    protected final long remoteObjectId;
    protected RemoteMethodInvoker invoker;

    public RemoteObjectProxy(long remoteObjectId, RemoteMethodInvoker invoker) {
        this.remoteObjectId = remoteObjectId;
        this.invoker = invoker;
    }

    public void setInvoker(RemoteMethodInvoker invoker) {
        this.invoker = invoker;
    }
    
    protected RuntimeException assertExceptionHandling(Throwable t) {
        if(t instanceof RuntimeException) {
            return (RuntimeException)t;
        } else {
            throw new AssertionError(t);
        }
    }

    protected Object invokeRemoteMethod(String methodName, Serializable... args) throws Throwable {
        Object response = invoker.invokeRemoteMethod(remoteObjectId, methodName, args);
        if(response instanceof RemoteObjectProxy) {
            ((RemoteObjectProxy)response).setInvoker(this.invoker);
        }
        return response;
    }

    public long getRemoteObjectId() {
        return remoteObjectId;
    }

    public void disconnect() {
        try {
            this.invoker.disconnect();
        } catch (IOException ioe) {
            //no-op.
        }
    }
}
