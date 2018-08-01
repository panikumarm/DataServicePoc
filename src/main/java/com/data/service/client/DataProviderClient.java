package com.data.service.client;

import com.data.service.constants.DataServiceConstants;
import com.data.service.enums.CommandType;
import org.apache.commons.lang3.StringUtils;

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
            return new DataProviderClient();
        }
        return instance;
    }

    public static void stop() throws IOException {
        socket.close();
        buffer = null;
    }

    private DataProviderClient() {
        try {

            client = SocketChannel.open(new InetSocketAddress(DataServiceConstants.LOCALHOST, DataServiceConstants.TCP_PORT));
            socket = client.socket();
            buffer = CharBuffer.allocate(DataServiceConstants.NUMBER_BYTE_SIZE);
        } catch (IOException ioException) {
            System.out.println("Exception occurred while preparing the client: " + ioException.getMessage());
        }
    }

    public void sendMessage(String message) {
        if (!requestValid(message)) throw new IllegalArgumentException("Invalid argument message received..");
        try {

            buffer.put(prependZeroes(message)).flip();
            client.write(StandardCharsets.UTF_8.encode(buffer));
            buffer.clear();
        } catch (IOException e) {
            System.out.println("Exception occurred while sending message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean requestValid(String message) {
        if (StringUtils.isEmpty(message)) return false;
        if (StringUtils.containsIgnoreCase(message, CommandType.TERMINATE.getCommandType())) return true;
        int value = Integer.parseInt(message);
        if (value > DataServiceConstants.MAX_VALUE || value < DataServiceConstants.MIN_VALUE) return false;
        return true;
    }

    private String prependZeroes(String number) {
        if (number.length() < DataServiceConstants.NUMBER_BYTE_SIZE)
            return ("000000000" + number).substring(number.length());
        return number;
    }
}
