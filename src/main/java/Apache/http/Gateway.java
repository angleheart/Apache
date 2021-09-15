package Apache.http;

import Apache.objects.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;

import java.util.Arrays;
import java.util.List;

import static Apache.http.GatewayUtil.*;
import static Apache.util.JSONTransformer.*;

public class Gateway {

    public static List<Customer> queryCustomers(String query) {
        String response = getJsonResponse("/customers/" + query);
        return response == null ? null : Arrays.asList(fromJson(Customer[].class, response));
    }

    public static List<CounterPerson> queryCounterPeople(String query) {
        String response = getJsonResponse("/counterperson/" + query);
        return response == null ? null : Arrays.asList(fromJson(CounterPerson[].class, response));
    }

    public static List<Part> queryParts(String mfgQuery, String numberQuery) {
        String response = getJsonResponse("/parts/" + mfgQuery + "/" + numberQuery);
        return response == null ? null : Arrays.asList(fromJson(Part[].class, response));
    }

    public static List<Invoice> getOpenInvoices() {
        String response = getJsonResponse("/invoices/open");
        return response == null ? null : Arrays.asList(fromJson(Invoice[].class, response));
    }

    public static List<Invoice> getPayableInvoices(String customerNumber){
        String response = getJsonResponse("/invoices/payable/" + customerNumber);
        return response == null ? null : Arrays.asList(fromJson(Invoice[].class, response));
    }

    public static Invoice getInvoiceByNumber(int invoiceNumber) {
        String response = getJsonResponse("/invoice/" + invoiceNumber);
        return response == null ? null : fromJson(Invoice.class, response);
    }

    public static void postSequenceRelease(Sequence sequence, int releaseCode) {
        System.out.println("[GATEWAY] Post sequence release with code " + releaseCode);


        HttpPost httpPost = new HttpPost("/release/sequence/" + releaseCode);
        StringEntity entity = new StringEntity(toJson(sequence))
    }

    public static List<String> getVehicleYears() {
        String response = getJsonResponse("/vehicles/years");
        return response == null ? null : Arrays.asList(fromJson(String[].class, response));
    }

    public static List<String> getVehicleMakes(String year) {
        String response = getJsonResponse("/vehicles/makes/" + year);
        return response == null ? null : Arrays.asList(fromJson(String[].class, response));
    }

    public static List<String> getVehicleModels(String year, String make) {
        String response = getJsonResponse("/vehicles/models/" + year + "/" + make);
        return response == null ? null : Arrays.asList(fromJson(String[].class, response));
    }

    public static List<String> getVehicleEngines(String year, String make, String model) {
        String response = getJsonResponse("/vehicles/engines/" + year + "/" + make + "/" + model);
        return response == null ? null : Arrays.asList(fromJson(String[].class, response));
    }

    public static boolean voidInvoice(int invoiceNumber){
        String response = getJsonResponse("/invoice/void/" + invoiceNumber);
        return Boolean.parseBoolean(response);
    }

    public static boolean setInvoiceReleaseCode(int invoiceNumber, int releaseCode){
        String response = getJsonResponse("/invoice/modify/" + invoiceNumber + "/" + releaseCode);
        return Boolean.parseBoolean(response);
    }

    public static List<Sequence> getSequences(){
        String response = getJsonResponse("/sequence/get");
        return response == null ? null : Arrays.asList(fromJson(Sequence[].class, response));
    }

    public static void killSequence(Sequence sequence){
        EXECUTOR.execute(
                new RequestRunnable(
                        new HttpDelete()
                        getHttpDelete()
                )
        );
    }

    public static void holdSequence(Sequence sequence){
        EXECUTOR.execute(new RequestRunnable(sequence, "/sequence/hold"));
    }

}
