package Apache.http;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

record RequestRunnable(HttpRequestBase httpRequestBase, String payload) implements Runnable{
    @Override
    public void run() {
        try{
            if(payload != null){
                StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_FORM_URLENCODED);
                ((HttpEntityEnclosingRequestBase) httpRequestBase).setEntity(entity);
            }
            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(httpRequestBase)) {
                System.out.println("[Gateway] Heard response " + response.getEntity());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
