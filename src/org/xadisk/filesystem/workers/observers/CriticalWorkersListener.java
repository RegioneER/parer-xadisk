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

package org.xadisk.filesystem.workers.observers;

import javax.resource.spi.work.WorkEvent;
import javax.resource.spi.work.WorkException;
import javax.resource.spi.work.WorkListener;
import org.xadisk.filesystem.NativeXAFileSystem;

public class CriticalWorkersListener implements WorkListener {

    private final NativeXAFileSystem xaFileSystem;

    public CriticalWorkersListener(NativeXAFileSystem xaFileSystem) {
        this.xaFileSystem = xaFileSystem;
    }

    public void workAccepted(WorkEvent we) {
    }

    public void workCompleted(WorkEvent we) {
        if (we.getType() == WorkEvent.WORK_COMPLETED) {
            WorkException workException = we.getException();
            if (workException != null) {
                xaFileSystem.notifySystemFailure(workException);
            }
        }
    }

    public void workRejected(WorkEvent we) {
        //if we decide to use "scheduleWork" for any case with zero OR non-zero startTimeout,
        //(in both cases) the work-rejected-due-to-starttimeout exception can come on this
        //listener (which does not come to a listener when using startWork, because startWork returns only
        //after starting the work, and so such starttimeout exception would directly come to the caller.
    }

    public void workStarted(WorkEvent we) {
    }
}
