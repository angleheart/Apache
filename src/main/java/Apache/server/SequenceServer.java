package Apache.server;

import Apache.objects.Invoice;
import Apache.objects.Sequence;
import Apache.server.database.InvoiceDatabase;
import Apache.server.database.PartDatabase;
import Apache.server.database.SequenceDatabase;

import static Apache.util.JSONTransformer.fromJson;
import static Apache.util.JSONTransformer.toJson;
import static spark.Spark.*;

class SequenceServer {

    static void init() {

        post("/sequence/release/:code", (req, res) -> {
            System.out.println("[POST] /sequence/release/" + req.params(":code"));
            try (InvoiceDatabase invoiceDatabase = new InvoiceDatabase();
                 PartDatabase partDatabase = new PartDatabase()) {
                Sequence sequence = fromJson(Sequence.class, req.body());
                Invoice invoice =
                        sequence.releaseToInvoice(
                                invoiceDatabase.getInvoiceNumber(),
                                Integer.parseInt(req.params(":code"))
                        );
                invoiceDatabase.storeInvoice(invoice);
                partDatabase.postInventoryUpdate(invoice);
                return Runtime.getRuntime().exec("print-invoice " + invoice.getInvoiceNumber());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });

        delete("/sequence/kill/:name", (req, res) -> {
            System.out.println("[DELETE] /sequence/kill/" + req.params(":name"));
            try (SequenceDatabase sequenceDatabase = new SequenceDatabase()) {
                Sequence sequence = fromJson(Sequence.class, req.body());
                return sequenceDatabase.killSequence(sequence.getSaveName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        get("/sequence/get", (req, res) -> {
            System.out.println("[GET] /sequences/get");
            try (SequenceDatabase sequenceDatabase = new SequenceDatabase()) {
                return toJson(sequenceDatabase.getSequences());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        put("/sequence/hold", (req, res) -> {
            System.out.println("[PUT] /sequences/hold");
            try (SequenceDatabase sequenceDatabase = new SequenceDatabase()) {
                Sequence sequence = fromJson(Sequence.class, req.body());
                return sequenceDatabase.holdSequence(sequence);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

    }

}
