package Apache.workstation.payments;

import Apache.database.PaymentBase;
import Apache.invoicer.Invoicer;
import Apache.util.TextFieldModifier;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import Apache.objects.Customer;
import Apache.objects.Invoice;
import Apache.objects.Payment;
import Apache.objects.PaymentLine;
import Apache.workstation.SceneController;

import java.text.SimpleDateFormat;
import java.util.*;

import static Apache.util.General.cleanDouble;

public class PaymentLineBody {

    private static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
    private static TextField[] INVOICE_ENTRIES = new TextField[8];
    private static TextField[] AMOUNT_ENTRIES = new TextField[8];
    private static Label[] DATE_LABELS = new Label[8];
    private static Label[] BALANCE_LABELS = new Label[8];
    private static GridPane MAIN_BODY;
    private static GridPane BODY_LABELS;
    private static TextField LOCK_FIELD;
    private static int focusRow;
    private static int focusCol;
    private static int focusPage;
    private static List<Invoice> invoices;
    private static Map<Integer, PaymentLine> perInvoicePayments = new HashMap<>();
    private static Map<Integer, Page> pages = new HashMap<>();

    static class Page {
        String[] invoice;
        String[] date;
        String[] balance;
        String[] amount;

        Page(
                String[] invoice,
                String[] date,
                String[] balance,
                String[] amount
        ) {
            this.invoice = invoice;
            this.date = date;
            this.balance = balance;
            this.amount = amount;
        }
    }

    static void clearPage() {
        for (TextField textField : INVOICE_ENTRIES)
            textField.clear();
        for (TextField textField : AMOUNT_ENTRIES)
            textField.clear();
        for (Label label : DATE_LABELS)
            label.setText("");
        for (Label label : BALANCE_LABELS)
            label.setText("");
        for (int i = 0; i < 8; i++) {
            BALANCE_LABELS[i].setTextFill(Color.BLACK);
        }
    }

    static void initiate(
            VBox invoice,
            VBox amount,
            VBox date,
            VBox balance,
            GridPane main,
            GridPane labels,
            TextField lock
    ) {
        ObservableList<Node> children;

        children = invoice.getChildren();
        for (int i = 0; i < 8; i++) {
            INVOICE_ENTRIES[i] = (TextField) children.get(i);
            TextFieldModifier.requireCaps(INVOICE_ENTRIES[i]);
        }

        children = amount.getChildren();
        for (int i = 0; i < 8; i++) {
            AMOUNT_ENTRIES[i] = (TextField) children.get(i);
            TextFieldModifier.requireCaps(AMOUNT_ENTRIES[i]);
        }

        children = date.getChildren();
        for (int i = 0; i < 8; i++)
            DATE_LABELS[i] = (Label) children.get(i);

        children = balance.getChildren();
        for (int i = 0; i < 8; i++)
            BALANCE_LABELS[i] = (Label) children.get(i);

        MAIN_BODY = main;
        BODY_LABELS = labels;
        focusCol = 0;
        focusPage = 0;
        focusRow = 0;
        MAIN_BODY.setOpacity(0);
        BODY_LABELS.setOpacity(0);
        LOCK_FIELD = lock;
        clearPage();
    }

    static void reset() {
        focusCol = 0;
        focusPage = 0;
        focusRow = 0;
        MAIN_BODY.setOpacity(0);
        BODY_LABELS.setOpacity(0);
        pages = new HashMap<>();
        perInvoicePayments = new LinkedHashMap<>();
        clearPage();
    }

    static void enter(List<Invoice> setInvoices) {
        invoices = setInvoices;
        MAIN_BODY.setOpacity(1);
        BODY_LABELS.setOpacity(1);
        focusRow = 0;
        focusCol = 0;
        refocus();
    }

    static void exit() {
        MAIN_BODY.setOpacity(0);
        BODY_LABELS.setOpacity(0);
        PaymentHeader.enterHeader();
    }

    private static void refocus() {
        if (focusCol == 0)
            INVOICE_ENTRIES[focusRow].requestFocus();
        else
            AMOUNT_ENTRIES[focusRow].requestFocus();
    }

