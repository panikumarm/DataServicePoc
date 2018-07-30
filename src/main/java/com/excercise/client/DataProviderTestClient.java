package com.excercise.client;

import java.io.IOException;

public class DataProviderTestClient {

    public static void main(String[] args) throws IOException {

        DataProviderClient dataProviderClient = DataProviderClient.start();


        dataProviderClient.sendMessage("999999999");
        dataProviderClient.sendMessage("999999990");
        dataProviderClient.sendMessage("999999991");
        dataProviderClient.sendMessage("999999992");
        dataProviderClient.sendMessage("999999993");

        DataProviderClient.stop();

    }
}
