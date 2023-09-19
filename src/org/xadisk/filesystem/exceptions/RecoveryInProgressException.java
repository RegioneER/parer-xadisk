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

package org.xadisk.filesystem.exceptions;

import org.xadisk.bridge.proxies.interfaces.XAFileSystem;
import org.xadisk.connector.outbound.XADiskConnectionFactory;

/**
 * This exception is an unchecked exception and can be thrown during :
 * <ol>
 * <li> {@link XAFileSystem#waitForBootup(long) waitForBootup} when the XAFileSystem is referring to
 * a native (running in the same JVM) or a remote XADisk instance.
 * <li> {@link XAFileSystem#createSessionForLocalTransaction() createSessionForLocalTransaction} when
 * the XAFileSystem is referring to a native (running in the same JVM) or remote XADisk instance.
 * <li> {@link XAFileSystem#createSessionForXATransaction() createSessionForXATransaction} when
 * the XAFileSystem is referring to a native (running in the same JVM) or remote XADisk instance.
 * <li> when using XADisk as a JCA Resource Adapter, during the connection creation steps upto
 * and including {@link XADiskConnectionFactory#getConnection() getConnection}.
 * </ol>
 * 
 * Occurrence of this exception indicates that the XADisk instance has not yet completed its bootup process;
 * the bootup process also involves crash recovery.
 * 
 * <p> Note that XADisk completes (rollback or commit) all of its <i>ongoing</i> (which were running during
 * last shutdown/crash of XADisk) local transactions and XA transactions as a part of its crash
 * recovery. For in-doubt XA transactions, an XADisk instance waits for the Transaction Manager
 * to inform it about the transaction decision; XADisk keeps boot-completion on hold
 * due to these in-doubt XA transactions.
 *
 * @since 1.0
 */

public class RecoveryInProgressException extends XASystemException {

    private static final long serialVersionUID = 1L;
    
    @Override
    public String getMessage() {
        return "This XADisk instance has not yet completed its crash recovery process.";
    }
}
