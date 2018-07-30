import com.excercise.client.DataProviderClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class DataProviderClientTest {


   private DataProviderClient dataProviderClient;


    @Before
    public void setup() {

       dataProviderClient=  DataProviderClient.start();

    }

    @After
    public void tearDown() throws IOException {
        DataProviderClient.stop();
    }

    @Test
    public void test(){
        dataProviderClient.sendMessage("999999991");
        dataProviderClient.sendMessage("999999992");
        dataProviderClient.sendMessage("999999993");
        dataProviderClient.sendMessage("999999994");
        dataProviderClient.sendMessage("999999995");
        dataProviderClient.sendMessage("999999996");
        dataProviderClient.sendMessage("999999997");
    }
}
