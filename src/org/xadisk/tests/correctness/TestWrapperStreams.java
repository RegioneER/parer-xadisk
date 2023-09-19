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

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import org.xadisk.additional.XAFileInputStreamWrapper;
import org.xadisk.additional.XAFileOutputStreamWrapper;
import org.xadisk.bridge.proxies.interfaces.Session;
import org.xadisk.filesystem.NativeXAFileSystem;
import org.xadisk.filesystem.standalone.StandaloneFileSystemConfiguration;

public class TestWrapperStreams {
    private static final String SEPERATOR = File.separator;
    private static final String currentWorkingDirectory = System.getProperty("user.dir");
    private static final String XADiskSystemDirectory = currentWorkingDirectory + SEPERATOR + "XADiskSystem";

    public static void main(String args[]) {
        try {
            StandaloneFileSystemConfiguration configuration = new StandaloneFileSystemConfiguration(XADiskSystemDirectory, "local");
            configuration.setWorkManagerCorePoolSize(100);
            configuration.setWorkManagerMaxPoolSize(100);
            configuration.setServerPort(9998);

            NativeXAFileSystem xaFileSystem = NativeXAFileSystem.bootXAFileSystemStandAlone(configuration);
            xaFileSystem.waitForBootup(-1L);

            Session session = xaFileSystem.createSessionForLocalTransaction();
            InputStream is = new XAFileInputStreamWrapper(session.createXAFileInputStream(new File("C:\\a.txt"), false));
            is.mark(100);
            System.out.println((char)is.read());
            System.out.println((char)is.read());
            System.out.println((char)is.read());
            is.reset();
            System.out.println((char)is.read());
            is.close();

            OutputStream os = new XAFileOutputStreamWrapper(session.createXAFileOutputStream(new File("C:\\b.txt"), false));
            os.write('a');
            os.write('b');
            os.close();
            session.commit();

            xaFileSystem.shutdown();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