    public static void reportKeyCode(KeyCode code) {
        switch (code) {
            case F12 -> {
                SceneController.setToPointOfSale();
                PaymentController.reset();
            }

            case F2 -> {
                if (focusCol != 0)
                    return;
                if (perInvoicePayments.size() == 0)
                    return;
                if (!PaymentHeader.allowRelease()) {
                    PaymentError.sendError("Your remaining amount must be 0");
                    return;
                }
                LOCK_FIELD.requestFocus();

                double total = 0;
                int releaseCode = PaymentHeader.getReleaseCode();
                String detail = PaymentHeader.getDetail();
                if (detail == null)
                    detail = "";

                for (PaymentLine pip : perInvoicePayments.values()) {
                    total += pip.getAmount();
                }

                total = Double.parseDouble(cleanDouble(total, 2));

                if (total != PaymentHeader.getTotalAmount()) {
                    PaymentError.sendError("Sorry, an error occurred try again");
                    System.out.println("Total from header: " + PaymentHeader.getTotalAmount());
                    System.out.println("Total you calculated: " + total);
                    return;
                }

                Customer customer = PaymentHeader.getCustomer();

                Payment payment = new Payment(
                        customer,
                        total,
                        releaseCode,
                        0,
                        detail,
                        new Date().getTime(),
                        new ArrayList<>(perInvoicePayments.values())
                );

                if(!PaymentBase.savePayment(payment)){
                    PaymentError.sendError("An error occurred, try again");
                    return;
                }

                Invoicer.printPayment(payment);
                PaymentHeader.reset();
                reset();
                ReceivableReporter.hide();
                PaymentHeader.focusAtTop();
            }

            case DOWN, RIGHT, ENTER -> handleForward();

            case UP, LEFT -> handleBackward();

        }
    }

    private static void handleBackward() {
        if (focusCol == 0) {
            LOCK_FIELD.requestFocus();
            if (focusRow == 0) {
                if (focusPage != 0) {
                    focusPage--;
                    loadPage();
                    focusRow = 7;
                } else {
                    exit();
                    return;
                }
            } else {
                focusRow--;
            }
            focusCol = 1;
            PaymentError.clear();

            int invoiceNumber = Integer.parseInt(INVOICE_ENTRIES[focusRow].getText().substring(2));
            perInvoicePayments.remove(invoiceNumber);
            double amountRollback = Double.parseDouble(AMOUNT_ENTRIES[focusRow].getText());
            PaymentHeader.postUpdateToRemaining(amountRollback, true);
            double oldBalance = Double.parseDouble(BALANCE_LABELS[focusRow].getText());
            double newBalance = oldBalance + amountRollback;
            BALANCE_LABELS[focusRow].setText(cleanDouble(newBalance, 2));
            BALANCE_LABELS[focusRow].setTextFill(Color.BLACK);
            AMOUNT_ENTRIES[focusRow].setText("");
            AMOUNT_ENTRIES[focusRow].requestFocus();
        } else {
            LOCK_FIELD.requestFocus();
            focusCol = 0;
            PaymentError.clear();

            AMOUNT_ENTRIES[focusRow].setText("");
            BALANCE_LABELS[focusRow].setText("");
            DATE_LABELS[focusRow].setText("");
            INVOICE_ENTRIES[focusRow].setText("");
            INVOICE_ENTRIES[focusRow].requestFocus();
        }
    }

