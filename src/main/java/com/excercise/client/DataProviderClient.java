package com.excercise.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class DataProviderClient {

    private static SocketChannel client;
    private static CharBuffer buffer;
    private static DataProviderClient instance;
    private static Socket socket;

    public static DataProviderClient start() {
        if (instance == null) {
            instance = new DataProviderClient();
        }
        return instance;
    }

    public static void stop() throws IOException {
        socket.close();
        buffer = null;
    }

    private DataProviderClient() {
        try {

            client = SocketChannel.open(new InetSocketAddress("localhost", 4000));
            socket = client.socket();
            buffer = CharBuffer.allocate(9);
        } catch (IOException ioException) {
            System.out.println("Exception occurred while preparing the client: " + ioException.getMessage());
        }
    }

    public void sendMessage(String message) {
        try {
            buffer.put(message).flip();
            client.write(StandardCharsets.UTF_8.encode(buffer));
            buffer.clear();
        } catch (IOException e) {
            System.out.println("Exception occurred while sending message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
