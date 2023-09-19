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
import java.io.OutputStream;
import org.xadisk.bridge.proxies.interfaces.XAFileOutputStream;
import org.xadisk.filesystem.exceptions.XAApplicationException;

/**
 * This class acts as a wrapper around the {@link XAFileOutputStream} for the purpose of
 * providing the standard {@link OutputStream} implementation (because {@link XAFileOutputStream}
 * itself does not extend the {@link OutputStream}).
 *
 * @since 1.0
 */

public class XAFileOutputStreamWrapper extends OutputStream {

    private XAFileOutputStream xos;

    /**
     * The sole constructor which takes the {@link XAFileOutputStream} to be wrapped.
     * @param xos the {@link XAFileOutputStream} to be wrapped.
     */
    public XAFileOutputStreamWrapper(XAFileOutputStream xos) {
        this.xos = xos;
    }

    @Override
    public void close() throws IOException {
        try {
            xos.close();
        } catch (XAApplicationException e) {
            throw Utilities.wrapWithIOException(e);
        }
    }

    @Override
    public void flush() throws IOException {
        try {
            xos.flush();
        } catch (XAApplicationException e) {
            throw Utilities.wrapWithIOException(e);
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        try {
            xos.write(b);
        } catch (XAApplicationException e) {
            throw Utilities.wrapWithIOException(e);
        }
    }

    @Override
    public void write(int b) throws IOException {
        try {
            xos.write(b);
        } catch (XAApplicationException e) {
            throw Utilities.wrapWithIOException(e);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        try {
            xos.write(b, off, len);
        } catch (XAApplicationException e) {
            throw Utilities.wrapWithIOException(e);
        }
    }
}
