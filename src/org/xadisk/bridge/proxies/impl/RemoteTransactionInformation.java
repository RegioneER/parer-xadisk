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

package org.xadisk.bridge.proxies.impl;

import org.xadisk.filesystem.TransactionInformation;

public class RemoteTransactionInformation extends TransactionInformation {

    private static final long serialVersionUID = 1L;
    
    private String serverAddress;
    private Integer serverPort;

    public RemoteTransactionInformation(TransactionInformation transactionInformation, String serverAddress, Integer serverPort) {
        super(transactionInformation.getGlobalTransactionId(), transactionInformation.getBranchQualifier(), transactionInformation.getFormatId());
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof RemoteTransactionInformation) {
            RemoteTransactionInformation remoteTransactionInformation = (RemoteTransactionInformation) obj;
            return super.equals(obj) && remoteTransactionInformation.getServerAddress().equals(serverAddress)
                    && remoteTransactionInformation.getServerPort().equals(serverPort);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
