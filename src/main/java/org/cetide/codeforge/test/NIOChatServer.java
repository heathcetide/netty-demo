package org.cetide.codeforge.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class NIOChatServer {
    // 用于保存所有客户端的通道
    private static final Set<SocketChannel> clientChannels = new HashSet<>();

    public static void main(String[] args) throws Exception {
        // 1. 创建 ServerSocketChannel 和 Selector
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8080));
        serverSocketChannel.configureBlocking(false);  // 设置非阻塞模式

        Selector selector = Selector.open();  // 创建 Selector
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);  // 注册到 Selector 上，监听接入事件

        System.out.println("聊天服务器启动，等待客户端连接...");
        while (true) {
            // 2. 阻塞等待客户端的连接
            selector.select();  // 这里是阻塞的，直到有事件发生

            // 3. 获取所有准备就绪的通道
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                // 4. 处理接入连接请求
                if (key.isAcceptable()) {
                    acceptConnection(serverSocketChannel, selector);
                }

                // 5. 处理客户端请求（读取消息）
                if (key.isReadable()) {
                    readFromClient(key);
                }
            }
        }
    }

    // 处理客户端连接
    private static void acceptConnection(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);  // 设置客户端 SocketChannel 为非阻塞

        // 注册客户端通道到 Selector，监听读事件
        clientChannel.register(selector, SelectionKey.OP_READ);

        // 将客户端通道加入到客户端集合中
        clientChannels.add(clientChannel);

        System.out.println("客户端已连接：" + clientChannel.getRemoteAddress());
    }

    // 处理客户端消息（读取并广播）
    private static void readFromClient(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(256);

        int bytesRead = clientChannel.read(buffer);
        if (bytesRead == -1) {
            // 客户端断开连接
            System.out.println("客户端断开连接：" + clientChannel.getRemoteAddress());
            clientChannels.remove(clientChannel);  // 从集合中移除客户端
            clientChannel.close();
            return;
        }

        buffer.flip();  // 切换为读模式
        String message = new String(buffer.array(), 0, buffer.limit());
        System.out.println("收到客户端消息：" + message);

        // 广播消息给其他客户端
        broadcastMessage(clientChannel, message);
    }

    // 广播消息给所有客户端
    private static void broadcastMessage(SocketChannel sender, String message) throws IOException {
        for (SocketChannel clientChannel : clientChannels) {
            if (clientChannel != sender && clientChannel.isOpen()) {
                ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
                clientChannel.write(buffer);  // 向客户端发送消息
            }
        }
    }
}