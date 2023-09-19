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

package org.xadisk.examples.jca;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import org.xadisk.connector.inbound.FileSystemEventListener;
import org.xadisk.filesystem.FileSystemStateChangeEvent;

/**
 * This example is applicable only for Message Driven Beans (JavaEE).
 */
/**
 * This is a very basic example which
 *      - declares the XADisk's ActivationSpec properties using annotations. These properties indicate
 *              the XADisk instance to connect to and also the kind of events the MDB is interested in.
 *      - specifies that this MDB should be invoked inside a transaction always.
 *      - prints a message on receiving an event from the XADisk instance.
 */
/**
 * How to run this example:
 *
 * 1) Change the various values inside the ActivationSpec properties to suit your environment.
 * 2) Put this MDB class inside an MDB jar file.
 * 3) Deploy XADisk as a Resource Adapter in the JavaEE (5.0 or above) server (independent of where is the
 *          XADisk instance this MDB is bound to).
 * 4) Deploy the MDB jar.
 * 5) Using any type of a Java application (JavaEE or non-JavaEE), perform some file operation on the
 *    XADisk instance (to which this MDB is bound to) so that an event get generated for this MDB. Remember to set the
 *    publish flag to true on the Session/XADiskConnection via setPublishFileStateChangeEventsOnCommit method.
 */
/**
 * Please refer to the XADisk JavaDoc and User Guide for knowing more about using XADisk.
 */
@MessageDriven(name = "XADiskListenerMDB1",
activationConfig = {
    @ActivationConfigProperty(propertyName = "fileNamesAndEventInterests", propertyValue = "C:\\::111|D:\\testDir\\::111"),
    @ActivationConfigProperty(propertyName = "areFilesRemote", propertyValue = "true"),
    @ActivationConfigProperty(propertyName = "remoteServerAddress", propertyValue = "localhost"),
    @ActivationConfigProperty(propertyName = "remoteServerPort", propertyValue = "2010")
})
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class EventListenerMDB implements FileSystemEventListener {

    public void onFileSystemEvent(FileSystemStateChangeEvent event) {
        System.out.println("RECEIVED AN EVENT OF TYPE : " + event.getEventType() + " FOR FILE : " + event.getFile());
    }
}
