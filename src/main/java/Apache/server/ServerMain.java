package Apache.server;

import Apache.config.Config;
import Apache.http.*;
import Apache.objects.Customer;
import Apache.objects.Invoice;
import Apache.objects.Sequence;
import Apache.server.database.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

import static Apache.util.JSONTransformer.fromJson;
import static Apache.util.JSONTransformer.toJson;
import static spark.Spark.*;

public class ServerMain {

    public static void main(String[] args) {
        if (!init())
            return;

//        get("/invoices/open", (req, res) -> {
//            System.out.println("[GET] /invoices/open");
//            try (Connection connection = DATA_SOURCE.getConnection()) {
//                InvoiceDatabase invoiceDatabase = new InvoiceDatabase(connection);
//                Transfer transfer =
//                        new Transfer(
//                                invoiceDatabase.getOpenInvoices()
//                        );
//                return toJson(transfer);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        });
//
//        get("/invoice/:number", (req, res) -> {
//            System.out.println("[GET] /invoice/" + req.params(":number"));
//            try (Connection connection = DATA_SOURCE.getConnection()) {
//                InvoiceDatabase invoiceDatabase = new InvoiceDatabase(connection);
//                Transfer transfer =
//                        new Transfer(
//                                invoiceDatabase.getInvoiceByNumber(Integer.parseInt(req.params(":number")))
//                        );
//                return toJson(transfer);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        });
//
//        get("/parts/:mfg/:number", (req, res) -> {
//            System.out.println("[GET] /parts/" + req.params(":mfg") + "/" + req.params(":number"));
//            try (Connection connection = DATA_SOURCE.getConnection()) {
//                PartDatabase partDatabase = new PartDatabase(connection);
//                Transfer transfer =
//                        new Transfer(
//                                partDatabase.getParts(req.params(":mfg"), req.params(":number"))
//                        );
//                return toJson(transfer);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        });
//
//        get("/counterperson/:query", (req, res) -> {
//            System.out.println("[GET] /counterperson/" + req.params(":query"));
//            try (Connection connection = DATA_SOURCE.getConnection()) {
//                CounterPersonDatabase counterBase = new CounterPersonDatabase(connection);
//                Transfer transfer =
//                        new Transfer(
//                                counterBase.getCounterPeople(req.params(":query"))
//                        );
//                return toJson(transfer);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        });

//        get("/counterperson/:number", (req, res) -> {
//            System.out.println("[GET] /counterperson/" + req.params(":number"));
//            try(CounterPersonDatabase counterPersonDatabase = new CounterPersonDatabase()){
//                return toJson(
//                        CounterPersonDatabase.getC(req.params(":query"))
//                );
//            }
//        });

        get("/customers/:query", (req, res) -> {
            System.out.println("[GET] /customers/" + req.params(":query"));
            try(CustomerDatabase customerDatabase = new CustomerDatabase()){
                return toJson(
                        customerDatabase.getCustomers(req.params(":query"))
                );
            }
        });



//        post("/invoice/print/:number", (req, res) -> {
//            System.out.println("[POST] /invoice/print/D" + req.params(":number"));
//            Runtime.getRuntime().exec("print-invoice " + req.params(":number"));
//            return req.params(":number");
//        });
//
//        post("/release/sequence/:code", (req, res) -> {
//            System.out.println("[POST] /release/sequence/" + req.params(":code"));
//            try (Connection connection = DATA_SOURCE.getConnection()) {
//                InvoiceDatabase invoiceBase = new InvoiceDatabase(connection);
//                Sequence sequence = fromJson(Sequence.class, req.body());
//                Invoice invoice =
//                        sequence.releaseToInvoice(
//                                invoiceBase.getInvoiceNumber(),
//                                Integer.parseInt(req.params(":code"))
//                        );
//                invoiceBase.storeInvoice(invoice);
//                PartDatabase partDatabase = new PartDatabase(connection);
//                int invoiceNumber = invoice.getInvoiceNumber();
//                if (!partDatabase.postInventoryUpdate(invoice))
//                    System.out.println(
//                            "[INVENTORY UPDATE] ERROR- FAILED TO POST UPDATE FOR D" +
//                                    invoiceNumber
//                    );
//                System.out.println("[PRINT-INVOICE] D" + invoiceNumber);
//                Runtime.getRuntime().exec(
//                        "print-invoice " + invoiceNumber
//                );
//                return invoiceNumber;
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
//        });

        System.out.println("Server initialized and listening on port " + Config.SERVER_RUN_PORT);
    }

    private static boolean init() {
        System.out.println("Getting data source...");
        DataSource dataSource = Config.getDataSource();
        if (dataSource == null) {
            System.out.println("Failed to establish database connection");
            return false;
        }
        Database.setDataSource(dataSource);
        System.out.println("Setting port...");
        port(Config.SERVER_RUN_PORT);
        return true;
    }
}
