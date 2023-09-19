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
import org.xadisk.filesystem.TransactionInformation;

/**
 * This exception is an unchecked exception thrown when a transaction is not able to
 * continue its work and is left in an inconsistent incomplete state. That is, the
 * transaction did not commit or rollback and will require an administrative
 * intervention for resolving inconsistency and then marking the transaction as
 * complete. A transaction can fail either during routine XADisk operations or
 * during the recovery phase of XADisk. For the routine cases, the
 * transaction will keep holding the locks (in-memory) over files/directories until
 * it is marked complete.
 *
 * <p> If a transaction fails during recovery (when xadisk is committing or rolling-back
 * the transactions running prior to the reboot), the recovery process will wait until
 * all these failed transactions are marked complete.
 *
 * <p> A failed transaction can be marked complete using
 * {@link XAFileSystem#declareTransactionAsComplete(byte[])}. The identifier for such
 * transaction can be obtained either from {@link #getTransactionIdentifier()}, or
 * {@link XAFileSystem#getIdentifiersForFailedTransactions()}.
 * 
 * <p> This exception is not expected to occur in general, and indicates a severe problem.
 *
 * @since 1.2.2
 */

public class TransactionFailedException extends XASystemException {

    private static final long serialVersionUID = 1L;

    private byte[] transactionIdentifier;

    public TransactionFailedException(Throwable cause, TransactionInformation xid) {
        super(cause);
        this.transactionIdentifier = xid.getBytes();
    }

    /**
     * Returns a byte-array identifier with which the transaction can be identified
     * to the XADisk instance for tasks like marking the transaction as complete.
     * @return the transaction identifier in byte-array form.
     */
    public byte[] getTransactionIdentifier() {
        return transactionIdentifier;
    }

    @Override
    public String getMessage() {
        return "The transaction has failed and has not completed commit or rollback. The "
                + "file-system data operated on by the transaction may be in inconsistent "
                + "state. This exception is not expected to occur in general, and "
                + "indicates a severe problem.";
    }
}
