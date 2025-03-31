package org.cetide.codeforge;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ServerSocketChannelTest {
    //    public static void main(String[] args) throws IOException {
//        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
//        serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 8080));
//        serverSocketChannel.configureBlocking(false);
//        while (true){
//            SocketChannel clientChannel = serverSocketChannel.accept();
//            if (clientChannel != null) {
//                System.out.println("客户端已连接：" + clientChannel.getRemoteAddress());
//                clientChannel.close();
//            } else {
//                System.out.println("客户端未收到连接");
//            }
//        }
//    }
//    public static void main(String[] args) throws IOException {
//        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
//        serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 8080));
//        SocketChannel clientChannel = serverSocketChannel.accept();
//        if (clientChannel != null) {
//            System.out.println("客户端已连接：" + clientChannel.getRemoteAddress());
//            SocketChannel serverChannel = serverSocketChannel.accept();
//            int read = serverChannel.read(ByteBuffer.allocate(1024));
//            if (read > 0) {
//                ByteBuffer buffer = ByteBuffer.allocate(read);
//                buffer.clear();
//            }
//            clientChannel.close();
//        } else {
//            System.out.println("客户端未收到连接");
//        }
//    }

    public static void main(String[] args) throws IOException {
        // 创建 ServerSocketChannel 和 Selector
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(8080));  // 绑定端口 8080
        serverChannel.configureBlocking(false);  // 设置为非阻塞模式

        // 创建 Selector
        Selector selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);  // 注册接受连接事件

        System.out.println("服务器启动，监听端口 8080...");

        while (true) {
            // 等待客户端的连接事件
            selector.select();  // 阻塞，直到有事件发生

            // 获取所有准备就绪的事件
            for (SelectionKey key : selector.selectedKeys()) {
                selector.selectedKeys().remove(key);  // 处理完后移除该 SelectionKey

                if (key.isAcceptable()) {
                    // 接受新连接
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel clientChannel = server.accept();
                    clientChannel.configureBlocking(false);  // 设置客户端通道为非阻塞模式
                    clientChannel.register(selector, SelectionKey.OP_READ);  // 注册读事件
                    System.out.println("新客户端连接：" + clientChannel.getRemoteAddress());
                } else if (key.isReadable()) {
                    // 处理读取事件
                    SocketChannel clientChannel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(256);
                    int bytesRead = clientChannel.read(buffer);
                    if (bytesRead == -1) {
                        // 客户端断开连接
                        System.out.println("客户端断开连接：" + clientChannel.getRemoteAddress());
                        clientChannel.close();
                    } else {
                        buffer.flip();
                        String message = new String(buffer.array(), 0, buffer.limit());
                        System.out.println("接收到消息：" + message);

                        // 发送响应给客户端
                        buffer.clear();
                        buffer.put("服务器响应：".getBytes());
                        buffer.flip();
                        clientChannel.write(buffer);
                    }
                }
            }
        }
    }
}
