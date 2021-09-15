package Apache.workstation.payments;

import Apache.config.Config;
import Apache.http.Gateway;
import Apache.objects.Customer;
import Apache.database.CustomerBase;
import Apache.objects.Invoice;
import Apache.workstation.SceneController;
import Apache.util.InputVerifier;
import Apache.util.TextFieldModifier;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import java.util.List;

import static Apache.util.General.cleanDouble;

public class PaymentHeader {

    private static TextField CUSTOMER_FIELD;
    private static TextField METHOD_FIELD;
    private static TextField DETAIL_FIELD;
    private static TextField AMOUNT_FIELD;
    private static Label REMAINING_LABEL;
    private static VBox DETAIL_AMOUNT;
    private static VBox REMAINING;
    private static Label METHOD_LABEL;
    private static Label AMOUNT_LABEL;
    private static Customer customer;
    private static int position;
    private static List<Invoice> invoicesToDisplay;

    public static void initiate(
            TextField customer,
            TextField method,
            TextField detail,
            TextField amount,
            Label remaining,
            VBox detailAmount,
            VBox remainingBox,
            Label methodLabel,
            Label amountLabel
    ) {
        CUSTOMER_FIELD = customer;
        METHOD_FIELD = method;
        DETAIL_FIELD = detail;
        AMOUNT_FIELD = amount;
        REMAINING_LABEL = remaining;
        DETAIL_AMOUNT = detailAmount;
        REMAINING = remainingBox;
        METHOD_LABEL = methodLabel;
        AMOUNT_LABEL = amountLabel;

        TextFieldModifier.requireCaps(CUSTOMER_FIELD);
        TextFieldModifier.requireCaps(METHOD_FIELD);
        TextFieldModifier.requireCaps(DETAIL_FIELD);
        TextFieldModifier.requireCaps(AMOUNT_FIELD);
    }

    public static void reset() {
        ReceivableReporter.hide();
        customer = null;
        position = 0;
        CUSTOMER_FIELD.setText("");
        METHOD_FIELD.setText("");
        DETAIL_FIELD.setText("");
        AMOUNT_FIELD.setText("");
        DETAIL_AMOUNT.setOpacity(0);
        REMAINING.setOpacity(0);
        METHOD_LABEL.setOpacity(0);
        METHOD_FIELD.setOpacity(0);
        AMOUNT_LABEL.setOpacity(0);
        AMOUNT_FIELD.setOpacity(0);
        CUSTOMER_FIELD.requestFocus();
        REMAINING_LABEL.setStyle("");
    }

    static void reportKeyCode(KeyCode keyCode) {
        if (keyCode == KeyCode.F12) {
            SceneController.setToPointOfSale();
            PaymentController.reset();
            return;
        }

        if (position == 1 && keyCode == KeyCode.RIGHT)
            keyCode = KeyCode.ENTER;

        if (position == 2 && keyCode == KeyCode.LEFT)
            keyCode = KeyCode.UP;

        switch (keyCode) {
            case UP -> handleUp();
            case DOWN, ENTER -> handleEnter();

        }
    }

    private static void handleUp() {
        switch (position) {
            case 0 -> {
                CUSTOMER_FIELD.clear();
                CUSTOMER_FIELD.requestFocus();
                ReceivableReporter.hide();
                PaymentError.clear();
            }

            case 1 -> {
                position--;
                CUSTOMER_FIELD.requestFocus();
                METHOD_LABEL.setOpacity(0);
                METHOD_FIELD.setOpacity(0);
                METHOD_FIELD.clear();
                ReceivableReporter.hide();
                PaymentError.clear();
            }

            case 2 -> {
                position--;
                METHOD_FIELD.requestFocus();
                DETAIL_AMOUNT.setOpacity(0);
                DETAIL_FIELD.clear();
            }

            case 3 -> {
                position--;
                DETAIL_FIELD.requestFocus();
                AMOUNT_FIELD.clear();
                AMOUNT_FIELD.setOpacity(0);
                AMOUNT_LABEL.setOpacity(0);
                REMAINING.setOpacity(0);
            }

        }
    }

    static void focusAtTop() {
        CUSTOMER_FIELD.requestFocus();
    }

