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

package org.xadisk.bridge.proxies.interfaces;

import java.io.OutputStream;
import org.xadisk.additional.XAFileOutputStreamWrapper;
import org.xadisk.filesystem.exceptions.ClosedStreamException;
import org.xadisk.filesystem.exceptions.NoTransactionAssociatedException;

/**
 * Represents an output stream to a file. This stream always appends to the target file.
 * Such a stream can be opened through the
 * {@link XADiskBasicIOOperations#createXAFileOutputStream(File, boolean) createXAFileOutputStream} method.
 * <p> This stream can be further wrapped by a utility class {@link XAFileOutputStreamWrapper} to
 * get easy pluggability via the standard {@link OutputStream}.
 *
 * @since 1.0
 */

public interface XAFileOutputStream {
    
    /**
     * Writes all bytes from <i>b</i> into the file.
     * @param b the byte array to write.
     * @throws ClosedStreamException
     * @throws NoTransactionAssociatedException
     */
    public void write(byte[] b) throws ClosedStreamException, NoTransactionAssociatedException;

    /**
     * Writes the input byte into the file.
     * @param b the byte to write.
     * @throws ClosedStreamException
     * @throws NoTransactionAssociatedException
     */
    public void write(int b) throws ClosedStreamException, NoTransactionAssociatedException;

    /**
     * Writes bytes from array <i>b</i>, starting at offset <i>off</i>, upto
     * total <i>length</i> bytes, into the file.
     * @param b the byte array.
     * @param off offset in the byte array.
     * @param length number of bytes to write.
     * @throws ClosedStreamException
     * @throws NoTransactionAssociatedException
     */
    public void write(byte[] b, int off, int length) throws ClosedStreamException, NoTransactionAssociatedException;

    /**
     * Flushes the buffer of this stream. This does not imply that the data gets written
     * to the disk. A guarantee for the buffered data to get persisted is made only after
     * the current transaction gets committed successfully.
     * @throws ClosedStreamException
     * @throws NoTransactionAssociatedException
     */
    public void flush() throws ClosedStreamException, NoTransactionAssociatedException;

    /**
     * Closes this stream. After closing, this stream becomes invalid for any i/o operations.
     * @throws NoTransactionAssociatedException
     */
    public void close() throws NoTransactionAssociatedException;

    /**
     * Tells whether this stream has been closed.
     * @return true if the stream is closed; false otherwise.
     */
    public boolean isClosed();
}
