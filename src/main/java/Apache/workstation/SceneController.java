package Apache.workstation;

import Apache.workstation.inventory.InventoryManagerController;
import Apache.invoicer.InvoiceController;
import Apache.workstation.payments.PaymentController;
import Apache.workstation.pos.PointOfSale;
import Apache.workstation.vehicleselector.VehicleSelectorController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneController {

    private static Stage stage;
    private static Scene pointOfSaleScene;
    private static Scene vehicleDescriptionRequestScene;
    private static Scene inventoryManagerScene;
    private static Scene paymentManagerScene;
    private static FXMLLoader loader;
    private static InventoryManagerController inventoryManagerController;

    public static void initiate() throws IOException {
        loadPointOfSale();
        loadVehicleDescriptionRequest();
        loadInventoryManager();
        loadPaymentManager();
        loadInvoice();
    }

    public static void setToPointOfSale() {
        stage.setScene(pointOfSaleScene);
    }

    public static void setToVehicleDescriptionRequest() {
        stage.setScene(vehicleDescriptionRequestScene);
    }

    public static void setToInventoryManager() {
        stage.setScene(inventoryManagerScene);
        inventoryManagerController.focusStart();
    }

    public static void setStage(Stage newStage) {
        stage = newStage;
    }

    public static void setToPaymentManager() {
        stage.setScene(paymentManagerScene);
    }

    public static void overrideLogin() throws IOException {
        SceneController.initiate();
        SceneController.setToPointOfSale();
        stage.show();
    }

    private static void loadVehicleDescriptionRequest() throws IOException {
        loader = new FXMLLoader(SceneController.class.getResource("/vehicle_selector.fxml"));
        vehicleDescriptionRequestScene = new Scene(loader.load(), 1280, 1024);
        VehicleSelectorController vehicleDescriptionRequestController = loader.getController();
        vehicleDescriptionRequestController.initiate();
        vehicleDescriptionRequestScene.setCursor(Cursor.NONE);
    }

    private static void loadPaymentManager() throws IOException {
        loader = new FXMLLoader(SceneController.class.getResource("/payment_manager.fxml"));
        paymentManagerScene = new Scene(loader.load(), 1280, 1024);
        PaymentController paymentController = loader.getController();
        paymentController.initiate();
        paymentManagerScene.setCursor(Cursor.NONE);
        paymentManagerScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.TAB)
                event.consume();
        });
    }

    private static void loadInventoryManager() throws IOException {
        loader = new FXMLLoader(SceneController.class.getResource("/inventory_manager.fxml"));
        inventoryManagerScene = new Scene(loader.load(), 1280, 1024);
        inventoryManagerController = loader.getController();
        inventoryManagerController.initiate();
        inventoryManagerScene.setCursor(Cursor.NONE);
        inventoryManagerScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.TAB) {
                event.consume();
            }
        });
    }

    private static void loadPointOfSale() throws IOException {
        loader = new FXMLLoader(SceneController.class.getResource("/point_of_sale.fxml"));
        pointOfSaleScene = new Scene(loader.load(), 1280, 1024);
        PointOfSale pointOfSale = loader.getController();
        pointOfSale.initiate();
        pointOfSaleScene.setCursor(Cursor.NONE);

        pointOfSaleScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.TAB)
                event.consume();
        });
    }

    private static void loadInvoice() throws IOException {
        loader = new FXMLLoader(SceneController.class.getResource("/invoice.fxml"));
        Scene invoiceScene = new Scene(loader.load(), 2550, 1570);
        InvoiceController invoiceController = loader.getController();
        invoiceController.initiate();
        invoiceScene.setCursor(Cursor.NONE);
    }

}
