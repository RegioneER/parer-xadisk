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

/*
 * Many Thanks to Jasper Siepkes for suggesting the bug fix for
 * https://java.net/jira/browse/XADISK-140
*/

package org.xadisk.bridge.server;

import org.xadisk.bridge.server.conversation.ConversationGateway;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkException;
import org.xadisk.filesystem.NativeXAFileSystem;

public class PointOfContact implements Work {
    
    private final ServerSocket serverSocket;
    private final ServerSocketChannel serverSocketChannel;
    private volatile boolean enabled = true;
    private final ConversationGateway conversationGateway;
    private final NativeXAFileSystem xaFileSystem;

    public PointOfContact(NativeXAFileSystem xaFileSystem, int port) throws IOException, WorkException {
        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocket = this.serverSocketChannel.socket();
        this.serverSocket.bind(new InetSocketAddress(port));
        this.conversationGateway = new ConversationGateway(xaFileSystem);
        this.xaFileSystem = xaFileSystem;
    }

    public void run() {
        try {
            xaFileSystem.startWork(conversationGateway);
            while (enabled) {
                try {
                    SocketChannel clientConverationChannel = serverSocketChannel.accept();
                    conversationGateway.delegateConversation(clientConverationChannel);
                } catch (AsynchronousCloseException asce) {
                    //the only possibility is release().
                    break;
                }
            }
        } catch (Throwable t) {
            xaFileSystem.notifySystemFailure(t);
        } finally {
            conversationGateway.release();
            closeServerSocket();
        }
    }

    public void release() {
        enabled = false;
        closeServerSocket();
        //we need to close the channel here to come out of accept.
    }

    private void closeServerSocket() {
        try {
            serverSocketChannel.close();
        } catch (Throwable t) {
            //no-op.
        }
    }
}
