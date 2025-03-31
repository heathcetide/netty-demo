package org.cetide.codeforge;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SocketChannelTest {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 8080));
        while (!socketChannel.finishConnect()) {
            System.out.println("等待连接中....");
        }
        System.out.println("连接成功！");
        String message = "Hello CodeForge!";
        ByteBuffer byteBuffer = ByteBuffer.wrap(message.getBytes());
        socketChannel.write(byteBuffer);

        byteBuffer.clear();
        socketChannel.read(byteBuffer);
        byteBuffer.flip();
        while (byteBuffer.hasRemaining()) {
            System.out.print((char) byteBuffer.get());
        }
        socketChannel.close();
    }
}
