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
 * This is an abstract super class of all exceptions which are thrown because of a problem
 * associated with the whole XADisk system, and may not be linked to (though, it could be
 * directly/indirectly caused by) the method call (which threw this exception) from the
 * application client.
 *
 * @since 1.0
 */

public abstract class XASystemException extends RuntimeException {

    public XASystemException(String msg) {
        super(msg);
    }

    public XASystemException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public XASystemException(Throwable cause) {
        super(cause);
    }

    public XASystemException() {
    }
}
