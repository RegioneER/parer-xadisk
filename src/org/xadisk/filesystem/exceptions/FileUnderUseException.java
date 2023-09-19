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

package org.xadisk.filesystem.exceptions;

import org.xadisk.bridge.proxies.interfaces.XADiskBasicIOOperations;
import org.xadisk.bridge.proxies.interfaces.XAFileInputStream;
import org.xadisk.bridge.proxies.interfaces.XAFileOutputStream;

/**
 * This exception is thrown by {@link XADiskBasicIOOperations#deleteFile(java.io.File)
 * deleteFile} and {@link XADiskBasicIOOperations#moveFile(java.io.File, java.io.File)
 * moveFile} operations when the to-be-deleted file is being read/written inside the
 * current transaction, i.e. there is a {@link XAFileInputStream} or
 * {@link XAFileOutputStream} opened for the file by the current transaction.
 * <p> This exception is also thrown by 
 * {@link XADiskBasicIOOperations#createXAFileOutputStream(java.io.File, boolean) createXAFileOutputStream}
 * when the <i>heavyWrite</i> parameter is true, but another output stream ({@link XAFileOutputStream})
 * is open for the same file in current transaction in non-<i>heavyWrite</i> mode.
 *
 * @since 1.0
 */

public class FileUnderUseException extends XAApplicationException {

    private static final long serialVersionUID = 1L;
    
    private String path;
    private boolean dueToNonHeavyWriteModeOutputStream;

    public FileUnderUseException(String path, boolean dueToNonHeavyWriteModeOutputStream) {
        this.path = path;
        this.dueToNonHeavyWriteModeOutputStream = dueToNonHeavyWriteModeOutputStream;
    }

    @Override
    public String getMessage() {
        if(dueToNonHeavyWriteModeOutputStream) {
            return "The file [" +path+ "] being accessed already has an output stream open to it in"
                    + "non-heavyWrite mode by the current transaction.";
        } else {
            return "The file [" +path+ "] being deleted has an input/output stream open to it "
                    + "by the current transaction.";
        }
    }

    /**
     * Returns the path of the file upon which the i/o operation could not succeed.
     * @return the path of the file.
    */
    public String getPath() {
        return path;
    }

    /**
     * This method returns true only and only when <i>this</i> exception is thrown by
     * {@link XADiskBasicIOOperations#createXAFileOutputStream(java.io.File, boolean) createXAFileOutputStream}
     * with the <i>heavyWrite</i> parameter as true, but another output stream ({@link XAFileOutputStream})
     * is open for the same file in current transaction in non-<i>heavyWrite</i> mode.
     * @return true, if thrown by {@link XADiskBasicIOOperations#createXAFileOutputStream(java.io.File, boolean)
     * createXAFileOutputStream}; false otherwise.
     */
    public boolean isDueToNonHeavyWriteModeOutputStream() {
        return dueToNonHeavyWriteModeOutputStream;
    }
}
