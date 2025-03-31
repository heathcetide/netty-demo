package org.cetide.codeforge;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class SelectorTest {
    public static void main(String[] args) throws IOException {
        // 打开Selector
        Selector selector = Selector.open();
        // 打开ServerSocketChannel
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(8080));
        // 将ServerSocketChannel注册到Selector，监听接受连接事件
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务器启动，监听端口8080...");
        while (true) {
            // 阻塞等待事件
            selector.select();
            // 获取所有准备好的SelectionKey
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                // 移除当前SelectionKey，防止重复处理
                keyIterator.remove();
                if (key.isAcceptable()) {
                    // 处理接受连接事件
                    ServerSocketChannel srvChannel = (ServerSocketChannel) key.channel();
                    SocketChannel clientChannel = srvChannel.accept();
                    clientChannel.configureBlocking(false);
                    // 注册读事件
                    clientChannel.register(selector, SelectionKey.OP_READ);
                    System.out.println("接受新的连接：" + clientChannel.getRemoteAddress());
                }
                if (key.isReadable()) {
                    // 处理读取事件
                    SocketChannel clientChannel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int bytesRead = clientChannel.read(buffer);

                    if (bytesRead == -1) {
                        // 客户端关闭连接
                        clientChannel.close();
                        System.out.println("连接关闭：" + clientChannel.getRemoteAddress());
                    } else {
                        buffer.flip();
                        byte[] data = new byte[buffer.limit()];
                        buffer.get(data);
                        System.out.println("接收到数据：" + new String(data));

                        // 回写数据给客户端
                        buffer.rewind();
                        clientChannel.write(buffer);
                    }
                }
            }
        }
    }
}
