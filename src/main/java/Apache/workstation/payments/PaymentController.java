package Apache.workstation.payments;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class PaymentController {

    @FXML
    private TextField customerField;
    @FXML
    private TextField methodField;
    @FXML
    private TextField detailField;
    @FXML
    private TextField amountField;
    @FXML
    private VBox invoiceNumberBox;
    @FXML
    private VBox dateBox;
    @FXML
    private VBox balanceBox;
    @FXML
    private VBox appliedBox;
    @FXML
    private VBox remainingBox;
    @FXML
    private Label remainingLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private Label methodLabel;
    @FXML
    private Label amountLabel;
    @FXML
    private VBox detailAmountBox;
    @FXML
    private GridPane bodyMain;
    @FXML
    private GridPane bodyLabels;
    @FXML
    private TextField lockField;
    @FXML
    private VBox custRecBox;
    @FXML
    private Label custNameLabel;
    @FXML
    private Label p4Label;
    @FXML
    private Label p3Label;
    @FXML
    private Label p2Label;
    @FXML
    private Label p1Label;
    @FXML
    private Label totalLabel;
    @FXML
    private VBox custSelectBox;
    @FXML
    private TextField custSelectLock;

    public void initiate(){
        PaymentHeader.initiate(
                customerField,
                methodField,
                detailField,
                amountField,
                remainingLabel,
                detailAmountBox,
                remainingBox,
                methodLabel,
                amountLabel
        );

        PaymentLineBody.initiate(
                invoiceNumberBox,
                appliedBox,
                dateBox,
                balanceBox,
                bodyMain,
                bodyLabels,
                lockField
        );

        ReceivableReporter.initiate(
                custRecBox,
                custNameLabel,
                p1Label,
                p2Label,
                p3Label,
                p4Label,
                totalLabel
        );

        PaymentSelectionBox.initiate(
                custSelectBox,
                custSelectLock
        );

        PaymentError.initiate(errorLabel);
        reset();
    }

    public void headerKeyPress(KeyEvent event){
        PaymentHeader.reportKeyCode(event.getCode());
    }

    public void bodyKeyPress(KeyEvent event){
        PaymentLineBody.reportKeyCode(event.getCode());
    }

    public void selectorKeyPress(KeyEvent keyEvent){
        PaymentSelectionBox.reportKeyCode(keyEvent.getCode());
    }

    public static void reset(){
        PaymentHeader.reset();
        PaymentLineBody.reset();
        PaymentError.clear();
        ReceivableReporter.hide();
    }

}
