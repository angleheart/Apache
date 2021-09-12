package Apache.console.eom;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;


public class StatementImagerController {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private Label customerNameLabel;
    @FXML
    private Label customerAddressLabel;
    @FXML
    private Label customerAddress2Label;
    @FXML
    private Label generatedLabel;
    @FXML
    private Label paidThisMonthLabel;
    @FXML
    private Label payThisAmountLabel;
    @FXML
    private Label pageNumberLabel;
    @FXML
    private Label pageCountLabel;
    @FXML
    private VBox invoiceDateBox;
    @FXML
    private VBox invoiceNumberBox;
    @FXML
    private VBox detailBox;
    @FXML
    private VBox originalAmountBox;
    @FXML
    private VBox amountAppliedBox;
    @FXML
    private VBox balanceBox;
    @FXML
    private VBox amountDueBox;
    @FXML
    private Label statementDateLabel;
    @FXML
    private Label customerNumberLabel;
    @FXML
    private Label accountTotalLabel;
    @FXML
    private Label days90Label;
    @FXML
    private Label days60Label;
    @FXML
    private Label days30Label;
    @FXML
    private Label currentLabel;



    public void init(){

        StatementImager.ROOT_PANE = rootPane;

        StatementImager.CUSTOMER_NAME_LABEL = customerNameLabel;
        StatementImager.CUSTOMER_ADDRESS_LABEL = customerAddressLabel;
        StatementImager.CUSTOMER_ADDRESS_LABEL2 = customerAddress2Label;
        StatementImager.GENERATED_LABEL = generatedLabel;
        StatementImager.PAID_THIS_MONTH_LABEL = paidThisMonthLabel;
        StatementImager.PAY_THIS_AMOUNT_LABEL = payThisAmountLabel;
        StatementImager.PAGE_NUMBER_LABEL = pageNumberLabel;
        StatementImager.PAGE_COUNT_LABEL = pageCountLabel;

        StatementImager.INVOICE_DATE_LABELS = getAsArray(invoiceDateBox.getChildren());
        StatementImager.INVOICE_NUMBER_LABELS = getAsArray(invoiceNumberBox.getChildren());
        StatementImager.DETAIL_LABELS = getAsArray(detailBox.getChildren());
        StatementImager.ORIGINAL_AMOUNT_LABELS = getAsArray(originalAmountBox.getChildren());
        StatementImager.AMOUNT_APPLIED_LABELS = getAsArray(amountAppliedBox.getChildren());
        StatementImager.BALANCE_LABELS = getAsArray(balanceBox.getChildren());
        StatementImager.AMOUNT_DUE_LABELS = getAsArray(amountDueBox.getChildren());

        StatementImager.STATEMENT_DATE_LABEL = statementDateLabel;
        StatementImager.CUSTOMER_NUMBER_LABEL = customerNumberLabel;
        StatementImager.ACCOUNT_TOTAL_LABEL = accountTotalLabel;
        StatementImager.DAYS_90_LABEL = days90Label;
        StatementImager.DAYS_60_LABEL = days60Label;
        StatementImager.DAYS_30_LABEL = days30Label;
        StatementImager.CURRENT_LABEL = currentLabel;

        System.out.println("Initiated statement imager");
    }

    private Label[] getAsArray(ObservableList<Node> children){
        Label[] labels = new Label[36];
        int row = 0;
        for(Node node : children){
            labels[row] = (Label) node;
            row++;
        }
        return labels;
    }



}
