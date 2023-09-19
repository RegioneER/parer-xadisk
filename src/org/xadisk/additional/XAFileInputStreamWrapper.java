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

package org.xadisk.additional;

import java.io.IOException;
import java.io.InputStream;
import org.xadisk.bridge.proxies.interfaces.XAFileInputStream;
import org.xadisk.filesystem.exceptions.XAApplicationException;

/**
 * This class acts as a wrapper around the {@link XAFileInputStream} for the purpose of
 * providing the standard {@link InputStream} implementation
 * (because {@link XAFileInputStream} itself does not extend the {@link InputStream}).
 *
 * @since 1.0
 */

public class XAFileInputStreamWrapper extends InputStream {

    private XAFileInputStream xis;
    private long latestMarkPoint = -1;

    /**
     * The sole constructor which takes the {@link XAFileInputStream} to be wrapped.
     * @param xis the {@link XAFileInputStream} to be wrapped.
     */
    public XAFileInputStreamWrapper(XAFileInputStream xis) {
        this.xis = xis;
    }

    @Override
    public int available() throws IOException {
        try {
            return xis.available();
        } catch(XAApplicationException e) {
            throw Utilities.wrapWithIOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            xis.close();
        } catch(XAApplicationException e) {
            throw Utilities.wrapWithIOException(e);
        }
    }

    @Override
    public synchronized void mark(int readlimit) {
        this.latestMarkPoint = xis.position();
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public int read() throws IOException {
        try {
            return xis.read();
        } catch(XAApplicationException e) {
            throw Utilities.wrapWithIOException(e);
        }
    }

    @Override
    public int read(byte[] b) throws IOException {
        try {
            return xis.read(b);
        } catch(XAApplicationException e) {
            throw Utilities.wrapWithIOException(e);
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        try {
            return xis.read(b, off, len);
        } catch(XAApplicationException e) {
            throw Utilities.wrapWithIOException(e);
        }
    }

    @Override
    public synchronized void reset() throws IOException {
        //why do the mark/reset methods are syncronous in the IS clases.
        if(latestMarkPoint == -1) {
            throw new IOException("No corresponding mark does exist.");
        }
        try {
            //we do not honor the readlimit; a more flexible approach, which IS spec also allows.
            xis.position(latestMarkPoint);
            this.latestMarkPoint = -1;
        } catch(XAApplicationException e) {
            throw Utilities.wrapWithIOException(e);
        }
    }

    @Override
    public long skip(long n) throws IOException {
        if (n <= 0) {
            return 0;
        }
        try {
            return xis.skip(n);
        } catch(XAApplicationException e) {
            throw Utilities.wrapWithIOException(e);
        }
    }
}
