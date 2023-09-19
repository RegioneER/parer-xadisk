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

package org.xadisk.filesystem.workers;

import org.xadisk.filesystem.NativeXAFileSystem;
import org.xadisk.filesystem.pools.ResourcePool;

public class ObjectPoolReliever extends TimedWorker {

    private final ResourcePool objectPool;
    private final NativeXAFileSystem xaFileSystem;

    public ObjectPoolReliever(ResourcePool objectPool, int frequency, NativeXAFileSystem xaFileSystem) {
        super(frequency);
        this.objectPool = objectPool;
        this.xaFileSystem = xaFileSystem;
    }

    @Override
    void doWorkOnce() {
        try {
            objectPool.freeIdleMembers();
        } catch (Throwable t) {
            xaFileSystem.notifySystemFailure(t);
        }
    }

    @Override
    public void release() {
        super.release();
    }

    @Override
    public void run() {
        super.run();
    }
}
