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
 * This exception is thrown by the i/o operation methods in
 * {@link XADiskBasicIOOperations} when the operation cannot be performed due to insufficient
 * permissions over the file/directory involved in the operation.
 *
 * @since 1.0
 */

public class InsufficientPermissionOnFileException extends XAApplicationException {

    private static final long serialVersionUID = 1L;
    
    private XADiskBasicIOOperations.PermissionType missingPermission;
    private String path;

    public InsufficientPermissionOnFileException(XADiskBasicIOOperations.PermissionType missingPermission, String path) {
        this.missingPermission = missingPermission;
        this.path = path;
    }

    @Override
    public String getMessage() {
        return "Permission of type [" + missingPermission.name() + "] is needed over"
                + " the file/directory with path [" + path + "] for the i/o operation to succeed.";
    }

    /**
     * Returns the path of the file/directory on which required permissions are missing.
     * @return the path of the file/directory.
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the permission which was found missing and is required to complete the
     * operation.
     * @return the required permission.
     */
    public XADiskBasicIOOperations.PermissionType getMissingPermission() {
        return missingPermission;
    }
}
