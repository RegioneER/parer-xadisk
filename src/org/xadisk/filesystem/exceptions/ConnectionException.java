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
 * This exception is thrown by any of the XADisk API methods when the target XADisk instance is a remote one
 * (running on a remote JVM) and a communication error (most likely, network related) is encountered
 * during the method call.
 * <p> This is a subclass of {@link XASystemException} and hence is a {@link RuntimeException}.
 *
 * @since 1.0
 */

public class ConnectionException extends XASystemException {

    private static final long serialVersionUID = 1L;
    
    public ConnectionException(Throwable cause) {
        super(cause);
    }
}
