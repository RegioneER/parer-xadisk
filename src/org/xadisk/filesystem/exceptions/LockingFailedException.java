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

import org.xadisk.bridge.proxies.interfaces.XADiskBasicIOOperations;

/**
 * This exception is a super class of all other exceptions which are thrown by
 * various i/o methods in {@link XADiskBasicIOOperations} when
 * a resource (file or directory) locking fails. Note that this class is an abstract class.
 *
 * @since 1.0
 */

public abstract class LockingFailedException extends XAApplicationException {
    private String path;

    public LockingFailedException(String path) {
        this.path = path;
    }

    /**
     * Returns the path of the file/directory on which the lock required for the operation could not be acquired.
     * @return the path of the file/directory.
     */
    public String getPath() {
        return path;
    }
    
    public String getGenericMessage() {
        return "A lock required over the file/directory [" + path + "], which was required"
                + " for the operation, could not be acquired.";
    }
}
