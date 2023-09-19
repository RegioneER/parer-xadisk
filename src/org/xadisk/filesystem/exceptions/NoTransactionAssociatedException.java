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

import org.xadisk.bridge.proxies.interfaces.Session;
import org.xadisk.bridge.proxies.interfaces.XADiskBasicIOOperations;
import org.xadisk.bridge.proxies.interfaces.XAFileInputStream;
import org.xadisk.bridge.proxies.interfaces.XAFileOutputStream;
import org.xadisk.bridge.proxies.interfaces.XASession;
import org.xadisk.connector.outbound.XADiskUserLocalTransaction;

/**
 * This exception is thrown by various methods in {@link Session},
 * {@link XASession}, {@link XADiskBasicIOOperations}, {@link XAFileInputStream},
 * {@link XAFileOutputStream} and {@link XADiskUserLocalTransaction}
 * when an associated (<i>current</i>) transaction was expected, but there is no such transaction.
 * <p> Such a situation may result when:
 * <ol>
 * <li> the current transaction has been explicitly rolled-back or committed earlier by the client
 * <li> the current transaction has been already rolled-back by XADisk system due
 * to transaction time out.
 * <li> the current transaction gets rolled-back by XADisk system during the method call
 * itself (e.g. this transaction was involved in a deadlock and was rolled-back by XADisk system
 * to remedy the deadlock. See {@link DeadLockVictimizedException}).
 * </ol>
 *
 * @since 1.0
 */

public class NoTransactionAssociatedException extends XAApplicationException {

    private static final long serialVersionUID = 1L;
    
    public NoTransactionAssociatedException() {
    }

    public NoTransactionAssociatedException(Throwable cause) {
        super(cause);
    }
    
    @Override
    public String getMessage() {
        return "The method that was called can only be called with a transaction associated, but"
                + "there is no such transaction present.";
    }
}
