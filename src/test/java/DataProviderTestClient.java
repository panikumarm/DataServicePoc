import com.data.service.client.DataProviderClient;

import java.io.IOException;

public class DataProviderTestClient {

    public static void main(String[] args) throws IOException {

        DataProviderClient dataProviderClient = DataProviderClient.start();


        dataProviderClient.sendMessage("999999999");
        dataProviderClient.sendMessage("999999990");
        dataProviderClient.sendMessage("999999991");
        dataProviderClient.sendMessage("99998");
        dataProviderClient.sendMessage("999989");
        dataProviderClient.sendMessage("9999897");
        dataProviderClient.sendMessage("99");
       // dataProviderClient.sendMessage("terminate");

        DataProviderClient.stop();

    }
}
