package Apache.invoicer;

import Apache.config.Config;
import Apache.database.InvoiceBase;
import Apache.objects.Invoice;
import Apache.objects.Invoiceable;
import Apache.objects.TransferableInvoice;
import Apache.workstation.SceneController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static Apache.util.JSONTransformer.fromJson;
import static com.sun.javafx.application.PlatformImpl.exit;

public class InvoicerApplication extends Application {

    private static Invoice INVOICE;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(SceneController.class.getResource("/invoice.fxml"));
        Scene invoiceScene = new Scene(loader.load(), 2550, 1570);
        InvoiceController invoiceController = loader.getController();
        invoiceController.initiate();
        Invoicer.printInvoice(INVOICE);
        exit();
    }

    public static void main(String[] args) {
        INVOICE = InvoiceBase.getInvoiceByNumberFromAll(args[2]);
        launch();
    }

}
