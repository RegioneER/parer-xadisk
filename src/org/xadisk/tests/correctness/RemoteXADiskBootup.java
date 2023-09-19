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
package org.xadisk.tests.correctness;

import java.io.File;
import org.xadisk.filesystem.NativeXAFileSystem;
import org.xadisk.filesystem.standalone.StandaloneFileSystemConfiguration;

public class RemoteXADiskBootup {

    public static final int DEFAULT_PORT = 5151;
    public static boolean cleanSystemDir = false;
    public static String XADiskSystemDirectory = "C:\\XADiskSystemRemote#" + DEFAULT_PORT;

    public static void main(String args[]) {
        try {
            if (cleanSystemDir) {
                TestUtility.cleanupDirectory(new File(XADiskSystemDirectory));
            }
            StandaloneFileSystemConfiguration configuration = new StandaloneFileSystemConfiguration(XADiskSystemDirectory, "remote");
            configuration.setWorkManagerCorePoolSize(100);
            configuration.setWorkManagerMaxPoolSize(100);
            configuration.setTransactionTimeout(Integer.MAX_VALUE);
            configuration.setServerPort(DEFAULT_PORT);
            configuration.setEnableRemoteInvocations(true);
            configuration.setDeadLockDetectorInterval(1);
            NativeXAFileSystem xaFileSystem = NativeXAFileSystem.bootXAFileSystemStandAlone(configuration);
            xaFileSystem.waitForBootup(2000);

            System.out.println("XADisk System is up for use...");
        } catch (Throwable t) {
            System.err.println(t);
        }
    }
}
