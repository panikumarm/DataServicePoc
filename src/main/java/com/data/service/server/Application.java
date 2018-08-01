package com.data.service.server;

import com.data.service.constants.DataServiceConstants;
import com.data.service.enums.CommandType;
import com.data.service.file.IFileHandler;
import com.data.service.file.NumberFileHandler;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.apache.commons.lang3.StringUtils;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public final class Application {

    private ServerSocketChannel serverSocketChannel;
    private ServerSocket serverSocket;
    private Selector selector;
    private BloomFilter<Integer> filter;
    private IFileHandler fileHandler;
    private AtomicLong totalNumberOfRecordsWritten = new AtomicLong(0);
    private AtomicLong totalDuplicateNumbersReceived = new AtomicLong(0);
    private AtomicLong totalNumberOfRecordsReceived = new AtomicLong(0);
    private AtomicBoolean interrupted = new AtomicBoolean(false);
    private ExecutorService requestProcessor;
    private ScheduledExecutorService scheduledExecutorService;

    private Application() {
        this.init();
    }

    private void init() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocket = serverSocketChannel.socket();
            serverSocket.bind(new InetSocketAddress(DataServiceConstants.TCP_PORT),5);
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            filter = BloomFilter.create(Funnels.integerFunnel(), DataServiceConstants.MAX_VALUE, 0.01);
            fileHandler = new NumberFileHandler();
            requestProcessor = Executors.newFixedThreadPool(1);
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        } catch (IOException ioException) {
            System.out.println("Server startup failed:" + ioException.getMessage());
        }
    }

    private void start() {
        requestProcessor.execute(getProcessRequestTask());
        scheduledExecutorService.scheduleWithFixedDelay(getReportTask(), 10, 10, TimeUnit.SECONDS);

    }

    private Runnable getProcessRequestTask() {
        Runnable task = () -> {
            try {
                while (!interrupted.get()) {
                    int select = selector.select();
                    Set<SelectionKey> keys = selector.selectedKeys();
                    for (SelectionKey key : keys) {
                        if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
                            this.accept(key);
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

        };
        return task;
    }

    private Runnable getReportTask() {
        Runnable task = () -> {
            System.out.println("Received " + totalNumberOfRecordsReceived + " unique numbers, " + totalDuplicateNumbersReceived + " duplicates. Unique total: " + totalNumberOfRecordsWritten);
            totalDuplicateNumbersReceived.set(0);
            totalNumberOfRecordsReceived.set(0);
        };

        return task;
    }

    private void accept(SelectionKey key) {
        try {
            ServerSocketChannel channel = (ServerSocketChannel) key.channel();
            SocketChannel socketChannel = channel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
        } catch (Exception exception) {
            System.out.println("Exception occurred while accepting the client: " + exception.getMessage());
        }
    }

    private void read(SelectionKey key) {
        //reading the content
        try (SocketChannel socketChannel = (SocketChannel) key.channel();) {
            ByteBuffer buffer = ByteBuffer.allocate(DataServiceConstants.NUMBER_BYTE_SIZE);
            while ((socketChannel.read(buffer)) > 0) {
                socketChannel.read(buffer);
                buffer.flip();
                CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer);
                String message = new String(charBuffer.array());
                processMessage(message);
                buffer.clear();
            }
            key.cancel();
        } catch (Exception exception) {
            System.out.println("Exception occurred while reading the events: " + exception.getMessage());
        }
    }

    private void processMessage(String message) {
        if (StringUtils.containsIgnoreCase(message, CommandType.TERMINATE.getCommandType())) {
            stop();
        } else {
            writeToFile(Integer.parseInt(message));
        }
    }

    private void writeToFile(Integer number) {
        if (numberAlreadyExistsInFilter(number)) {
            totalDuplicateNumbersReceived.incrementAndGet();
        } else {
            addNumberToFilter(number);
            fileHandler.writeToFile(number);
            totalNumberOfRecordsWritten.incrementAndGet();
            totalNumberOfRecordsReceived.incrementAndGet();
        }
    }

    private boolean numberAlreadyExistsInFilter(int number) {
        return filter.mightContain(number);
    }

    private void addNumberToFilter(int number) {
        filter.put(number);
    }


    public static void run() {
        Application application = new Application();
        application.start();
    }

    private void stop() {
        System.out.println("\n\nShutdown request received...");
        interrupted.getAndSet(true);
        requestProcessor.shutdown();
        scheduledExecutorService.shutdown();
    }

}
