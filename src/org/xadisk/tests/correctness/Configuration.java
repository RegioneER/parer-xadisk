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
import java.net.ServerSocket;
import java.util.Properties;

public class Configuration {

    private static final String xadiskSystemDirectory;
    private static final String testRootDirectory;
    private static int nextServerPort;

    static {
        Properties properties = new Properties();
        try {
            InputStream configurationStream = ClassLoader.getSystemResourceAsStream("org/xadisk/tests/correctness/configuration.properties");
            properties.load(configurationStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        xadiskSystemDirectory = properties.getProperty("XADISK_SYSTEM_DIRECTORY");
        testRootDirectory = properties.getProperty("TEST_ROOT_DIRECTORY");
        nextServerPort = Integer.valueOf(properties.getProperty("FIRST_SERVER_PORT"));
    }

    public static String getTestRootDirectory() {
        return testRootDirectory;
    }

    public static String getXADiskSystemDirectory() {
        return xadiskSystemDirectory;
    }

    public static int getNextServerPort() {
        while (true) {
            try {
                ServerSocket serverSocket = new ServerSocket(nextServerPort);
                serverSocket.close();
                break;
            } catch (Exception e) {
                nextServerPort++;
            }
        }
        return nextServerPort++;
    }
}
