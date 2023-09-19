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

package org.xadisk.filesystem.virtual;

import org.xadisk.filesystem.pools.PooledBuffer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.locks.ReentrantLock;
import org.xadisk.bridge.proxies.interfaces.XAFileInputStream;
import org.xadisk.filesystem.Buffer;
import org.xadisk.filesystem.NativeSession;
import org.xadisk.filesystem.NativeXAFileSystem;
import org.xadisk.filesystem.exceptions.ClosedStreamException;
import org.xadisk.filesystem.exceptions.FileNotExistsException;
import org.xadisk.filesystem.exceptions.NoTransactionAssociatedException;

public class NativeXAFileInputStream implements XAFileInputStream {

    private FileChannel physicalFileChannel;
    private FileInputStream physicalFileInputStream;
    private ByteBuffer byteBuffer;
    private final NativeXAFileSystem xaFileSystem;
    private boolean filledAtleastOnce = false;
    private final VirtualViewFile vvf;
    private boolean closed = false;
    private long position;
    private final ByteBuffer cachedWritableByteBuffer;
    private int headerLengthInByteBuffer;
    private final NativeSession owningSession;
    private final ReentrantLock asynchronousRollbackLock;
    private final PooledBuffer pooledBuffer;

    public NativeXAFileInputStream(VirtualViewFile vvf, NativeSession owningSession, NativeXAFileSystem xaFileSystem)
            throws FileNotExistsException {
        this.xaFileSystem = xaFileSystem;
        pooledBuffer = this.xaFileSystem.getBufferPool().checkOut();
        if (pooledBuffer != null) {
            this.byteBuffer = pooledBuffer.getBuffer();
        } else {
            this.byteBuffer = (new Buffer(xaFileSystem.getConfiguredBufferSize(), false, xaFileSystem)).getBuffer();
        }
        this.cachedWritableByteBuffer = this.byteBuffer;
        assert cachedWritableByteBuffer != null;//to debug a strange issue where refillBuffer was reporting
        //null for "cachedWritableBB".
        assert !cachedWritableByteBuffer.isReadOnly();//to debug another issue where this was reported to be r/o.
        this.vvf = vvf;
        vvf.addBeingRead();
        this.position = 0;
        this.owningSession = owningSession;
        this.asynchronousRollbackLock = owningSession.getAsynchronousRollbackLock();
        if (vvf.isMappedToAPhysicalFile()) {
            try {
                this.physicalFileInputStream = new FileInputStream(vvf.getMappedToPhysicalFile());
                this.physicalFileChannel = physicalFileInputStream.getChannel();
            } catch (FileNotFoundException fnfe) {
                throw new FileNotExistsException(vvf.getFileName().getAbsolutePath());
            }
        }
    }

    public int available() throws NoTransactionAssociatedException, ClosedStreamException {
        try {
            asynchronousRollbackLock.lock();
            checkIfCanContinue();
            if (!filledAtleastOnce) {
                return 0;
            }
            return byteBuffer.remaining();
        } finally {
            asynchronousRollbackLock.unlock();
        }
    }

    public void close() throws NoTransactionAssociatedException {
        if (closed) {
            return;
        }
        try {
            asynchronousRollbackLock.lock();
            owningSession.checkIfCanContinue();
            if (physicalFileChannel != null) {
                try {
                    physicalFileInputStream.close();//will close the channel too.
                } catch (IOException ioe) {
                    xaFileSystem.notifySystemFailure(ioe);
                }
            }
            vvf.reduceBeingRead();
            if (pooledBuffer != null) {
                xaFileSystem.getBufferPool().checkIn(pooledBuffer);
            }
            closed = true;
        } finally {
            asynchronousRollbackLock.unlock();
        }
    }

    public int read() throws ClosedStreamException, NoTransactionAssociatedException {
        try {
            int eofMark;
            asynchronousRollbackLock.lock();
            checkIfCanContinue();
            if (!filledAtleastOnce) {
                eofMark = refillBuffer();
                if (eofMark == -1) {
                    return -1;
                }
            }
            int nextByte;
            try {
                nextByte = byteBuffer.get();
            } catch (BufferUnderflowException bue) {
                eofMark = refillBuffer();
                if (eofMark == -1) {
                    return -1;
                }
                nextByte = byteBuffer.get();
            }
            return nextByte;
        } finally {
            asynchronousRollbackLock.unlock();
        }
    }

    public int read(byte[] b) throws ClosedStreamException, NoTransactionAssociatedException {
        return read(b, 0, b.length);
    }

    public int read(byte[] b, int off, int len) throws ClosedStreamException, NoTransactionAssociatedException {
        try {
            int eofMark;
            asynchronousRollbackLock.lock();
            checkIfCanContinue();
            if (!filledAtleastOnce) {
                eofMark = refillBuffer();
                if (eofMark == -1) {
                    return -1;
                }
            }
            int remaining = byteBuffer.remaining();
            if (remaining == 0) {
                eofMark = refillBuffer();
                if (eofMark == -1) {
                    return -1;
                }
            }
            remaining = byteBuffer.remaining();
            if (remaining < len) {
                len = remaining;
            }
            byteBuffer.get(b, off, len);
            return len;
        } finally {
            asynchronousRollbackLock.unlock();
        }
    }

