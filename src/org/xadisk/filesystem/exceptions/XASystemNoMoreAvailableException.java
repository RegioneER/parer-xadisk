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
 * This is an unchecked exception thrown by any of the XADisk APIs to indicate
 * that the XADisk instance has encountered a critical issue and is no more available.
 *
 * @since 1.0
 */

public class XASystemNoMoreAvailableException extends XASystemException {

    private static final long serialVersionUID = 1L;
    
    private final String message;
    
    public XASystemNoMoreAvailableException(Throwable cause) {
        super(cause);
        this.message = "The XADisk instance has encoutered a critial issue and is no more available."
                + " Such a condition is very rare. If you think you have setup everything right for"
                + " XADisk to work, please consider discussing in XADisk forums, or raising a bug"
                + " with details";
    }

    public XASystemNoMoreAvailableException() {
        this.message = "The XADisk instance has been shutdown and is no more available for use.";
    }
    
    @Override
    public String getMessage() {
        return message;
    }
}
