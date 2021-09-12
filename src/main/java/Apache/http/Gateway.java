package Apache.http;

import Apache.config.Config;
import Apache.objects.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static Apache.util.JSONTransformer.fromJson;

public class Gateway {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    public static List<Customer> queryCustomers(String query) {
        try {
            HttpGet request = new HttpGet(
                    "http://" +
                            Config.TARGET_SERVER + ":"
                            + Config.TARGET_PORT +
                            "/customers/" + query
            );
            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(request)) {
                CustomerResponse customerResponse =
                        fromJson(
                                CustomerResponse.class,
                                EntityUtils.toString(response.getEntity())
                        );
                return customerResponse.getCustomers();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<CounterPerson> queryCounterPeople(String query) {
        try {
            HttpGet request = new HttpGet(
                    "http://" +
                            Config.TARGET_SERVER + ":"
                            + Config.TARGET_PORT +
                            "/counterperson/" + query
            );
            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(request)) {
                CounterPersonResponse cpResponse = fromJson(
                        CounterPersonResponse.class,
                        EntityUtils.toString(response.getEntity())
                );
                return cpResponse.getCounterPeople();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Part> queryParts(String mfgQuery, String numberQuery) {
        try {
            HttpGet request = new HttpGet(
                    "http://" +
                            Config.TARGET_SERVER + ":"
                            + Config.TARGET_PORT +
                            "/parts/" + mfgQuery + "/" + numberQuery
            );
            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(request)) {
                PartResponse partResponse = fromJson(
                        PartResponse.class,
                        EntityUtils.toString(response.getEntity())
                );
                return partResponse.getParts();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Invoice> getOpenInvoices(){
        try {
            HttpGet request = new HttpGet(
                    "http://" +
                            Config.TARGET_SERVER + ":"
                            + Config.TARGET_PORT +
                            "/invoices/open"
            );
            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(request)) {
                InvoiceResponse invoiceResponse = fromJson(
                        InvoiceResponse.class,
                        EntityUtils.toString(response.getEntity())
                );
                return invoiceResponse.getInvoices();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Invoice getInvoiceByNumber(String invoiceNumber){
        try {
            if(invoiceNumber.startsWith("D"))
                invoiceNumber = invoiceNumber.substring(1);
            HttpGet request = new HttpGet(
                    "http://" +
                            Config.TARGET_SERVER + ":"
                            + Config.TARGET_PORT +
                            "/invoice/" + invoiceNumber
            );
            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(request)) {
                InvoiceResponse invoiceResponse = fromJson(
                        InvoiceResponse.class,
                        EntityUtils.toString(response.getEntity())
                );
                return invoiceResponse.getInvoice();
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
