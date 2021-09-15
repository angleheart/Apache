package Apache.invoicer;

import Apache.objects.Invoice;
import Apache.server.database.Database;
import Apache.server.database.InvoiceDatabase;
import Apache.workstation.SceneController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.SQLException;

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
        Database.init();
        try(InvoiceDatabase invoiceDatabase = new InvoiceDatabase()){
            INVOICE = invoiceDatabase.getInvoiceByNumber(Integer.parseInt(args[2]));
            launch();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

}
