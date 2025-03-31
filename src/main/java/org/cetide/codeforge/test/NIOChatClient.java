package org.cetide.codeforge.test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Set;

public class NIOChatClient {
    public static void main(String[] args) throws Exception {
        // 1. 打开SocketChannel，并设置为非阻塞模式
        SocketChannel clientChannel = SocketChannel.open();
        clientChannel.configureBlocking(false);

        // 2. 连接到服务器
        clientChannel.connect(new InetSocketAddress("localhost", 8080));

        // 3. 获取Selector并注册连接事件
        Selector selector = Selector.open();
        clientChannel.register(selector, SelectionKey.OP_CONNECT);

        while (true) {
            // 4. 阻塞直到有事件发生
            selector.select();

            // 5. 获取所有就绪的事件
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                // 6. 处理连接事件
                if (key.isConnectable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    if (channel.isConnectionPending()) {
                        channel.finishConnect();
                        System.out.println("连接到服务器成功！");

                        // 连接成功后，注册读事件
                        channel.register(selector, SelectionKey.OP_READ);
                    }
                }

                // 7. 处理读事件
                if (key.isReadable()) {
                    readFromServer(key);
                }
            }
        }
    }

    // 读取服务器消息
    private static void readFromServer(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(256);

        int bytesRead = clientChannel.read(buffer);
        if (bytesRead == -1) {
            System.out.println("服务器关闭了连接");
            clientChannel.close();
            return;
        }

        buffer.flip();  // 切换为读取模式
        String message = new String(buffer.array(), 0, buffer.limit());
        System.out.println("收到服务器消息: " + message);

        // 启动一个线程等待用户输入消息并发送到服务器
        new Thread(() -> {
            try {
                sendMessageToServer(clientChannel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // 发送消息到服务器
    private static void sendMessageToServer(SocketChannel clientChannel) throws IOException {
        while (true) {
            byte[] bytes = new byte[256];
            System.out.println("请输入消息：");
            int len = System.in.read(bytes);
            if (len == -1) break;

            ByteBuffer buffer = ByteBuffer.wrap(bytes, 0, len);
            clientChannel.write(buffer);  // 将消息发送给服务器
        }
    }
}