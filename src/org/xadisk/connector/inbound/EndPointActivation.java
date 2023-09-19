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

package org.xadisk.connector.inbound;

import java.io.Serializable;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import org.xadisk.bridge.proxies.impl.RemoteMessageEndpointFactory;
import org.xadisk.filesystem.NativeXAFileSystem;

public class EndPointActivation implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private final MessageEndpointFactory messageEndpointFactory;
    private final XADiskActivationSpecImpl activationSpecImpl;

    public EndPointActivation(MessageEndpointFactory messageEndpointFactory, XADiskActivationSpecImpl activationSpecImpl) {
        this.messageEndpointFactory = messageEndpointFactory;
        this.activationSpecImpl = activationSpecImpl;
    }

    public MessageEndpointFactory getMessageEndpointFactory() {
        return messageEndpointFactory;
    }

    public XADiskActivationSpecImpl getActivationSpecImpl() {
        return activationSpecImpl;
    }

    public void setLocalXAFileSystemForRemoteMEF(NativeXAFileSystem localXAFileSystem) {
        if(messageEndpointFactory instanceof RemoteMessageEndpointFactory) {
            ((RemoteMessageEndpointFactory) messageEndpointFactory).setLocalXAFileSystem(localXAFileSystem);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EndPointActivation) {
            EndPointActivation epActivation = (EndPointActivation) obj;
            return epActivation.activationSpecImpl.equals(this.activationSpecImpl)
                    && epActivation.messageEndpointFactory.equals(this.messageEndpointFactory);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return messageEndpointFactory.hashCode() + activationSpecImpl.hashCode();
    }
}
