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

package org.xadisk.filesystem;

import java.io.File;
import java.io.Serializable;
import javax.ejb.MessageDrivenBean;
import org.xadisk.connector.inbound.FileSystemEventListener;

/**
 * An object of this class represents a directory modification event (a child object
 * added/deleted to/from the directory) published to an <i>interested</i> (as indicated
 * through its activation-spec) {@link MessageDrivenBean} by XADisk. Such a
 * {@link MessageDrivenBean} is handed-over this event object through the 
 * {@link FileSystemEventListener#onFileSystemEvent(FileSystemStateChangeEvent) onFileSystemEvent} method.
 *
 * @since 1.2.1
 */
public class DirectoryModificationEvent extends FileSystemStateChangeEvent implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final File childFilePath;

	DirectoryModificationEvent(File childFilePath, File file, boolean isDirectory, FileSystemEventType eventType,
			TransactionInformation enqueuingTransaction) {
		super(file, isDirectory, eventType, enqueuingTransaction);
		this.childFilePath = childFilePath;
	}

	/**
	 * Returns the child object (file/directory) the addition/deletion of which
	 * triggered this event.
	 * @return a {@link File} object representing the child object (file/directory).
	 */
	public File getChildFilePath() {
		return childFilePath;
	}

	@Override
	public int hashCode() {
		return childFilePath.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DirectoryModificationEvent) {
			DirectoryModificationEvent that = (DirectoryModificationEvent) obj;
			return this.childFilePath.equals(that.childFilePath) && super.equals(obj);
		}
		return false;
	}
}
