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

import org.xadisk.bridge.proxies.facilitators.RemoteMethodInvoker;
import org.xadisk.bridge.proxies.facilitators.RemoteObjectProxy;
import org.xadisk.bridge.proxies.interfaces.XAFileOutputStream;
import org.xadisk.filesystem.exceptions.ClosedStreamException;
import org.xadisk.filesystem.exceptions.NoTransactionAssociatedException;

public class RemoteXAFileOutputStream extends RemoteObjectProxy implements XAFileOutputStream {

    private static final long serialVersionUID = 1L;
    
    public RemoteXAFileOutputStream(long objectId, RemoteMethodInvoker invoker) {
        super(objectId, invoker);
    }

    public void write(int b) throws ClosedStreamException, NoTransactionAssociatedException {
        try {
            invokeRemoteMethod("write", b);
        } catch (NoTransactionAssociatedException tre) {
            throw tre;
        } catch (ClosedStreamException cse) {
            throw cse;
        } catch (Throwable t) {
            throw assertExceptionHandling(t);
        }
    }

    public void write(byte[] b) throws ClosedStreamException, NoTransactionAssociatedException {
        try {
            invokeRemoteMethod("write", b);
        } catch (NoTransactionAssociatedException tre) {
            throw tre;
        } catch (ClosedStreamException cse) {
            throw cse;
        } catch (Throwable t) {
            throw assertExceptionHandling(t);
        }
    }

    public void write(byte[] b, int off, int len) throws ClosedStreamException, NoTransactionAssociatedException {
        try {
            byte[] onWire = new byte[len];
            System.arraycopy(b, off, onWire, 0, len);
            invokeRemoteMethod("write", b, off, len);
        } catch (NoTransactionAssociatedException tre) {
            throw tre;
        } catch (ClosedStreamException cse) {
            throw cse;
        } catch (Throwable t) {
            throw assertExceptionHandling(t);
        }
    }

    public void flush() throws ClosedStreamException, NoTransactionAssociatedException {
        try {
            invokeRemoteMethod("flush");
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
}
