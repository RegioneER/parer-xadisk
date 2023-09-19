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

package org.xadisk.filesystem.pools;

import java.nio.ByteBuffer;
import org.xadisk.filesystem.Buffer;
import org.xadisk.filesystem.NativeXAFileSystem;

public class PooledBuffer extends Buffer implements PooledResource {

    private volatile long lastFreed = -1;

    PooledBuffer(int bufferSize, boolean isDirect, NativeXAFileSystem xaFileSystem) {
        super(bufferSize, isDirect, xaFileSystem);
    }

    public void markFree() {
        buffer.clear();
        lastFreed = System.currentTimeMillis() / 1000;
    }

    @Override
    public ByteBuffer getBuffer() {
        return buffer;
    }

    public long getLastFreed() {
        return lastFreed;
    }
}
