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
 * This exception is thrown by the
 * {@link XADiskBasicIOOperations#moveFile(java.io.File, java.io.File) move}
 * operation on a directory when an attempt to acquire a "pin" (<i> a pin is a special kind of
 * lock over a directory which is acquired by a transaction willing to
 * {@link XADiskBasicIOOperations#moveFile(java.io.File, java.io.File) move}
 * the directory</i>) over the target directory could not succeed because one of the 
 * descendants (children files/directories, children's children file/directories, and further)
 * of the directory has been locked by some other transaction.
 * 
 * <p> As of XADisk 1.0, no wait is done if any such descendant file/directory is locked, and this
 * exception is thrown immediately. (<i>Such waiting would be implemented in upcoming releases.</i>)
 *
 * @since 1.0
 */

public class DirectoryPinningFailedException extends LockingFailedException {

    private static final long serialVersionUID = 1L;
    
    private final String descendantPath;

    public DirectoryPinningFailedException(String path, String descendantPath) {
        super(path);
        this.descendantPath = descendantPath;
    }

    @Override
    public String getMessage() {
        return super.getGenericMessage() + " The reason is : "
                + "A descendant file/directory [" + descendantPath + "] has been locked by some other transaction.";
    }
    
    /**
     * Returns the descendant file/directory's path, lock on which is blocking the current operation to proceed.
     * @return the path of the descendant file/directory.
     */
    public String getDescendantPath() {
        return descendantPath;
    }
}