    private static void handleForward() {
        if (focusCol == 0) {
            LOCK_FIELD.requestFocus();
            String invoiceNumber = INVOICE_ENTRIES[focusRow].getText().trim();
            if (invoiceNumber.equalsIgnoreCase("END")) {
                SceneController.setToPointOfSale();
                PaymentController.reset();
                return;
            }

            Invoice toPost;

            if (invoiceNumber.equals("")) {
                toPost = getNextInvoice();
                if (toPost == null) {
                    INVOICE_ENTRIES[focusRow].clear();
                    INVOICE_ENTRIES[focusRow].requestFocus();
                    PaymentError.sendError("No more invoices left");
                    return;
                }
            } else {
                toPost = getInvoiceByRequest(invoiceNumber);
                if (toPost == null) {
                    INVOICE_ENTRIES[focusRow].clear();
                    INVOICE_ENTRIES[focusRow].requestFocus();
                    PaymentError.sendError("Invalid invoice number given");
                    return;
                }
            }
            PaymentError.clear();
            INVOICE_ENTRIES[focusRow].setText("D-" + toPost.getInvoiceNumber());
            DATE_LABELS[focusRow].setText(sdf.format(toPost.getDate()));
            BALANCE_LABELS[focusRow].setText(cleanDouble(toPost.getBalance(), 2));
            AMOUNT_ENTRIES[focusRow].setText(cleanDouble(toPost.getBalance(), 2));
            focusCol++;
            refocus();
        } else {
            LOCK_FIELD.requestFocus();
            String amountStr = AMOUNT_ENTRIES[focusRow].getText().trim();
            if (amountStr.equals(""))
                AMOUNT_ENTRIES[focusRow].setText(BALANCE_LABELS[focusRow].getText());
            amountStr = AMOUNT_ENTRIES[focusRow].getText().trim();
            double amount;
            try {
                amount = Double.parseDouble(amountStr);
                if (amount == 0)
                    throw new Exception();
            } catch (Exception e) {
                PaymentError.sendError("Invalid amount");
                AMOUNT_ENTRIES[focusRow].clear();
                AMOUNT_ENTRIES[focusRow].requestFocus();
                return;
            }

            PaymentError.clear();
            AMOUNT_ENTRIES[focusRow].setText(cleanDouble(amount, 2));
            PaymentHeader.postUpdateToRemaining(amount, false);


            int invoiceNum = Integer.parseInt(INVOICE_ENTRIES[focusRow].getText().substring(2));
            double roundedAmount = Double.parseDouble(AMOUNT_ENTRIES[focusRow].getText());
            double originalBalance = Double.parseDouble(BALANCE_LABELS[focusRow].getText());

            perInvoicePayments.put(
                    invoiceNum,
                    new PaymentLine(
                            invoiceNum,
                            roundedAmount,
                            originalBalance
                    )
            );

            double newBalance = originalBalance - amount;
            BALANCE_LABELS[focusRow].setText(cleanDouble(newBalance, 2));
            if (BALANCE_LABELS[focusRow].getText().equals("0.00"))
                BALANCE_LABELS[focusRow].setTextFill(Color.GREEN);
            else
                BALANCE_LABELS[focusRow].setTextFill(Color.RED);
            nextLine();
        }
    }

    private static void nextLine() {
        focusRow++;
        focusCol = 0;
        if (focusRow == 8) {
            savePage();
            clearPage();
            focusPage++;
            focusRow = 0;
        }
        refocus();
    }

    private static Invoice getNextInvoice() {
        for (int i = 0; i < invoices.size(); i++) {
            Invoice next = invoices.get(i);
            if (!perInvoicePayments.containsKey(next.getInvoiceNumber()))
                return next;
        }
        return null;
    }

    private static Invoice getInvoiceByRequest(String request) {
        int invoiceNumber;
        try {
            invoiceNumber = Integer.parseInt(request);
        } catch (Exception e) {
            return null;
        }
        for (int i = 0; i < invoices.size(); i++) {
            int match = invoices.get(i).getInvoiceNumber();

            if (
                    match == invoiceNumber &&
                            !perInvoicePayments.containsKey(match)
            ) return invoices.get(i);
        }
        return null;
    }

    private static void loadPage() {
        Page page = pages.get(focusPage);
        for (int i = 0; i < 8; i++) {
            INVOICE_ENTRIES[i].setText(page.invoice[i]);
            DATE_LABELS[i].setText(page.date[i]);
            BALANCE_LABELS[i].setText(page.balance[i]);
            if (Double.parseDouble(page.balance[i]) == 0)
                BALANCE_LABELS[i].setTextFill(Color.GREEN);
            else
                BALANCE_LABELS[i].setTextFill(Color.RED);

            AMOUNT_ENTRIES[i].setText(page.amount[i]);
        }
    }

    private static void savePage() {
        Page page = new Page(
                Arrays.stream(INVOICE_ENTRIES).map(TextInputControl::getText).toArray(String[]::new),
                Arrays.stream(DATE_LABELS).map(Labeled::getText).toArray(String[]::new),
                Arrays.stream(BALANCE_LABELS).map(Labeled::getText).toArray(String[]::new),
                Arrays.stream(AMOUNT_ENTRIES).map(TextInputControl::getText).toArray(String[]::new)
        );

        pages.put(focusPage, page);
    }
}
