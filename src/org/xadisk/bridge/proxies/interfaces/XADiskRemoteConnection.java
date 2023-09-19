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

package org.xadisk.bridge.proxies.interfaces;

import org.xadisk.connector.outbound.XADiskConnection;
import org.xadisk.connector.outbound.XADiskConnectionFactory;

/**
 * This interface is applicable only when invoking XADisk as a JCA Resource Adapter.
 * <p> This interface is a marker for connections to remote XADisk instances and is required/used
 * only by the JavaEE container itself. It should never be used inside JavaEE application code;
 * instead {@link XADiskConnection} interface should be used to refer to connection objects
 * obtained from both {@link XADiskConnectionFactory} and {@link XADiskRemoteConnectionFactory}.
 * (<i>In fact, {@link XADiskRemoteConnectionFactory} does not generate instances of this interface.</i>)
 *
 * @since 1.0
 */

public interface XADiskRemoteConnection extends XADiskConnection {
}
