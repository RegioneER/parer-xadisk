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
 * Many Thanks to Jasper Siepkes for suggesting the bug fix for
 * https://java.net/jira/browse/XADISK-140
*/

package org.xadisk.bridge.server.conversation;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.resource.spi.work.Work;
import org.xadisk.filesystem.NativeXAFileSystem;

public class ConversationGateway implements Work {

    private volatile boolean enabled = true;
    private final Selector selector;
    private ConcurrentLinkedQueue<SocketChannel> channelsToRegister = new ConcurrentLinkedQueue<SocketChannel>();
    private final NativeXAFileSystem xaFileSystem;

    public ConversationGateway(NativeXAFileSystem xaFileSystem) throws IOException {
        this.xaFileSystem = xaFileSystem;
        this.selector = Selector.open();
    }

    public void delegateConversation(SocketChannel clientChannel) throws IOException, InterruptedException {
        clientChannel.configureBlocking(false);
        channelsToRegister.add(clientChannel);
        selector.wakeup();
    }

    public void run() {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(1000);
            while (enabled) {
                int n = selector.select();
                while (true) {
                    SocketChannel newConversationChannel = channelsToRegister.poll();
                    if (newConversationChannel == null) {
                        break;
                    }
                    ConversationContext context = new ConversationContext(newConversationChannel, xaFileSystem);
                    newConversationChannel.register(selector, SelectionKey.OP_READ, context);
                }
                if (n == 0) {
                    continue;
                }
                Set<SelectionKey> selectedReadable = selector.selectedKeys();
                for (SelectionKey selectionKey : selectedReadable) {
                    ConversationContext context = (ConversationContext) selectionKey.attachment();
                    SocketChannel channel = context.getConversationChannel();

                    buffer.clear();
                    try {
                        int numRead = channel.read(buffer);
                        if (numRead == -1) {
                            throw new EOFException();
                        }
                        buffer.flip();
                        context.updateWithConversation(buffer);
                    } catch (IOException ioe) {
                        closeClientConversation(selectionKey, channel);
                    }
                }
                selectedReadable.clear();
            }
        } catch (Throwable t) {
            xaFileSystem.notifySystemFailure(t);
        } finally {
            closeAllClientConversations();
        }
    }
    
    public void release() {
        this.enabled = false;
        this.selector.wakeup();
    }
    
    private void closeClientConversation(SelectionKey selectionKey, SocketChannel channel) {
        selectionKey.cancel();
        try {
            channel.socket().close();
        } catch(Throwable t) {
            //no-op.
        }
    }

    private void closeAllClientConversations() {
        //no need to cancel keys as we will close selector itself.
        Set<SelectionKey> connectedClientKeys = selector.keys();
        for (SelectionKey key : connectedClientKeys) {
            try {
                ((SocketChannel) key.channel()).socket().close();
            } catch (Throwable t) {
                //no-op.
            }
        }
        try {
            selector.close();
        } catch(Throwable t) {
            //no-op.
        }
    }
}
