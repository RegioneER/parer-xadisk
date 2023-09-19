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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import javax.resource.spi.work.Work;

public abstract class TimedWorker implements Work {

    private final int frequency;
    private final ReentrantLock wakeUpAndDieAlarm = new ReentrantLock(false);
    private final Condition hasBeenReleased = wakeUpAndDieAlarm.newCondition();
    private boolean released = false;

    TimedWorker(int frequency) {
        this.frequency = frequency;
    }

    public void release() {
        try {
            wakeUpAndDieAlarm.lockInterruptibly();
            released = true;
            hasBeenReleased.signal();
        } catch(InterruptedException ie) {
            Thread.currentThread().interrupt();
            return;
        }finally {
            wakeUpAndDieAlarm.unlock();
        }
    }

    public void run() {
        while (!released) {
            doWorkOnce();
            try {
                wakeUpAndDieAlarm.lockInterruptibly();
                hasBeenReleased.await(frequency * 1000L, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            } finally {
                wakeUpAndDieAlarm.unlock();
            }
        }
    }

    abstract void doWorkOnce();

    int getFrequency() {
        return frequency;
    }
}
