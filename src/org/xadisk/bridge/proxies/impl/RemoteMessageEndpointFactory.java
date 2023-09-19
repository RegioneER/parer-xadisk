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
import org.xadisk.bridge.proxies.facilitators.SerializedMethod;
import java.lang.reflect.Method;
import javax.resource.spi.UnavailableException;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;
import org.xadisk.bridge.proxies.facilitators.MethodSerializabler;
import org.xadisk.filesystem.NativeXAFileSystem;
import org.xadisk.bridge.server.conversation.HostedContext;

public class RemoteMessageEndpointFactory extends RemoteObjectProxy implements MessageEndpointFactory {

    private static final long serialVersionUID = 1L;

    private final String xaDiskSystemId;
    private transient NativeXAFileSystem localXAFileSystem;

    public RemoteMessageEndpointFactory(long objectId, String xaDiskSystemId, RemoteMethodInvoker invoker) {
        super(objectId, invoker);
        this.xaDiskSystemId = xaDiskSystemId;
    }

    public void setLocalXAFileSystem(NativeXAFileSystem localXAFileSystem) {
        this.localXAFileSystem = localXAFileSystem;
    }

    synchronized public boolean isDeliveryTransacted(Method method) throws NoSuchMethodException {
        try {
            SerializedMethod serializableMethod = new MethodSerializabler().serialize(method);
            return (Boolean) invokeRemoteMethod("isDeliveryTransacted", serializableMethod);
        } catch (NoSuchMethodException nsme) {
            throw nsme;
        } catch (Throwable th) {
            throw assertExceptionHandling(th);
        }
    }

    synchronized public MessageEndpoint createEndpoint(XAResource xar) throws UnavailableException {
        try {
            HostedContext globalCallbackContext = localXAFileSystem.getGlobalCallbackContext();
            long objectId = globalCallbackContext.hostObject(xar);
            //one problem was to deHost this xar at an appropriate time when its use is done. But its
            //use may not be done with the end of mep.release(). Who knows, the remote TM sends txn
            //related commands to this xar at some point quite later. No. Not so. Good news came from
            //the JCA spec: "During the afterDelivery call from the resource adapter, the application server
            //completes the transaction and sends transaction completion notifications to the
            //XAResource instance".
            RemoteEventProcessingXAResource remoteEventProcessingXAResource = new RemoteEventProcessingXAResource(objectId,
                    localXAFileSystem.createRemoteMethodInvokerToSelf());
            RemoteMessageEndpoint remoteMEP =
                    (RemoteMessageEndpoint) invokeRemoteMethod("createEndpoint", remoteEventProcessingXAResource);
            remoteMEP.setInvoker((RemoteMethodInvoker) this.invoker.makeCopy());
            return remoteMEP;
        } catch (UnavailableException ue) {
            throw ue;
        } catch (Throwable th) {
            throw assertExceptionHandling(th);
        }
    }

    public void shutdown() {
        disconnect();
    }

    /*
     * JCA spec dis-allows equals method for this bean. For this case,
     * remoteObjectId infact follows the remote unique object identities for the
     * original MEF objects.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RemoteMessageEndpointFactory) {
            RemoteMessageEndpointFactory that = (RemoteMessageEndpointFactory) obj;
            return this.remoteObjectId == that.remoteObjectId
                    && this.xaDiskSystemId.equals(that.xaDiskSystemId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return xaDiskSystemId.hashCode() + (int) remoteObjectId;
    }

    public String getXaDiskSystemId() {
        return xaDiskSystemId;
    }

    public long getRemoteObjectId() {
        return remoteObjectId;
    }

    public RemoteMethodInvoker getInvoker() {
        return invoker;
    }
}
