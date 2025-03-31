package org.cetide.codeforge;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class SocketChannelTest {

    static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 8080));
        while (!socketChannel.finishConnect()) {
            System.out.println("等待连接中....");
        }
        System.out.println("连接成功！");
//        while (true){
//            System.out.println("请输入消息----");
//            String message = scanner.nextLine();
//            ByteBuffer byteBuffer = ByteBuffer.wrap(message.getBytes());
//            socketChannel.write(byteBuffer);
//            System.out.println("向服务端发送消息: " + message);
//        }
        String message = "Hello, Server!";
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        socketChannel.write(buffer);
        System.out.println("向服务器发送消息：" + message);

        // 读取服务器响应
        buffer.clear();
        int bytesRead = socketChannel.read(buffer);
        if (bytesRead > 0) {
            buffer.flip();
            byte[] data = new byte[buffer.limit()];
            buffer.get(data);
            System.out.println("接收到服务器响应：" + new String(data));
        }

        socketChannel.close();
//        byteBuffer.clear();
//        socketChannel.read(byteBuffer);
//        byteBuffer.flip();
//        while (byteBuffer.hasRemaining()) {
//            System.out.print((char) byteBuffer.get());
//        }
        socketChannel.close();
    }
}
