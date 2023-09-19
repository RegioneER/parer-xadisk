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

import org.xadisk.bridge.proxies.interfaces.XAFileSystem;
import java.io.PrintWriter;
import java.util.HashSet;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ConnectionEventListener;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.LocalTransaction;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionMetaData;
import javax.security.auth.Subject;
import org.xadisk.filesystem.NativeXASession;

public class XADiskManagedConnection extends NativeXASession implements ManagedConnection {

    private final HashSet<ConnectionEventListener> listeners = new HashSet<ConnectionEventListener>(10);
    private volatile PrintWriter logWriter;
    private final HashSet<XADiskConnection> connectionHandles = new HashSet<XADiskConnection>(2);
    private volatile boolean memorySynchTrigger = false;
    private volatile XADiskLocalTransaction localTransactionImpl;

    public XADiskManagedConnection(XAFileSystem xaFileSystem, String instanceId) {
        super(xaFileSystem, instanceId);
        this.localTransactionImpl = new XADiskLocalTransaction(this);
    }

    public LocalTransaction getLocalTransaction() throws ResourceException {
        return localTransactionImpl;
    }
    
    @Override
    public void cleanup() throws ResourceException {
        super.cleanup();
        this.localTransactionImpl = new XADiskLocalTransaction(this);
        this.connectionHandles.clear();
    }

    public void associateConnection(Object connection) throws ResourceException {
        if (!(connection instanceof XADiskConnection)) {
            throw new ResourceException("Unexpected type for connection handle.");
        }
        ((XADiskConnectionImpl) connection).setManagedConnection(this);
        invalidateCache();
        connectionHandles.add((XADiskConnection) connection);
        flushCacheToMainMemory();
    }

    public void destroy() throws ResourceException {
    }

    public Object getConnection(Subject subject, ConnectionRequestInfo cri) throws ResourceException {
        XADiskConnection temp = new XADiskConnectionImpl(this);
        invalidateCache();
        connectionHandles.add(temp);
        flushCacheToMainMemory();
        return temp;
    }

    public ManagedConnectionMetaData getMetaData() throws ResourceException {
        return new XADiskManagedConnectionMetaData();
    }

    public void addConnectionEventListener(ConnectionEventListener cel) {
        invalidateCache();
        listeners.add(cel);
        flushCacheToMainMemory();
    }

    public void removeConnectionEventListener(ConnectionEventListener cel) {
        invalidateCache();
        listeners.remove(cel);
        flushCacheToMainMemory();
    }

    public PrintWriter getLogWriter() throws ResourceException {
        return this.logWriter;
    }

    public void setLogWriter(PrintWriter logWriter) throws ResourceException {
        this.logWriter = logWriter;
    }

    void connectionClosed(XADiskConnection connection) {
        connectionHandles.remove(connection);
        ConnectionEvent connectionEvent = new ConnectionEvent(this, ConnectionEvent.CONNECTION_CLOSED);
        connectionEvent.setConnectionHandle(connection);
        raiseConnectionEvent(connectionEvent);
    }

    private void raiseConnectionEvent(ConnectionEvent ce) {
        invalidateCache();
        for (ConnectionEventListener cel : listeners) {
            switch (ce.getId()) {
                case ConnectionEvent.CONNECTION_CLOSED:
                    cel.connectionClosed(ce);
                    break;
                case ConnectionEvent.CONNECTION_ERROR_OCCURRED:
                    cel.connectionErrorOccurred(ce);
                    break;
                case ConnectionEvent.LOCAL_TRANSACTION_STARTED:
                    cel.localTransactionStarted(ce);
                    break;
                case ConnectionEvent.LOCAL_TRANSACTION_COMMITTED:
                    cel.localTransactionCommitted(ce);
                    break;
                case ConnectionEvent.LOCAL_TRANSACTION_ROLLEDBACK:
                    cel.localTransactionRolledback(ce);
                    break;
            }
        }
    }

    void raiseUserLocalTransactionEvent(int transactionalEvent) {
        ConnectionEvent ce = new ConnectionEvent(this, transactionalEvent);
        raiseConnectionEvent(ce);
    }

    private void flushCacheToMainMemory() {
        memorySynchTrigger = true;
    }

    private void invalidateCache() {
        boolean temp = memorySynchTrigger;
    }
}
