package Apache.server;

import Apache.objects.Invoice;
import Apache.server.database.InvoiceDatabase;
import Apache.server.database.PartDatabase;
import static Apache.util.JSONTransformer.toJson;
import static spark.Spark.*;

class InvoiceServer {

    static void init() {

        get("/invoices/payable/:customer", (req, res) -> {
            System.out.println("[GET] /invoices/payable");
            try (InvoiceDatabase invoiceDatabase = new InvoiceDatabase()) {
                return toJson(invoiceDatabase.getPayableInvoices(req.params(":customer")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        get("/invoices/open", (req, res) -> {
            System.out.println("[GET] /invoices/open");
            try (InvoiceDatabase invoiceDatabase = new InvoiceDatabase()) {
                return toJson(invoiceDatabase.getOpenInvoices());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        get("/invoice/:number", (req, res) -> {
            System.out.println("[GET] /invoice/" + req.params(":number"));
            try (InvoiceDatabase invoiceDatabase = new InvoiceDatabase()) {
                return toJson(invoiceDatabase.getInvoiceByNumber(Integer.parseInt(":number")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        post("/invoice/print/:number", (req, res) -> {
            System.out.println("[POST] /invoice/print/" + req.params(":number"));
            try {
                return Runtime.getRuntime().exec("print-invoice " + req.params(":number"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        put("/invoice/modify/:number/:code", (req, res) -> {
            System.out.println("[PUT] /invoice/modify/" + req.params(":number") + "/" + req.params(":code"));
            try {
                return Runtime.getRuntime().exec("print-invoice " + req.params(":number"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        delete("/invoice/void/:number", (req, res) -> {
            System.out.println("[POST] /invoice/void/" + req.params(":number"));
            try (InvoiceDatabase invoiceDatabase = new InvoiceDatabase();
                 PartDatabase partDatabase = new PartDatabase()) {
                Invoice invoice = invoiceDatabase.getInvoiceByNumber(Integer.parseInt(req.params(":number")));
                partDatabase.postInventoryUpdate(invoice, true);
                return invoiceDatabase.voidInvoice(Integer.parseInt(req.params(":number")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

    }

}
