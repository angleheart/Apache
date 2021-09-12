package Apache.invoicer;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class InvoiceController extends Application implements Runnable{

    @FXML
    private AnchorPane rootPane;
    @FXML
    private Label customerNameLabel;
    @FXML
    private Label customerAddressLabel;
    @FXML
    private Label customerAddress2Label;
    @FXML
    private Label vehicleDescriptionLabel;
    @FXML
    private Label shipToLabel;
    @FXML
    private Label pageNumberLabel;
    @FXML
    private Label pageCountLabel;
    @FXML
    private Label invoiceNumberLabel;
    @FXML
    private Label customerNumberLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label poLabel;
    @FXML
    private Label counterPersonLabel;
    @FXML
    private Label termsLabel;
    @FXML
    private Label freightTotalLabel;
    @FXML
    private Label subTotalLabel;
    @FXML
    private Label salesTaxLabel;
    @FXML
    private Label totalLabel;
    @FXML
    private VBox mfgPartNumberBox;
    @FXML
    private VBox descriptionBox;
    @FXML
    private VBox quantityBox;
    @FXML
    private VBox listBox;
    @FXML
    private VBox unitBox;
    @FXML
    private VBox coreBox;
    @FXML
    private VBox extendedBox;
    @FXML
    private VBox taxBox;
    @FXML
    private Label partsForLabel;

    public void test(){
        taxBox.setOpacity(5);
    }

    public void initiate(){

        Invoicer.ROOT_PANE = rootPane;

        Invoicer.customerNameLabel = customerNameLabel;
        Invoicer.customerAddressLabel = customerAddressLabel;
        Invoicer.customerAddress2Label = customerAddress2Label;
        Invoicer.vehicleDescriptionLabel = vehicleDescriptionLabel;
        Invoicer.shipToLabel = shipToLabel;
        Invoicer.pageNumberLabel = pageNumberLabel;
        Invoicer.pageCountLabel = pageCountLabel;
        Invoicer.invoiceNumberLabel = invoiceNumberLabel;
        Invoicer.customerNumberLabel = customerNumberLabel;
        Invoicer.dateLabel = dateLabel;
        Invoicer.timeLabel = timeLabel;
        Invoicer.poLabel = poLabel;
        Invoicer.counterPersonLabel = counterPersonLabel;
        Invoicer.termsLabel = termsLabel;
        Invoicer.freightTotalLabel = freightTotalLabel;
        Invoicer.subTotalLabel = subTotalLabel;
        Invoicer.salesTaxLabel = salesTaxLabel;
        Invoicer.totalLabel = totalLabel;
        Invoicer.partsForLabel = partsForLabel;

        loadChildrenIntoArray(Invoicer.mfgPartNumberArray, mfgPartNumberBox.getChildren());
        loadChildrenIntoArray(Invoicer.descriptionArray, descriptionBox.getChildren());
        loadChildrenIntoArray(Invoicer.quantityBoxArray, quantityBox.getChildren());
        loadChildrenIntoArray(Invoicer.listBoxArray, listBox.getChildren());
        loadChildrenIntoArray(Invoicer.unitBoxArray, unitBox.getChildren());
        loadChildrenIntoArray(Invoicer.coreBoxArray, coreBox.getChildren());
        loadChildrenIntoArray(Invoicer.extendedBoxArray, extendedBox.getChildren());
        loadChildrenIntoArray(Invoicer.taxBoxArray, taxBox.getChildren());
    }

    private static void loadChildrenIntoArray(Label[] labels, ObservableList<Node> children){
        int row = 0;
        for(Node node : children){
            labels[row] = (Label)node;
            row++;
        }
    }

    @Override
    public void run() {

    }

    @Override
    public void start(Stage stage) throws Exception {

    }
}
