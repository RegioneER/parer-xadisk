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

package org.xadisk.filesystem.standalone;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.resource.spi.work.ExecutionContext;
import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkEvent;
import javax.resource.spi.work.WorkException;
import javax.resource.spi.work.WorkListener;
import javax.resource.spi.work.WorkManager;

public class StandaloneWorkManager implements WorkManager {

    private ThreadPoolExecutor threadPool;

    public StandaloneWorkManager(int corePoolSize, int maxPoolSize, long keepAliveTime) {
        threadPool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    }

    public long startWork(Work work) throws WorkException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long startWork(Work work, long timeout, ExecutionContext execCtxt, WorkListener workListener)
            throws WorkException {
        try {
            threadPool.execute(new WorkRunnable(work, workListener));
        } catch(Exception e) {
            throw new WorkException(e);
        }
        return 1;
    }

    public void doWork(Work arg0) throws WorkException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void doWork(Work arg0, long arg1, ExecutionContext arg2, WorkListener arg3) throws WorkException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void scheduleWork(Work arg0) throws WorkException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void scheduleWork(Work arg0, long arg1, ExecutionContext arg2, WorkListener arg3) throws WorkException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void shutdown() {
        threadPool.shutdown();
    }

    private static class WorkRunnable implements Runnable {

        private Work work;
        private WorkListener workListener;

        private WorkRunnable(Work work, WorkListener workListener) {
            this.work = work;
            this.workListener = workListener;
        }

        public void run() {
            try {
                work.run();
                if (workListener != null) {
                    workListener.workCompleted(new WorkEvent(work, WorkEvent.WORK_COMPLETED, work, null));
                }
            } catch (Throwable t) {
                if (workListener != null) {
                    workListener.workCompleted(new WorkEvent(work, WorkEvent.WORK_COMPLETED, work,
                            new WorkException(t)));
                }
            }
        }
    }
}
