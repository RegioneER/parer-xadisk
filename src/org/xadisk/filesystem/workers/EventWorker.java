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

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import javax.resource.spi.work.Work;

public abstract class EventWorker implements Work {

    private final ReentrantLock eventRaiseSynchLock = new ReentrantLock(false);
    private final Condition waitTillEventRaised = eventRaiseSynchLock.newCondition();
    private boolean enabled = true;
    private boolean eventRaised = false;

    public void release() {
        try {
            eventRaiseSynchLock.lockInterruptibly();
            enabled = false;
            waitTillEventRaised.signal();
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return;
        } finally {
            eventRaiseSynchLock.unlock();
        }
    }

    public void run() {
        while (enabled) {
            try {
                eventRaiseSynchLock.lockInterruptibly();
                while (!eventRaised && enabled) {
                    waitTillEventRaised.await();
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            } finally {
                eventRaiseSynchLock.unlock();
            }
            if (eventRaised) {
                eventRaised = false;
                processEvent();
            }
        }
    }

    abstract void processEvent();

    void raiseEvent() {
        if (eventRaised) {
            return;
        }
        eventRaised = true;
        try {
            eventRaiseSynchLock.lock();
            waitTillEventRaised.signal();
        } finally {
            eventRaiseSynchLock.unlock();
        }
    }
}
