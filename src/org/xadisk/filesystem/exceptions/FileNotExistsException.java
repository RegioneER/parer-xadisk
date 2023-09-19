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
 * This exception is thrown by those I/O methods in {@link XADiskBasicIOOperations}
 * which are expecting a file/directory to exist, but didn't find it.
 *
 * <p> Note that the existence of a file/directory is derived from the perspective of the current
 * transaction. So, there may be a file/directory on disk, but which was deleted (virtually)
 * by the current transaction. Such a file/directory is <i>non-existing</i> for the current transaction.
 * Similarly, a file/directory which is not on disk, but which was created by the current transaction,
 * is <i>existing</i> from the perspective of the current transaction.
 *
 * @since 1.0
 */

public class FileNotExistsException extends XAApplicationException {

    private static final long serialVersionUID = 1L;
    
    private String path;

    public FileNotExistsException(String path) {
        this.path = path;
    }


    @Override
    public String getMessage() {
        return "The file/directory [" +path+ "] is expected by the i/o operation, but does not exist.";
    }

    /**
     * Returns the path of the file/directory which was expected by the i/o operation, but
     * does not exist.
     * <p> See the class description for definition of <i>existence</i> of
     * a file/directory.
     * @return the path of the file/directory.
    */
    public String getPath() {
        return path;
    }
}
