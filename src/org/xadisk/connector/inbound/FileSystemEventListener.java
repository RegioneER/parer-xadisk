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

package org.xadisk.connector.inbound;

import javax.ejb.MessageDrivenBean;
import org.xadisk.filesystem.FileSystemStateChangeEvent;

/**
 * An interface which must be implemented by the {@link MessageDrivenBean} willing to
 * receive file-system events ({@link FileSystemStateChangeEvent}) from XADisk.
 *
 * @since 1.0
 */

public interface FileSystemEventListener {

    /**
     * This method is called by the JavaEE container when an event, targeted for this MDB,
     * is published by the XADisk instance.
     * @param event the event object providing details of the event which has taken place.
     */
    
    public void onFileSystemEvent(FileSystemStateChangeEvent event);
}
