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

package org.xadisk.filesystem;

import java.io.File;
import java.util.ArrayList;
import org.xadisk.filesystem.TransactionInformation;
import org.xadisk.filesystem.exceptions.AncestorPinnedException;
import org.xadisk.filesystem.exceptions.DeadLockVictimizedException;
import org.xadisk.filesystem.exceptions.DirectoryPinningFailedException;
import org.xadisk.filesystem.exceptions.LockingFailedException;
import org.xadisk.filesystem.exceptions.TransactionRolledbackException;
import org.xadisk.filesystem.exceptions.TransactionTimeoutException;

public interface ConcurrencyControl {

    public Lock acquireFileLock(TransactionInformation requestor, File f, long time, boolean exclusive)
            throws LockingFailedException, InterruptedException, TransactionRolledbackException,
            DeadLockVictimizedException, TransactionTimeoutException;

    public void releaseLock(TransactionInformation releasor, Lock lock);

    public void releaseRenamePinOnDirectories(ArrayList<File> dirs);

    public void releaseRenamePinOnDirectory(File dir);

    public void pinDirectoryForRename(File dir, TransactionInformation requestor)
            throws DirectoryPinningFailedException, AncestorPinnedException;

    public void interruptTransactionIfWaitingForResourceLock(TransactionInformation xid, byte cause);

    public void shutdown();
}
