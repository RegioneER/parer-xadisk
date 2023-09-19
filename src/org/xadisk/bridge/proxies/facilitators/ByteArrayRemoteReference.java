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

public class ByteArrayRemoteReference extends OptimizedRemoteReference<byte[]> {

    private static final long serialVersionUID = 1L;
    private transient byte[] originalByteArray;
    private int lengthForUpdate;
    private transient int offsetForUpdate;
    private byte[] resultBytes;

    public ByteArrayRemoteReference(byte[] b, int offset, int length) {
        this.originalByteArray = b;
        this.lengthForUpdate = length;
        this.offsetForUpdate = offset;
    }

    public byte[] regenerateRemoteObject() {
        return new byte[lengthForUpdate];
    }

    public void setResultObject(byte[] b) {
        resultBytes = b;
    }

    public void mergeWithRemoteObject(byte[] resultBytes) {
		if(resultBytes == null) {
			return;//case when -1 is returned.
		} else {
			byte[] result = resultBytes;
			System.arraycopy(result, 0, originalByteArray, offsetForUpdate, result.length);
		}
    }

    public byte[] getResultObject() {
        return resultBytes;
    }
}
