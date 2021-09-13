package Apache.http;

import Apache.config.Config;
import Apache.objects.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static Apache.util.JSONTransformer.*;

public class Gateway {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    private static HttpGet getHttpGet(String request) {
        return new HttpGet("http://" + Config.TARGET_SERVER + ":" + Config.TARGET_PORT + request);
    }

    private static String getJsonResponse(HttpGet request) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(request)) {
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Customer> queryCustomers(String query) {
        String response = getJsonResponse(getHttpGet("/customers/" + query));
        return response == null ? null : Arrays.asList(fromJson(Customer[].class, response));
    }


    public static List<Object> queryCounterPeople(String query) {
        HttpGet request = getHttpGet("/customers/" + query);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(request)) {
            Transfer transfer = fromJson(
                    Transfer.class,
                    EntityUtils.toString(response.getEntity())
            );
            return transfer.getContents();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Object> queryParts(String mfgQuery, String numberQuery) {
        try {
            HttpGet request = new HttpGet(
                    "http://" +
                            Config.TARGET_SERVER + ":"
                            + Config.TARGET_PORT +
                            "/parts/" + mfgQuery + "/" + numberQuery
            );
            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(request)) {
                Transfer transfer = fromJson(
                        Transfer.class,
                        EntityUtils.toString(response.getEntity())
                );
                return transfer.getContents();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Object> getOpenInvoices() {
        try {
            HttpGet request = new HttpGet(
                    "http://" +
                            Config.TARGET_SERVER + ":"
                            + Config.TARGET_PORT +
                            "/invoices/open"
            );
            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(request)) {
                Transfer transfer = fromJson(
                        Transfer.class,
                        EntityUtils.toString(response.getEntity())
                );
                return transfer.getContents();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getInvoiceByNumber(String invoiceNumber) {
        try {
            if (invoiceNumber.startsWith("D"))
                invoiceNumber = invoiceNumber.substring(1);
            HttpGet request = new HttpGet(
                    "http://" +
                            Config.TARGET_SERVER + ":"
                            + Config.TARGET_PORT +
                            "/invoice/" + invoiceNumber
            );
            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(request)) {
                Transfer transfer = fromJson(
                        Transfer.class,
                        EntityUtils.toString(response.getEntity())
                );
                return transfer.getFirstContent();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void postSequenceRelease(Sequence sequence, int releaseCode) {
        EXECUTOR.execute(new SequencePostRunnable(sequence, releaseCode));
    }



}
