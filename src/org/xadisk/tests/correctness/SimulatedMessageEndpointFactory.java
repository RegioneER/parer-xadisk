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

package org.xadisk.tests.correctness;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;
import javax.resource.spi.UnavailableException;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;

public class SimulatedMessageEndpointFactory implements MessageEndpointFactory {

    private AtomicInteger eventsReceived = new AtomicInteger(0);
    public GoTill goTill = GoTill.commit;
    public enum GoTill {

        consume, prepare, commit
    };

    public MessageEndpoint createEndpoint(XAResource xar) throws UnavailableException {
        SimulatedMessageEndpoint smep = new SimulatedMessageEndpoint(xar, this);
        smep.goTill = this.goTill;
        return smep;
    }

    public boolean isDeliveryTransacted(Method meth) throws NoSuchMethodException {
        return true;
    }

    public void incrementEventsReceivedCount() {
        eventsReceived.getAndIncrement();
    }

    public int getEventsReceivedCount() {
        return eventsReceived.get();
    }
}
