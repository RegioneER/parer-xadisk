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

/*
*/

package org.xadisk.bridge.proxies.impl;

import java.io.File;
import java.util.ArrayList;
import org.xadisk.bridge.proxies.facilitators.RemoteMethodInvoker;
import org.xadisk.bridge.proxies.facilitators.RemoteObjectProxy;
import org.xadisk.filesystem.ConcurrencyControl;
import org.xadisk.filesystem.Lock;
import org.xadisk.filesystem.TransactionInformation;
import org.xadisk.filesystem.exceptions.AncestorPinnedException;
import org.xadisk.filesystem.exceptions.DeadLockVictimizedException;
import org.xadisk.filesystem.exceptions.DirectoryPinningFailedException;
import org.xadisk.filesystem.exceptions.LockingFailedException;
import org.xadisk.filesystem.exceptions.TransactionRolledbackException;
import org.xadisk.filesystem.exceptions.TransactionTimeoutException;

public class RemoteConcurrencyControl extends RemoteObjectProxy implements ConcurrencyControl {

    private static final long serialVersionUID = 1L;
    
    public RemoteConcurrencyControl(String serverAddress, int serverPort) {
        super(1, new RemoteMethodInvoker(serverAddress, serverPort));
    }

    public RemoteConcurrencyControl getNewInstance() {
        return new RemoteConcurrencyControl(this.invoker.getServerAddress(), this.invoker.getServerPort());
    }

    private RemoteTransactionInformation convertToRemoteTransactionInformation(TransactionInformation transactionInformation) {
        return new RemoteTransactionInformation(transactionInformation, invoker.getServerAddress(), invoker.getServerPort());
    }

    public Lock acquireFileLock(TransactionInformation requestor, File f, long time, boolean exclusive) throws
            LockingFailedException, InterruptedException, TransactionRolledbackException,
            DeadLockVictimizedException, TransactionTimeoutException {
        try {
            return (Lock) invokeRemoteMethod("acquireFileLock", convertToRemoteTransactionInformation(requestor), f, time, exclusive);
        } catch (LockingFailedException lfe) {
            throw lfe;
        } catch (InterruptedException ie) {
            throw ie;
        } catch (TransactionRolledbackException tre) {
            throw tre;
        } catch (DeadLockVictimizedException dlve) {
            throw dlve;
        } catch (TransactionTimeoutException tte) {
            throw tte;
        } catch (Throwable t) {
            throw assertExceptionHandling(t);
        }
    }

    public void pinDirectoryForRename(File dir, TransactionInformation requestor) throws
            DirectoryPinningFailedException, AncestorPinnedException {
        try {
            invokeRemoteMethod("pinDirectoryForRename", dir, convertToRemoteTransactionInformation(requestor));
        } catch (DirectoryPinningFailedException dpfe) {
            throw dpfe;
        } catch (AncestorPinnedException ape) {
            throw ape;
        } catch (Throwable t) {
            throw assertExceptionHandling(t);
        }
    }

    public void releaseLock(TransactionInformation releasor, Lock lock) {
        try {
            invokeRemoteMethod("releaseLock", convertToRemoteTransactionInformation(releasor), lock);
        } catch (Throwable t) {
            throw assertExceptionHandling(t);
        }
    }

    public void releaseRenamePinOnDirectory(File dir) {
        try {
            invokeRemoteMethod("releaseRenamePinOnDirectory", dir);
        } catch (Throwable t) {
            throw assertExceptionHandling(t);
        }
    }

    public void releaseRenamePinOnDirectories(ArrayList<File> dirs) {
        try {
            invokeRemoteMethod("releaseRenamePinOnDirectories", dirs);
        } catch (Throwable t) {
            throw assertExceptionHandling(t);
        }
    }

    public void interruptTransactionIfWaitingForResourceLock(TransactionInformation xid, byte cause) {
        try {
            invokeRemoteMethod("interruptTransactionIfWaitingForResourceLock", convertToRemoteTransactionInformation(xid), cause);
        } catch (Throwable t) {
            throw assertExceptionHandling(t);
        }
    }

    public void shutdown() {
        disconnect();
    }
}
