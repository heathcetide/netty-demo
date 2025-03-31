package org.cetide.codeforge;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileChannelTest {
    public static void main(String[] args) throws Exception {
        RandomAccessFile accessFile = new RandomAccessFile("test.txt", "rw");
        FileChannel channel = accessFile.getChannel();

        String content = "All in CodeForge";
        ByteBuffer byteBuffer = ByteBuffer.wrap(content.getBytes());
        channel.write(byteBuffer);
        channel.position(0);

        byteBuffer.clear();
        channel.read(byteBuffer);

        byteBuffer.flip();
        while (byteBuffer.hasRemaining()) {
            System.out.print((char) byteBuffer.get());
        }
        channel.close();
        accessFile.close();
    }
}