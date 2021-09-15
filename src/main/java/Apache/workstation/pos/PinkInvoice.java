package Apache.workstation.pos;

import Apache.http.Gateway;
import Apache.objects.Invoice;
import Apache.objects.InvoiceLine;
import Apache.invoicer.Invoicer;
import Apache.util.TextFieldModifier;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.text.SimpleDateFormat;

import static Apache.util.General.cleanDouble;

public class PinkInvoice {

    private static AnchorPane PINK_PANE;

    private static Label INV_NUMBER_LABEL;
    private static Label CUSTOMER_NAME_LABEL;
    private static Label DATE_LABEL;
    private static Label VEHICLE_LABEL;
    private static Label SHIP_TO_LABEL;
    private static Label TOTAL_LABEL;
    private static Label TERMS_LABEL;

    private final static Label[] QTY = new Label[10];
    private final static Label[] MFG_PART = new Label[10];
    private final static Label[] UNIT = new Label[10];
    private final static Label[] EXT = new Label[10];
    private final static Label[] TX = new Label[10];

    private static TextField INPUT_FIELD;
    private static boolean openedFromSelectionMenu;
    private static int pageNumber;
    private static Invoice loadedInvoice;

    static void initiate(
            AnchorPane pinkPane,
            Label invoiceNum,
            Label custName,
            Label date,
            Label terms,
            Label vehicle,
            Label shipTo,
            Label total,
            VBox qty,
            VBox mfgPart,
            VBox unit,
            VBox ext,
            VBox tx,
            TextField input
    ) {
        PINK_PANE = pinkPane;
        INV_NUMBER_LABEL = invoiceNum;
        CUSTOMER_NAME_LABEL = custName;
        DATE_LABEL = date;
        TERMS_LABEL = terms;
        VEHICLE_LABEL = vehicle;
        SHIP_TO_LABEL = shipTo;
        TOTAL_LABEL = total;
        INPUT_FIELD = input;

        initiateArray(qty.getChildren(), QTY);
        initiateArray(mfgPart.getChildren(), MFG_PART);
        initiateArray(unit.getChildren(), UNIT);
        initiateArray(ext.getChildren(), EXT);
        initiateArray(tx.getChildren(), TX);

        TextFieldModifier.requireCaps(input);
    }

    static void setOpenedFromSelectionMenu(boolean b){
        openedFromSelectionMenu = b;
    }

    private static void initiateArray(
            ObservableList<Node> children,
            Label[] array
    ) {
        for (int i = 0; i < 10; i++) {
            array[i] = (Label) children.get(i);
        }
    }


    static void loadInvoice(Invoice invoice) {
        pageNumber = 0;
        clearConsistent();
        clearPage();

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy hh:mm a");
        loadedInvoice = invoice;

        INV_NUMBER_LABEL.setText("D" + invoice.getInvoiceNumber());
        CUSTOMER_NAME_LABEL.setText(invoice.getCustomer().getName());
        DATE_LABEL.setText(sdf.format(invoice.getDate()));

        String addOn = " CLOSED";
        TERMS_LABEL.setTextFill(Color.RED);
        if (invoice.getAccountingPeriod() == 0) {
            TERMS_LABEL.setTextFill(Color.GREEN);
            addOn = " OPEN";
        }

        switch (invoice.getReleaseCode()) {
            case 31 -> {
                if (invoice.getTransCode().contains("SAL"))
                    TERMS_LABEL.setText("SALE-CHARGE");
                else
                    TERMS_LABEL.setText("RETURN-CHARGE");
            }

            case 11 -> {
                if (invoice.getTransCode().contains("SAL"))
                    TERMS_LABEL.setText("SALE-CASH");
                else
                    TERMS_LABEL.setText("RETURN-CASH");
            }

            case 13 -> {
                if (invoice.getTransCode().contains("SAL"))
                    TERMS_LABEL.setText("SALE-CREDIT CARD");
                else
                    TERMS_LABEL.setText("RET-CREDIT CARD");
            }

            case 12 -> {
                if (invoice.getTransCode().contains("SAL"))
                    TERMS_LABEL.setText("SALE-CHECK");
                else
                    TERMS_LABEL.setText("RETURN-CHECK");
            }
        }

        TERMS_LABEL.setText(TERMS_LABEL.getText() + addOn);

        VEHICLE_LABEL.setText(invoice.getVehicleDescription());
        SHIP_TO_LABEL.setText(invoice.getShipTo());
        TOTAL_LABEL.setText("TOTAL: $" + cleanDouble(invoice.getTotals().getGrandTotal(), 2));
        reloadPage();
    }

    static void startDisplay(boolean selectionMenu) {
        openedFromSelectionMenu = selectionMenu;
        INPUT_FIELD.requestFocus();
        PINK_PANE.setOpacity(1);
    }

    private static void endDisplay() {
        if (LineBody.isFocusedHere())
            LineBody.reFocus();
        else
            Header.refocus();
        PINK_PANE.setOpacity(0);
    }

