import com.data.service.client.DataProviderClient;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DataProviderClientTest {


    private DataProviderClient dataProviderClient;


    @Before
    public void setup() throws InterruptedException {

        dataProviderClient = DataProviderClient.start();
        Thread.sleep(100);

    }

    @After
    public void tearDown() throws IOException {
        DataProviderClient.stop();
    }

    @Test
    public void test1_SuccessfulSendMessage() {
        dataProviderClient.sendMessage("9");
        dataProviderClient.sendMessage("99");
        dataProviderClient.sendMessage("999");
        dataProviderClient.sendMessage("9999");
        dataProviderClient.sendMessage("99999");
        dataProviderClient.sendMessage("999999");
        dataProviderClient.sendMessage("9999999");
        dataProviderClient.sendMessage("99999999");
        dataProviderClient.sendMessage("999999999");
    }

    @Test(expected = NumberFormatException.class)
    public void test2_InvalidMessage() {
        dataProviderClient.sendMessage("test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test3_MaxValueRequest() {
        dataProviderClient.sendMessage(String.valueOf(Integer.MAX_VALUE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test4_MinValueRequest() {
        dataProviderClient.sendMessage(String.valueOf(Integer.MIN_VALUE));
    }

    @Test
    public void test5_ServerShutdown() throws InterruptedException {
        dataProviderClient.sendMessage("terminate");
    }

}
