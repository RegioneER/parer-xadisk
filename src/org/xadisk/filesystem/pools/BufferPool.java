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

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import org.xadisk.filesystem.NativeXAFileSystem;

public class BufferPool implements ResourcePool<PooledBuffer> {

    private final int directBufferMaxPoolSize;
    private final int nonDirectBufferMaxPoolSize;
    private final int bufferSize;
    private final AtomicInteger currentDirectPoolSize;
    private final AtomicInteger currentNonDirectPoolSize;
    private final ConcurrentLinkedQueue<PooledBuffer> directFreeBuffers;
    private final ConcurrentLinkedQueue<PooledBuffer> nonDirectFreeBuffers;
    private final int directBufferIdleTime;
    private final int nonDirectBufferIdleTime;
    private final NativeXAFileSystem xaFileSystem;

    public BufferPool(int directBufferPoolSize, int nonDirectBufferPoolSize, int bufferSize,
            int directBufferIdleTime, int nonDirectBufferIdleTime, NativeXAFileSystem xaFileSystem) {
        this.xaFileSystem = xaFileSystem;
        this.directBufferMaxPoolSize = directBufferPoolSize;
        this.nonDirectBufferMaxPoolSize = nonDirectBufferPoolSize;
        this.bufferSize = bufferSize;
        this.directBufferIdleTime = directBufferIdleTime;
        this.nonDirectBufferIdleTime = nonDirectBufferIdleTime;
        this.directFreeBuffers = new ConcurrentLinkedQueue<PooledBuffer>();
        this.nonDirectFreeBuffers = new ConcurrentLinkedQueue<PooledBuffer>();
        this.currentDirectPoolSize = new AtomicInteger(0);
        this.currentNonDirectPoolSize = new AtomicInteger(0);
    }

    public PooledBuffer checkOut() {
        PooledBuffer temp = lookIntoCurrentPool(true);
        if (temp != null) {
            return temp;
        }
        temp = lookIntoCurrentPool(false);
        if (temp != null) {
            return temp;
        }
        temp = allocateNewInCurrentPool(true);
        if (temp != null) {
            return temp;
        }
        temp = allocateNewInCurrentPool(false);
        if (temp != null) {
            return null;
        }
        return null;
    }

    private PooledBuffer lookIntoCurrentPool(boolean inDirectBufferPool) {
        ConcurrentLinkedQueue<PooledBuffer> buffers;
        if (inDirectBufferPool) {
            buffers = directFreeBuffers;
        } else {
            buffers = nonDirectFreeBuffers;
        }
        PooledBuffer freeBuffer = buffers.poll();
        if (freeBuffer != null) {
            freeBuffer.invalidateByteBufferFromCache();
        }
        return freeBuffer;
    }

    private PooledBuffer allocateNewInCurrentPool(boolean inDirectBufferPool) {
        AtomicInteger currentPoolSize;
        int maxPoolSize;
        PooledBuffer newBuffer = null;
        if (inDirectBufferPool) {
            currentPoolSize = currentDirectPoolSize;
            maxPoolSize = directBufferMaxPoolSize;
        } else {
            currentPoolSize = currentNonDirectPoolSize;
            maxPoolSize = nonDirectBufferMaxPoolSize;
        }
        while (true) {
            int temp = currentPoolSize.get();
            if (temp >= maxPoolSize) {
                return null;
            }
            if (currentPoolSize.compareAndSet(temp, temp + 1)) {
                break;
            }
        }
        newBuffer = new PooledBuffer(bufferSize, inDirectBufferPool, xaFileSystem);
        return newBuffer;
    }

    public void checkIn(PooledBuffer buffer) {
        buffer.markFree();
        if (buffer.isDirect) {
            directFreeBuffers.offer(buffer);
        } else {
            nonDirectFreeBuffers.offer(buffer);
        }
        buffer.flushByteBufferChanges();
    }

    public void freeIdleMembers() {
        freeIdleMembers(true);
        freeIdleMembers(false);
    }

    private void freeIdleMembers(boolean inDirectBufferPool) {
        AtomicInteger currentPoolSize;
        ConcurrentLinkedQueue<PooledBuffer> buffers;
        int bufferIdleTime;

        if (inDirectBufferPool) {
            currentPoolSize = currentDirectPoolSize;
            bufferIdleTime = directBufferIdleTime;
            buffers = directFreeBuffers;
        } else {
            currentPoolSize = currentNonDirectPoolSize;
            bufferIdleTime = nonDirectBufferIdleTime;
            buffers = nonDirectFreeBuffers;
        }
        long now = System.currentTimeMillis() / 1000;
        while (true) {
            PooledBuffer buffer = buffers.peek();
            if (buffer == null) {
                break;
            }
            if (now - buffer.getLastFreed() > bufferIdleTime) {
                if (buffers.remove(buffer)) {
                    currentPoolSize.decrementAndGet();
                }
            } else {
                break;
            }
        }
    }
}
