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

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SelectorPool implements ResourcePool<PooledSelector> {

    private final ConcurrentLinkedQueue<PooledSelector> freeSelectors;
    private final int idleTime;

    public SelectorPool(int idleTime) {
        this.idleTime = idleTime;
        this.freeSelectors = new ConcurrentLinkedQueue<PooledSelector>();
    }

    public PooledSelector checkOut() {
        PooledSelector temp = lookIntoCurrentPool();
        if (temp != null) {
            return temp;
        }
        temp = allocateNewInCurrentPool();
        if (temp != null) {
            return temp;
        }
        return null;
    }

    private PooledSelector lookIntoCurrentPool() {
        PooledSelector freeSelector = freeSelectors.poll();
        return freeSelector;
    }

    private PooledSelector allocateNewInCurrentPool() {
        PooledSelector newSelector = null;
        try {
            newSelector = new PooledSelector();
            return newSelector;
        } catch (IOException ioe) {
            //allocation failed...return null.
            return null;
        }
    }

    public void checkIn(PooledSelector selector) {
        selector.markFree();
        freeSelectors.offer(selector);
    }

    public void freeIdleMembers() {
        long now = System.currentTimeMillis() / 1000;
        while (true) {
            PooledSelector selector = freeSelectors.peek();
            if (selector == null) {
                break;
            }
            if (now - selector.getLastFreed() > idleTime) {
                freeSelectors.remove(selector);
            } else {
                break;
            }
        }
    }
}