    private static void reloadPage() {
        clearPage();
        for (int i = pageNumber * 10; i < (pageNumber * 10 + 10); i++) {
            if (i >= loadedInvoice.getLines().size())
                break;
            InvoiceLine line = loadedInvoice.getLines().get(i);
            QTY[i - (pageNumber * 10)].setText(Integer.toString(line.getQty()));
            MFG_PART[i - (pageNumber * 10)].setText(line.getMfg() + " " + line.getPartNumber());
            UNIT[i - (pageNumber * 10)].setText(cleanDouble(line.getUnitPrice(), 3));
            EXT[i - (pageNumber * 10)].setText(cleanDouble(line.getUnitPrice(), 2));
            TX[i - (pageNumber * 10)].setText(line.getTx());
        }
    }

    private static void clearPage() {
        for (int i = 0; i < 10; i++) {
            QTY[i].setText("");
            MFG_PART[i].setText("");
            UNIT[i].setText("");
            EXT[i].setText("");
            TX[i].setText("");
        }
    }

    private static void clearConsistent() {
        INV_NUMBER_LABEL.setText("");
        CUSTOMER_NAME_LABEL.setText("");
        DATE_LABEL.setText("");
        TERMS_LABEL.setText("");
        VEHICLE_LABEL.setText("");
        SHIP_TO_LABEL.setText("");
        TOTAL_LABEL.setText("");
        INPUT_FIELD.clear();
    }

    static void reportKeyPress(KeyCode keyCode) {

        switch (keyCode) {

            case ESCAPE, F12 -> {
                if (!openedFromSelectionMenu) {
                    endDisplay();
                    return;
                }
                PINK_PANE.setOpacity(0);
                SelectionMenu.performRequest(SelectionMenuType.INVOICE_VIEW_CALLBACK, null);
            }

            case UP -> {
                if (pageNumber == 0)
                    return;
                pageNumber--;
                reloadPage();
            }

            case DOWN -> {
                if (loadedInvoice.getLines().size() < pageNumber * 10 + 11)
                    return;
                pageNumber++;
                reloadPage();
            }

            case ENTER -> {
                CUSTOMER_NAME_LABEL.requestFocus();
                switch (INPUT_FIELD.getText()) {

                    case "VOID" -> {
                        Error.clear();

                        if (loadedInvoice.getAccountingPeriod() != 0) {
                            Error.send("Invoice is not voidable");
                            INPUT_FIELD.clear();
                            INPUT_FIELD.requestFocus();
                            return;
                        }
                        if(!Gateway.voidInvoice(loadedInvoice.getInvoiceNumber())){
                            Error.send("A server error occurred");
                            return;
                        }
                        Error.sendSuccess("Invoice voided");
                        endDisplay();
                    }

                    case "REPRINT", "RP" -> {

                        if (!Invoicer.printInvoice(loadedInvoice)) {
                            Error.send("Failed to reprint invoice");
                            INPUT_FIELD.clear();
                            INPUT_FIELD.requestFocus();
                            return;
                        }

                        Error.clear();
                        endDisplay();
                    }

                    case "1.3 CASH", "1.3 11", "11" -> tryReleaseUpdate(11);
                    case "1.3 CHECK", "1.3 12", "12" -> tryReleaseUpdate(12);
                    case "1.3 PLASTIC",
                            "1.3 VISA",
                            "1.3 MASTERCARD",
                            "1.3 DISCOVER",
                            "1.3 AMEX",
                            "1.3 13", "13" -> tryReleaseUpdate(13);
                    case "1.3 CHARGE", "1.3 31", "31" -> tryReleaseUpdate(31);

                    case "END", "BACK", "CANCEL", "EXIT" -> {
                        Error.clear();
                        if (!openedFromSelectionMenu) {
                            endDisplay();
                            return;
                        }

                        PINK_PANE.setOpacity(0);
                        SelectionMenu.performRequest(SelectionMenuType.INVOICE_VIEW_CALLBACK, null);
                    }

                    default -> {
                        Error.send("Unrecognized Request");
                        INPUT_FIELD.clear();
                        INPUT_FIELD.requestFocus();
                    }
                }
            }

        }


    }


    private static void tryReleaseUpdate(int code) {
        Error.clear();
        if (loadedInvoice.getAccountingPeriod() != 0) {
            Error.send("Modification not allowed");
            INPUT_FIELD.clear();
            INPUT_FIELD.requestFocus();
            return;
        }

        if (Gateway.setInvoiceReleaseCode(loadedInvoice.getInvoiceNumber(), code)) {
            Error.send("Failed to update invoice");
            INPUT_FIELD.clear();
            INPUT_FIELD.requestFocus();
            return;
        }
        Error.clear();
        Invoice newInvoice = Gateway.getInvoiceByNumber(loadedInvoice.getInvoiceNumber());
        if (newInvoice == null) {
            Error.send("Database error occurred");
            return;
        }

        Error.sendSuccess("Invoice release code updated");
        loadInvoice(newInvoice);
        INPUT_FIELD.requestFocus();
    }


}