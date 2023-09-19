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

/**
 * This exception can appear as a "cause" of a {@link TransactionRolledbackException}
 * and indicates that an attempt to acquire required locks (over file/directory objects) resulted in a deadlock
 * <i> (a situation when a set of transactions asking for resources, here locks, and also holding
 * some resources, form a cycle of dependencies. This results in all transactions in the cycle getting stuck)
 * </i>, and among the transactions which are member of the deadlock cycle, the
 * current transaction was chosen, by XADisk system, for rollback to remedy the deadlock.
 *
 * @since 1.0
 */

public class DeadLockVictimizedException extends XAApplicationException {

    private static final long serialVersionUID = 1L;
    
    private String path;

    public DeadLockVictimizedException(String path) {
        this.path = path;
    }
    
    @Override
    public String getMessage() {
        return "The current transaction was rolled back prematurely because the transaction"
                + " was one of the transactions stuck in a deadlock, and was chosen for rollback"
                + " as a remedy to the deadlock.";
    }

    /**
     * Returns the file/directory's path, waiting for lock on which resulted in this exception.
     * @return the path of the file/directory.
     */
    public String getPath() {
        return path;
    }
}
