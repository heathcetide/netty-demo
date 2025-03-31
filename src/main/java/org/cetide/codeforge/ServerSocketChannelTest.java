package org.cetide.codeforge;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ServerSocketChannelTest {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 8080));
        serverSocketChannel.configureBlocking(false);
        while (true){
            SocketChannel clientChannel = serverSocketChannel.accept();
            if (clientChannel != null) {
                System.out.println("客户端已连接：" + clientChannel.getRemoteAddress());
                clientChannel.close();
            } else {
                System.out.println("客户端未收到连接");
            }
        }
    }
}
