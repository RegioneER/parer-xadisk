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
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;

public class PooledSelector implements PooledResource {

    private volatile long lastFreed = -1;
    private Selector selector;

    public PooledSelector() throws IOException {
        this.selector = Selector.open();
    }

    public long getLastFreed() {
        return lastFreed;
    }

    public Selector getSelector() {
        return selector;
    }

    public void markFree() {
        Set<SelectionKey> keys = selector.keys();
        for (SelectionKey key : keys) {
            key.cancel();
        }
        try {
            selector.selectNow();
        } catch (IOException ioe) {
            //ignore.
        }
        lastFreed = System.currentTimeMillis() / 1000;
    }
}
