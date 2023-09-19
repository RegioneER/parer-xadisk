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

import java.util.ArrayList;

public class ConversationalHostedContext implements HostedContext {
    
    private final ArrayList<Object> remoteInvocationTargets = new ArrayList<Object>();

    public long hostObject(Object target) {
        remoteInvocationTargets.add(target);
        return remoteInvocationTargets.size() - 1;
    }

    public Object getHostedObjectWithId(long objectId) {
        return remoteInvocationTargets.get((int)objectId);//safe to case as conversations start from
        //id 0 instead of a long value as in case of global contex.
    }

    public long deHostObject(Object target) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void deHostObjectWithId(long objectId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
