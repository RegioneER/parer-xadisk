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
 * This exception is thrown by the various I/O methods in {@link XADiskBasicIOOperations}
 * when an attempt to acquire required locks (over file/directory objects) could not succeed
 * because one of the ancestors (parent directory, parent's parent directory, and further)
 * of the corresponding files/directories has been "pinned" by some other transaction. A
 * Pin is a special kind of lock over a directory which is acquired by a transaction
 * willing to {@link XADiskBasicIOOperations#moveFile(java.io.File, java.io.File) move}
 * the directory.
 * <p> As of XADisk 1.0, no wait is done for release of the pin on the ancestor directory, and this
 * exception is thrown immediately. (<i>Such waiting would be implemented in upcoming releases.</i>)
 *
 * @since 1.0
 */
public class AncestorPinnedException extends LockingFailedException {

    private static final long serialVersionUID = 1L;
    
    private final String ancestorPath;

    public AncestorPinnedException(String path, String ancestorPath) {
        super(path);
        this.ancestorPath = ancestorPath;
    }

    @Override
    public String getMessage() {
        return super.getGenericMessage() + " The reason is : "
                + "An ancestor directory [" + ancestorPath + "] has been pinned by some other transaction.";
    }

    /**
     * Returns the ancestor directory's path, whose pinning is blocking the current operation to proceed.
     * @return the path of the ancestor directory.
     */
    public String getAncestorPath() {
        return ancestorPath;
    }
}