    public long skip(long n) throws NoTransactionAssociatedException, ClosedStreamException {
        try {
            asynchronousRollbackLock.lock();
            checkIfCanContinue();
            if (n < 0) {
                throw new IllegalArgumentException("Argument should be a non-negative integer.");
            }
            long filesize = vvf.getLength();
            int bufferedBytesRemaining = 0;
            if(filledAtleastOnce) {
                bufferedBytesRemaining = byteBuffer.remaining();
            }
            long readPositionAfterSkip = (position - bufferedBytesRemaining) + n;
            if (readPositionAfterSkip > filesize) {
                n = n - (readPositionAfterSkip - filesize);
                readPositionAfterSkip = filesize;
            }
            position(readPositionAfterSkip);
            return n;
        } finally {
            asynchronousRollbackLock.unlock();
        }
    }

    public void position(long n) throws NoTransactionAssociatedException, ClosedStreamException {
        try {
            asynchronousRollbackLock.lock();
            checkIfCanContinue();

            long filesize = vvf.getLength();
            if (n < 0 || n > filesize) {
                throw new IllegalArgumentException("New position cannot be negative or more than file size.");
            }

            if (!filledAtleastOnce) {
                position = n;
                return;
            }

            long oldReadPosition = position - byteBuffer.remaining();
            boolean isAhead = n - oldReadPosition > 0;
            long amountOfMove = Math.abs(n - oldReadPosition);
            if (isAhead) {
                if (amountOfMove > byteBuffer.remaining()) {
                    position = n;
                    byteBuffer.position(byteBuffer.limit());
                } else {
                    byteBuffer.position(byteBuffer.position() + (int) amountOfMove);
                }
            } else {
                if (amountOfMove > byteBuffer.position() - this.headerLengthInByteBuffer) {
                    position = n;
                    byteBuffer.position(byteBuffer.limit());
                } else {
                    byteBuffer.position(byteBuffer.position() - (int) amountOfMove);
                }
            }
        } finally {
            asynchronousRollbackLock.unlock();
        }
    }

    //could not throw those rich exception below due to the wrapping by InputStream.mark method.
    public long position() {
        try {
            asynchronousRollbackLock.lock();
            if (!filledAtleastOnce) {
                return 0;
            }
            return position - (byteBuffer.remaining() - headerLengthInByteBuffer);
        } finally {
            asynchronousRollbackLock.unlock();
        }
    }

    private int refillBuffer() {
        try {
            byteBuffer = cachedWritableByteBuffer;
            byteBuffer.clear();
            int numRead = 0;

            if (vvf.isUsingHeavyWriteOptimization()) {
                numRead = vvf.fillUpContentsFromChannel(byteBuffer, position);
                byteBuffer.flip();
                if (numRead != -1) {
                    filledAtleastOnce = true;
                    position += numRead;
                }
                this.headerLengthInByteBuffer = 0;
                return numRead;
            }

            if (position <= vvf.getMappedToThePhysicalFileTill() - 1) {
                long maxAmountToBeRead = vvf.getMappedToThePhysicalFileTill() - position;
                if (maxAmountToBeRead < byteBuffer.limit()) {
                    byteBuffer.limit((int) maxAmountToBeRead);
                }
                physicalFileChannel.position(position);
                while (numRead == 0) {
                    numRead = physicalFileChannel.read(byteBuffer);
                }
                if (numRead != -1) {
                    position += numRead;
                    filledAtleastOnce = true;
                }
                byteBuffer.flip();
                this.headerLengthInByteBuffer = 0;
                return numRead;
            }

            //the only remaining case...

            Buffer newBuffer = vvf.getInMemoryContentBuffer(position);
            if (newBuffer == null) {
                byteBuffer.flip();//to cancel the effect of above "clear". (a bug was
                //reported where after an EOF once, again data started coming.
                return -1;
            }
            newBuffer = newBuffer.createReadOnlyClone();
            if (newBuffer.getBuffer() == null) {
                int offsetInNewBuffer = (int) (position - newBuffer.getFileContentPosition());
                numRead = newBuffer.regenerateContentFromDisk(byteBuffer, offsetInNewBuffer);
                if (numRead != -1) {
                    position += numRead;
                    filledAtleastOnce = true;
                }

                this.headerLengthInByteBuffer = 0;
                return numRead;
            } else {
                this.byteBuffer = newBuffer.getBuffer();
                int offsetInNewBuffer = (int) (position - newBuffer.getFileContentPosition());
                this.byteBuffer.position(newBuffer.getHeaderLength() + offsetInNewBuffer);
                this.byteBuffer.limit(newBuffer.getHeaderLength() + newBuffer.getFileContentLength());
                position = position + newBuffer.getFileContentLength() - offsetInNewBuffer;
                filledAtleastOnce = true;

                this.headerLengthInByteBuffer = this.byteBuffer.position();
                return 0;
            }
        } catch (IOException ioe) {
            xaFileSystem.notifySystemFailure(ioe);
            return -1;
        }
    }

    public boolean isClosed() {
        return closed;
    }

    private void checkIfCanContinue() throws NoTransactionAssociatedException, ClosedStreamException {
        owningSession.checkIfCanContinue();
    }

    public File getSourceFileName() {
        return vvf.getFileName();
    }
}
