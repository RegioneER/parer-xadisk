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

import java.io.InputStream;
import org.xadisk.additional.XAFileInputStreamWrapper;
import org.xadisk.filesystem.exceptions.ClosedStreamException;
import org.xadisk.filesystem.exceptions.NoTransactionAssociatedException;

/**
 * Represents an input stream to a file. Such a stream can be opened through the
 * {@link XADiskBasicIOOperations#createXAFileInputStream(File, boolean) createXAFileInputStream}
 * method.
 * <p> This stream can be further wrapped by a utility class {@link XAFileInputStreamWrapper} to
 * get easy pluggability via the standard {@link InputStream}.
 *
 * @since 1.0
 */

public interface XAFileInputStream {
    
    /**
     * Returns the number of bytes (readily) available in the internal buffer of this stream.
     * @return number of bytes available
     * @throws NoTransactionAssociatedException
     * @throws ClosedStreamException
     */
    public int available() throws NoTransactionAssociatedException, ClosedStreamException;

    /**
     * Closes this stream. After closing, this stream becomes invalid for any i/o operations.
     * @throws NoTransactionAssociatedException
     */
    public void close() throws NoTransactionAssociatedException;

    /**
     * Reads the next byte if available. If EOF has been reached, returns -1.
     * @return value of the next byte as an integer.
     * @throws ClosedStreamException
     * @throws NoTransactionAssociatedException
     */
    public int read() throws ClosedStreamException, NoTransactionAssociatedException;

    /**
     * Read at least 1 byte, upto <i>b.length</i> bytes from the stream and put them into the
     * array <i>b</i>.
     * @param b the byte array into which the bytes will be read.
     * @return number of bytes actually read. -1 if EOF has been reached before reading even 1 byte.
     * @throws ClosedStreamException
     * @throws NoTransactionAssociatedException
     */
    public int read(byte[] b) throws ClosedStreamException, NoTransactionAssociatedException;

    /**
     * Read at least 1 byte, upto <i>length</i> bytes from the stream and put them into the array
     * <i>b</i> starting at offset <i>off</i>.
     * @param b the byte array into which the bytes will be read.
     * @param off offset in the byte array.
     * @param length maximum number of bytes to read
     * @return number of bytes actually read. -1 if EOF has been reached before reading even 1 byte.
     * @throws ClosedStreamException
     * @throws NoTransactionAssociatedException
     */
    public int read(byte[] b, int off, int length) throws ClosedStreamException, NoTransactionAssociatedException;

    /**
     * Skips upto <i>n</i> bytes in the stream.
     * @param n a non-negative integer representing the number of bytes to skip.
     * @return actual number of bytes that were skipped.
     * @throws NoTransactionAssociatedException
     * @throws ClosedStreamException
     */
    public long skip(long n) throws NoTransactionAssociatedException, ClosedStreamException;

    /**
     * Positions the <i>pointer</i> inside the stream from where the next bytes will be read.
     * @param n the new position; should be within 0 and fileLength, both inclusive.
     * @throws NoTransactionAssociatedException
     * @throws ClosedStreamException
     */
    public void position(long n) throws NoTransactionAssociatedException, ClosedStreamException;

    /**
     * Tells whether this stream has been closed.
     * @return true if the stream is closed; false otherwise.
     */
    public boolean isClosed();

    /**
     * Returns the current position (<i>pointer</i>) in the stream. Immediately after opening this stream,
     * it is 0.
     * @return the current position in the stream.
     */
    public long position();
}
