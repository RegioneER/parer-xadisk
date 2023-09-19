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

package org.xadisk.bridge.server.conversation;

import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class GlobalHostedContext implements HostedContext {

    private final ConcurrentHashMap<Long, Object> remoteInvocationTargets =
            new ConcurrentHashMap<Long, Object>();

    //following is a fix to an issue that could arise when one of the parties (xadisk or JavaEE server)
    //can reboot and the other party could still be holding a reference to the older "objectId". Following
    //scheme will ensure that if the system hosting the object reboots, everything will be ok
    //if the time between last reboot and this is X seconds and number of objects hosted
    //before crashing is less than X * 1000. A fairly practical safety because, for example, there is not going to
    //be one ep activation per millisecond throughout the server up time.
    private final AtomicLong objectIdSequence = new AtomicLong(-System.currentTimeMillis());

    //-ve IDs are for global contexts, +ve/0 are for local (conversation specific) contexts.
    public long hostObject(Object target) {
        long objectId = objectIdSequence.decrementAndGet();
        remoteInvocationTargets.put(objectId, target);
        return objectId;
    }

    public void deHostObjectWithId(long objectId) {
        remoteInvocationTargets.remove(objectId);
    }

    public long deHostObject(Object target) {
        Long objectId = null;
        Set<Entry<Long, Object>> entries = remoteInvocationTargets.entrySet();
        for (Entry<Long, Object> entry : entries) {
            if (entry.getValue().equals(target)) {
                objectId = entry.getKey();
                break;
            }
        }
        if (objectId != null) {
            remoteInvocationTargets.remove(objectId);
            return objectId;
        }
        return 1;
    }

    public Object getHostedObjectWithId(long objectId) {
        return remoteInvocationTargets.get(objectId);
    }
}
