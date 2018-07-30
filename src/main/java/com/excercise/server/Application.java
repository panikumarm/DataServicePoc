package com.excercise.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public final class Application {

    private ServerSocketChannel serverSocketChannel;
    private ServerSocket serverSocket;
    private Selector selector;

    private Application() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocket = serverSocketChannel.socket();
            serverSocket.bind(new InetSocketAddress(4000));
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException ioException) {
            System.out.println("Server startup failed:" + ioException.getMessage());
        }
    }

    private void start() {
        try {
            while (true) {
                System.out.println("\nListening to events...");
                int select = selector.select();
                System.out.printf("Received %d events", select);
                Set<SelectionKey> keys = selector.selectedKeys();
                for (SelectionKey key : keys) {
                    if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
                        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                        SocketChannel socketChannel = channel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                        keys.remove(key);
                    } else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
                        this.read(key);
                        keys.remove(key);
                    }
                }
            }
        } catch (IOException ioException) {
            System.out.println("Server processor failed:" + ioException.getMessage());
        }

    }

//    private void accept(SelectionKey key) {
//        try (ServerSocketChannel channel = (ServerSocketChannel) key.channel();
//             SocketChannel socketChannel = channel.accept()) {
//            socketChannel.configureBlocking(false);
//            socketChannel.register(selector, SelectionKey.OP_READ);
//        } catch (Exception exception) {
//            System.out.println("Exception occurred while accepting the events: " + exception.getMessage());
//        }
//    }

    private void read(SelectionKey key) {
        //reading the content
        try (SocketChannel socketChannel = (SocketChannel) key.channel();) {
            ByteBuffer buffer = ByteBuffer.allocate(9);
            int nBytes = 0;
            while((nBytes = nBytes = socketChannel.read(buffer)) > 0) {
                socketChannel.read(buffer);
                buffer.flip();
                CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer);
                String result = new String(charBuffer.array());
                System.out.println("Read: " + result);
                buffer.clear();
            }
            key.cancel();
        } catch (Exception exception) {
            System.out.println("Exception occurred while reading the events: " + exception.getMessage());
        }


    }

    public static void run() {
        Application application = new Application();
        application.start();
    }

    private void stop() {

    }

}
