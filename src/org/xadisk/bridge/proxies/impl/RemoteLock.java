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

/*
*/

package org.xadisk.bridge.proxies.impl;

import java.io.File;
import org.xadisk.bridge.proxies.facilitators.RemoteObjectProxy;
import org.xadisk.filesystem.Lock;

public class RemoteLock extends RemoteObjectProxy implements Lock {

    private static final long serialVersionUID = 1L;
    private final File resource;
    private boolean exclusive;

    public RemoteLock(long objectId, File resource, boolean exclusive) {
        super(objectId, null);
        this.resource = resource;
        this.exclusive = exclusive;
    }

    public File getResource() {
        return resource;
    }

    public boolean isExclusive() {
        return exclusive;
    }
}
