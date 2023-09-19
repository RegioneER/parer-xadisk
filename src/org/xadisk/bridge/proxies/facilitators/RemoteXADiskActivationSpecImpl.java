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

package org.xadisk.bridge.proxies.facilitators;

import org.xadisk.connector.inbound.XADiskActivationSpecImpl;

public class RemoteXADiskActivationSpecImpl extends XADiskActivationSpecImpl {

    private static final long serialVersionUID = 1L;
    
    private int originalActivationSpecObjectsHashCode = this.hashCode();//this will get serialized too.

    public RemoteXADiskActivationSpecImpl() {
    }

    public RemoteXADiskActivationSpecImpl(XADiskActivationSpecImpl copyFrom) {
        this.setFileNamesAndEventInterests(copyFrom.getFileNamesAndEventInterests());
        this.originalActivationSpecObjectsHashCode = copyFrom.hashCode();
        //no need to set other values like areFilesRemote, remoteAddr, remotePort as these values
        //were meant to be used by local xadisk instance only to decide where to send the activation to.
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RemoteXADiskActivationSpecImpl) {
            RemoteXADiskActivationSpecImpl that = (RemoteXADiskActivationSpecImpl) obj;
            return this.originalActivationSpecObjectsHashCode == that.originalActivationSpecObjectsHashCode;
            //no need to check for the remote xadisk's "system id" because this equals method is always called
            //alongwith that of RemoteMEPF's equals which itself checks for "system id". And these two equals
            //are called during equals of the enclosing entity called epActivation.
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.originalActivationSpecObjectsHashCode;
    }

    public int getOriginalActivationSpecObjectsHashCode() {
        return originalActivationSpecObjectsHashCode;
    }

    public void setOriginalActivationSpecObjectsHashCode(int originalActivationSpecObjectsHashCode) {
        this.originalActivationSpecObjectsHashCode = originalActivationSpecObjectsHashCode;
    }
}
