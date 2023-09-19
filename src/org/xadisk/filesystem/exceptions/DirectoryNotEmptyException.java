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
 * This exception is thrown when a directory {@link XADiskBasicIOOperations#deleteFile(java.io.File) delete}
 * operation was invoked but the directory is not empty.
 *
 * @since 1.0
 */
public class DirectoryNotEmptyException extends XAApplicationException {

    private static final long serialVersionUID = 1L;
    
    private String path;

    public DirectoryNotEmptyException(String path) {
        this.path = path;
    }

    @Override
    public String getMessage() {
        return "The directory [" +path+ "] could not be deleted as it is not empty.";
    }

    /**
     * Returns the path of the directory which could not be deleted.
     * @return the path of the directory.
    */
    public String getPath() {
        return path;
    }
}
