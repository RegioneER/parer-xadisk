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

package org.xadisk.filesystem.workers;

import org.xadisk.filesystem.NativeSession;
import org.xadisk.filesystem.NativeXAFileSystem;
import org.xadisk.filesystem.ResourceDependencyGraph.Node;
import org.xadisk.filesystem.TransactionInformation;
import org.xadisk.filesystem.exceptions.TransactionTimeoutException;

public class TransactionTimeoutDetector extends TimedWorker {

    private final NativeXAFileSystem xaFileSystem;

    public TransactionTimeoutDetector(int frequency, NativeXAFileSystem xaFileSystem) {
        super(frequency);
        this.xaFileSystem = xaFileSystem;
    }

    @Override
    void doWorkOnce() {
        try {
            NativeSession sessions[] = xaFileSystem.getAllSessions();
            for (int i = 0; i < sessions.length; i++) {
                NativeSession session = sessions[i];
                long timeoutValue = session.getTransactionTimeout() * 1000;
                long birthTime = session.getTimeOfEntryToTransaction();
                long timeNow = System.currentTimeMillis();
                if (timeoutValue > 0 && timeNow - birthTime > timeoutValue) {
                    xaFileSystem.getConcurrencyControl().interruptTransactionIfWaitingForResourceLock(session.getXid(),
                            Node.INTERRUPTED_DUE_TO_TIMEOUT);
                    session.rollbackAsynchronously(new TransactionTimeoutException());
                }
            }
        } catch (Throwable t) {
            xaFileSystem.notifySystemFailure(t);
        }
    }

    @Override
    public void release() {
        super.release();
    }

    @Override
    public void run() {
        super.run();
    }
}
