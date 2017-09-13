import org.testng.Assert;
import org.testng.annotations.Test;
import rest.client.ApacheRestClient;
import rest.model.RequestModel;

public class ApacheClientTest {

    @Test
    void apacheGetTest() {
        ApacheRestClient restAssureClient = new ApacheRestClient();
        RequestModel requestModel = new RequestModel();
        requestModel.setAllLog(true);
        requestModel.setHost("www.google.com");
        requestModel.setPath("/");
        requestModel.setMethod("GET");
        requestModel.setProtocol("https://");
        Assert.assertNotNull(restAssureClient.call(requestModel).getBody());
    }
}
