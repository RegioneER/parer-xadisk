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

import java.io.InputStream;
import java.io.OutputStream;

class ChildProcessOutputStreamReader implements Runnable {

    private InputStream is;
    private OutputStream os;
    private static final int BUFFER_SIZE = 1000;
    private byte buffer[] = new byte[BUFFER_SIZE];

    public ChildProcessOutputStreamReader(InputStream is, OutputStream os) {
        this.is = is;
        this.os = os;
    }
    
    public void run() {
        try {
            while (true) {
                int numRead = is.read(buffer);
                if (numRead == -1) {
                    return;
                }
                os.write(buffer, 0, numRead);
                os.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
