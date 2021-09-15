package Apache.http;

import Apache.config.Config;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GatewayUtil {

    protected static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    protected static String getJsonResponse(String request) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(
                     new HttpGet("http://" + Config.TARGET_SERVER + ":" + Config.TARGET_PORT + request)
             )) {
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected static HttpPost getHttpPost(String url){
        return new HttpPost(Config.getTargetServerURL() + url);
    }

    protected static HttpGet getHttpGet(String url){
        return new HttpGet(Config.getTargetServerURL() + url);
    }

    protected static HttpDelete getHttpDelete(String url){
        return new HttpDelete(Config.getTargetServerURL() + url);
    }

    protected  static HttpPut getHttpPut(String url){
        return new HttpPut(Config.getTargetServerURL() + url);
    }

}
