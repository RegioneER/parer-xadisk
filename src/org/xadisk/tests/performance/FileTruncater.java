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

/*
*/

package org.xadisk.tests.performance;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicLong;
import org.xadisk.bridge.proxies.interfaces.Session;
import org.xadisk.bridge.proxies.interfaces.XAFileSystem;
import org.xadisk.bridge.proxies.interfaces.XAFileSystemProxy;

/**
 * Note that these performance tests are "under construction". Your suggestions about
 * writing these tests, setting up the system and taking measurements are always welcome.
 * Thanks.
 */

public class FileTruncater extends TimeMeasuredWork {

    private File filePath;

    public FileTruncater(File filePath, AtomicLong timeTaken, boolean useXADisk) {
        super(timeTaken, useXADisk);
        this.filePath = filePath;
    }

    @Override
    protected void doWorkDirectly() throws Exception {
        FileChannel fc = new FileOutputStream(filePath, true).getChannel();
        fc.truncate((long) (Appraiser.FILE_SIZE * 0.9));
        fc.force(false);
        fc.close();
    }

    @Override
    protected void doWorkViaXADisk() throws Exception {
        XAFileSystem xafs = XAFileSystemProxy.getNativeXAFileSystemReference("");
        Session session = xafs.createSessionForLocalTransaction();
        session.truncateFile(filePath, (long) (Appraiser.FILE_SIZE * 0.9));
        session.commit();
    }
}
