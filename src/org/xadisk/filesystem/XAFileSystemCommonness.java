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

package org.xadisk.filesystem;

import java.io.IOException;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import org.xadisk.bridge.proxies.interfaces.Session;
import org.xadisk.bridge.proxies.interfaces.XAFileSystem;
import org.xadisk.connector.inbound.EndPointActivation;

public interface XAFileSystemCommonness extends XAFileSystem {

    public Session createSessionForXATransaction(Xid xid);

    public Session getSessionForTransaction(Xid xid);

    public void notifySystemFailureAndContinue(Throwable t);

    public int getDefaultTransactionTimeout();

    public Xid[] recover(int flag) throws XAException;

    public XAResource getEventProcessingXAResourceForRecovery();

    public void registerEndPointActivation(EndPointActivation epActivation) throws IOException;

    public void deRegisterEndPointActivation(EndPointActivation epActivation) throws IOException;

    //it is not a good idea to expose this as an API. If we do that we will have to
    //return an accurate true/false based on actual instance checking (which is feasible
    //only via interaction with the underlying xadisk instance). Doesn't sound
    //worth implementating that way just to expose an api. So, we keep this method
    //internal and the implementation of checking based on remoteAddressPort and
    //local instance-id would be enough for use in XAR.isSameRM.
    public boolean pointToSameXAFileSystem(XAFileSystem xaFileSystem);
}
