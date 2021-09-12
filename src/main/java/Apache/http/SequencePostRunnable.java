package Apache.http;

import Apache.config.Config;
import Apache.objects.Sequence;
import Apache.util.JSONTransformer;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

record SequencePostRunnable(Sequence sequence, int releaseCode) implements Runnable {

    @Override
    public void run() {
        try {
            String payload = JSONTransformer.toJson(sequence);
            StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_FORM_URLENCODED);
            HttpPost request = new HttpPost(Config.getTargetServerURL() + "/release/sequence/" + releaseCode);
            request.setEntity(entity);
            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(request)) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
