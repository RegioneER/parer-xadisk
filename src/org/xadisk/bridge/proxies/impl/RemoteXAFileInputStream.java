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

import org.xadisk.bridge.proxies.facilitators.ByteArrayRemoteReference;
import org.xadisk.bridge.proxies.facilitators.RemoteMethodInvoker;
import org.xadisk.bridge.proxies.facilitators.RemoteObjectProxy;
import org.xadisk.bridge.proxies.interfaces.XAFileInputStream;
import org.xadisk.filesystem.exceptions.ClosedStreamException;
import org.xadisk.filesystem.exceptions.NoTransactionAssociatedException;

public class RemoteXAFileInputStream extends RemoteObjectProxy implements XAFileInputStream {

    private static final long serialVersionUID = 1L;
    
    public RemoteXAFileInputStream(long objectId, RemoteMethodInvoker invoker) {
        super(objectId, invoker);
    }

    public int available() throws NoTransactionAssociatedException, ClosedStreamException {
        try {
            return (Integer) invokeRemoteMethod("available");
        } catch (NoTransactionAssociatedException tre) {
            throw tre;
        } catch (ClosedStreamException cse) {
            throw cse;
        } catch (Throwable t) {
            throw assertExceptionHandling(t);
        }
    }

    public void close() throws NoTransactionAssociatedException {
        try {
            invokeRemoteMethod("close");
        } catch (NoTransactionAssociatedException tre) {
            throw tre;
        } catch (Throwable t) {
            throw assertExceptionHandling(t);
        }
    }

    public boolean isClosed() {
        try {
            return (Boolean) invokeRemoteMethod("isClosed");
        } catch (Throwable t) {
            throw assertExceptionHandling(t);
        }
    }

    public void position(long n) throws NoTransactionAssociatedException, ClosedStreamException {
        try {
            invokeRemoteMethod("position", n);
        } catch (NoTransactionAssociatedException tre) {
            throw tre;
        } catch (ClosedStreamException cse) {
            throw cse;
        } catch (Throwable t) {
            throw assertExceptionHandling(t);
        }
    }

    public long position() {
        try {
            return (Long) invokeRemoteMethod("position");
        } catch (Throwable t) {
            throw assertExceptionHandling(t);
        }
    }

    public int read() throws ClosedStreamException, NoTransactionAssociatedException {
        try {
            return (Integer) invokeRemoteMethod("read");
        } catch (NoTransactionAssociatedException tre) {
            throw tre;
        } catch (ClosedStreamException cse) {
            throw cse;
        } catch (Throwable t) {
            throw assertExceptionHandling(t);
        }
    }
    
    public int read(byte[] b) throws ClosedStreamException, NoTransactionAssociatedException {
        try {
            ByteArrayRemoteReference ref = new ByteArrayRemoteReference(b, 0, b.length);
            return (Integer) invokeRemoteMethod("read", ref);
        } catch (NoTransactionAssociatedException tre) {
            throw tre;
        } catch (ClosedStreamException cse) {
            throw cse;
        } catch (Throwable t) {
            throw assertExceptionHandling(t);
        }
    }

    public int read(byte[] b, int off, int len) throws ClosedStreamException, NoTransactionAssociatedException {
        try {
            ByteArrayRemoteReference ref = new ByteArrayRemoteReference(b, off, len);
            return (Integer) invokeRemoteMethod("read", ref);
        } catch (NoTransactionAssociatedException tre) {
            throw tre;
        } catch (ClosedStreamException cse) {
            throw cse;
        } catch (Throwable t) {
            throw assertExceptionHandling(t);
        }
    }

    public long skip(long n) throws NoTransactionAssociatedException, ClosedStreamException {
        try {
            return (Long) invokeRemoteMethod("skip", n);
        } catch (NoTransactionAssociatedException tre) {
            throw tre;
        } catch (ClosedStreamException cse) {
            throw cse;
        } catch (Throwable t) {
            throw assertExceptionHandling(t);
        }
    }
}