    private static void handleEnter() {
        switch (position) {
            case 0 -> {
                String customerNumber = CUSTOMER_FIELD.getText();
                if (customerNumber.equalsIgnoreCase("END")) {
                    SceneController.setToPointOfSale();
                    PaymentController.reset();
                    return;
                }

                if (!InputVerifier.verifyCustomerRequest(customerNumber)) {
                    PaymentError.sendError("Invalid customer request");
                    CUSTOMER_FIELD.setText("");
                    CUSTOMER_FIELD.requestFocus();
                    return;
                }

                if (Config.STAND_ALONE) {
                    customer = CustomerBase.getCustomerByNumber(customerNumber);
                    if (customer == null) {
                        ReceivableReporter.hide();
                        PaymentError.sendError("Customer not found");
                        CUSTOMER_FIELD.clear();
                        CUSTOMER_FIELD.requestFocus();
                    }
                } else {
                    if (customerNumber.equals(""))
                        customerNumber = "*";
                    List<Customer> customers = Gateway.queryCustomers(customerNumber);
                    if (customers == null) {
                        PaymentError.sendError("Sorry, a server error occurred");
                        CUSTOMER_FIELD.clear();
                        return;
                    }
                    if (customers.size() == 0) {
                        PaymentError.sendError("Customer not found");
                        CUSTOMER_FIELD.clear();
                        return;
                    }
                    PaymentError.clear();
                    if (customers.size() == 1) {
                        customer = (Customer)customers.get(0);
                        CUSTOMER_FIELD.setText(customer.getNumber());
                        afterCustomerSelected();
                        return;
                    }

                    //PaymentSelectionBox.performCustomerRequest(customers);
                }
            }

            case 1 -> {
                String method = METHOD_FIELD.getText();
                if (method.equalsIgnoreCase("END")) {
                    SceneController.setToPointOfSale();
                    PaymentController.reset();
                    return;
                }

                switch (method) {
                    case "11", "CASH" -> {
                        PaymentError.clear();
                        position++;
                        DETAIL_AMOUNT.setOpacity(1);
                        DETAIL_FIELD.requestFocus();
                        METHOD_FIELD.setText("CASH");
                    }

                    case "12", "CHECK" -> {
                        PaymentError.clear();
                        position++;
                        DETAIL_AMOUNT.setOpacity(1);
                        DETAIL_FIELD.requestFocus();
                        METHOD_FIELD.setText("CHECK");
                    }

                    case "13", "PLASTIC", "VISA", "MASTERCARD", "AMEX", "DISCOVER" -> {
                        PaymentError.clear();
                        position++;
                        DETAIL_AMOUNT.setOpacity(1);
                        DETAIL_FIELD.requestFocus();
                        METHOD_FIELD.setText("PLASTIC");
                    }

                    default -> {
                        PaymentError.sendError("Invalid Payment Method");
                        METHOD_FIELD.clear();
                        METHOD_FIELD.requestFocus();
                    }
                }
            }

            case 2 -> {
                String detail = DETAIL_FIELD.getText();
                if (detail.equalsIgnoreCase("END")) {
                    SceneController.setToPointOfSale();
                    PaymentController.reset();
                    return;
                }

                if (!InputVerifier.verifyDocumentDetail(detail)) {
                    PaymentError.sendError("Invalid Document Detail");
                    DETAIL_FIELD.clear();
                    DETAIL_FIELD.requestFocus();
                    return;
                }
                PaymentError.clear();
                position++;
                AMOUNT_LABEL.setOpacity(1);
                AMOUNT_FIELD.setOpacity(1);
                AMOUNT_FIELD.requestFocus();
            }

            case 3 -> {
                String amount = AMOUNT_FIELD.getText();
                if(amount.trim().equals(""))
                    amount = "0";

                if (amount.equalsIgnoreCase("END")) {
                    SceneController.setToPointOfSale();
                    PaymentController.reset();
                    return;
                }

                if (!InputVerifier.verifyPaymentAmount(amount)) {
                    PaymentError.sendError("Invalid Payment Amount");
                    AMOUNT_FIELD.clear();
                    AMOUNT_FIELD.requestFocus();
                    return;
                }
                String cleanedAmount = cleanDouble(Double.parseDouble(amount), 2);
                REMAINING_LABEL.setText(cleanedAmount);
                REMAINING.setOpacity(1);
                PaymentLineBody.enter(invoicesToDisplay);
                AMOUNT_FIELD.setText(cleanedAmount);
            }
        }
    }

    private static void afterCustomerSelected() {
        invoicesToDisplay = Gateway.getPayableInvoices(customer.getNumber());

        if (invoicesToDisplay == null) {
            CUSTOMER_FIELD.clear();
            CUSTOMER_FIELD.requestFocus();
            PaymentError.sendError("Failed to fetch payable invoices");
            return;
        }

        ReceivableReporter.post(customer);

        if (invoicesToDisplay.size() == 0) {
            CUSTOMER_FIELD.clear();
            CUSTOMER_FIELD.requestFocus();
            PaymentError.sendError("This customer does not have any payable invoices");
            return;
        }
        position++;
        METHOD_FIELD.requestFocus();
        METHOD_LABEL.setOpacity(1);
        METHOD_FIELD.setOpacity(1);
    }

    static int getReleaseCode() {
        switch (METHOD_FIELD.getText()) {
            case "CASH" -> {
                return 11;
            }
            case "CHECK" -> {
                return 12;
            }
            case "PLASTIC" -> {
                return 13;
            }
        }
        return 11;
    }

    static String getDetail() {
        return DETAIL_FIELD.getText();
    }

    static double getTotalAmount() {
        return Double.parseDouble(AMOUNT_FIELD.getText());
    }

    static Customer getCustomer() {
        return customer;
    }

    static void enterHeader() {
        AMOUNT_FIELD.requestFocus();
    }

    static boolean allowRelease() {
        return Double.parseDouble(REMAINING_LABEL.getText()) == 0;
    }

    static void postUpdateToRemaining(double amount, boolean rollback) {
        double rem = Double.parseDouble(REMAINING_LABEL.getText());
        if (rollback)
            rem += amount;
        else
            rem -= amount;

        REMAINING_LABEL.setText(cleanDouble(rem, 2));
        if (rem == 0)
            REMAINING_LABEL.setStyle("-fx-background-color: green");
        else if (rem > 0)
            REMAINING_LABEL.setStyle("-fx-background-color: white");
        else
            REMAINING_LABEL.setStyle("-fx-background-color: red");
    }

    static void postCustomer(Customer postCustomer) {
        customer = postCustomer;
        CUSTOMER_FIELD.setText(customer.getNumber());
        afterCustomerSelected();
    }


}
