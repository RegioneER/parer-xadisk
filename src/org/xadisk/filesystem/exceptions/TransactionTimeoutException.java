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
 * This exception can appear as a <i>cause</i> of a {@link TransactionRolledbackException}
 * and indicates that the reason for the rollback by XADisk System was transaction time out.
 *
 * @since 1.0
 */

public class TransactionTimeoutException extends XAApplicationException {

    private static final long serialVersionUID = 1L;
    
    @Override
    public String getMessage() {
        return "The transaction associated earlier was rolled back by XADisk because the transaction"
                + " was open for more than the transaction timeout value.";
    }
}
