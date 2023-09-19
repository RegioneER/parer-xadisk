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

package org.xadisk.filesystem.utilities;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.transaction.xa.XAException;

public class MiscUtils {

    public static XAException createXAExceptionWithCause(int errorCode, Throwable cause) {
        XAException xae = new XAException(errorCode);
        xae.initCause(cause);
        return xae;
    }
    
    public static boolean isRootPath(File f) {
        if(f.getParentFile() == null) {
            return true;
        }
        if(f.getAbsolutePath().startsWith("\\\\")) {
            File parent = f.getParentFile();
            //parent=null is infeasible here.
            File grandParent = parent.getParentFile();
            if(grandParent == null) {
                return false;
            }
            if(grandParent.getParentFile() == null) {
                return true;
            }
        }
        return false;
    }

    public static void closeAll(Closeable... closeables) {
        for(Closeable closeable: closeables) {
            if(closeable != null) {
                try {
                    closeable.close();
                } catch(IOException ioe) {
                }
            }
        }
    }

    public static ArrayList<String> isDescedantOf(File a, File b) {
        File parentA = a.getParentFile();
        ArrayList<String> stepsToDescend = new ArrayList<String>(10);
        stepsToDescend.add(a.getName());
        while (parentA != null) {
            if (parentA.equals(b)) {
                return stepsToDescend;
            }
            stepsToDescend.add(parentA.getName());
            parentA = parentA.getParentFile();
        }
        return null;
    }
}
