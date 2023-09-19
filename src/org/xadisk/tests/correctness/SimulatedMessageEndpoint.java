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
import javax.resource.ResourceException;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import org.xadisk.bridge.proxies.interfaces.XAFileSystemProxy;
import org.xadisk.connector.inbound.FileSystemEventListener;
import org.xadisk.filesystem.FileSystemStateChangeEvent;
import org.xadisk.filesystem.NativeXAFileSystem;
import org.xadisk.filesystem.TransactionInformation;

public class SimulatedMessageEndpoint implements MessageEndpoint, FileSystemEventListener {

    private XAResource epXAR;
    private SimulatedMessageEndpointFactory owningFactory;
    private TransactionInformation generatedXid;
    public SimulatedMessageEndpointFactory.GoTill goTill;

    public SimulatedMessageEndpoint(XAResource epXAR, SimulatedMessageEndpointFactory owningFactory) {
        this.epXAR = epXAR;
        this.owningFactory = owningFactory;
    }

    public void beforeDelivery(Method meth) throws NoSuchMethodException, ResourceException {
        this.generatedXid = TransactionInformation.getXidInstanceForLocalTransaction(
                ((NativeXAFileSystem)XAFileSystemProxy.getNativeXAFileSystemReference("local")).getNextLocalTransactionId());
        try {
            epXAR.start(generatedXid, XAResource.TMNOFLAGS);
        } catch (XAException xae) {
            xae.printStackTrace();
            throw new ResourceException(xae);
        }
    }

    public void onFileSystemEvent(FileSystemStateChangeEvent event) {
        System.out.println("Received " + event.getEventType() + " event for file " + event.getFile());
        if (this.goTill == SimulatedMessageEndpointFactory.GoTill.consume) {
            throw new ArrayIndexOutOfBoundsException("intentional");
        }
        owningFactory.incrementEventsReceivedCount();
        try {
            Thread.sleep(100);
        } catch (InterruptedException ie) {
        }
    }

    public void afterDelivery() throws ResourceException {
        try {
            epXAR.end(generatedXid, XAResource.TMSUCCESS);
            epXAR.prepare(generatedXid);
            if (this.goTill == SimulatedMessageEndpointFactory.GoTill.prepare) {
                return;
            }
            epXAR.commit(generatedXid, false);
        } catch (XAException xae) {
            throw new ResourceException(xae);
        }
    }

    public void release() {
    }
}
