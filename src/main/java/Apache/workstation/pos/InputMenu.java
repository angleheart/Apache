package Apache.workstation.pos;

import Apache.config.Config;
import Apache.http.Gateway;
import Apache.objects.Invoice;
import Apache.invoicer.Invoicer;
import Apache.util.General;
import Apache.util.InputRefiner;
import Apache.util.InputVerifier;
import Apache.util.TextFieldModifier;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

public class InputMenu {

    private static InputMenuType INPUT_MENU_TYPE;
    private static AnchorPane INPUT_MENU_PANE;
    private static Label INPUT_MENU_TITLE;
    private static TextField INPUT_MENU_FIELD;
    private static Label INPUT_MENU_HELP_LABEL;

    static void initiate(
            AnchorPane inputMenuPane,
            Label inputMenuLabel,
            TextField inputMenuField,
            Label inputMenuHelpLabel) {
        INPUT_MENU_PANE = inputMenuPane;
        INPUT_MENU_TITLE = inputMenuLabel;
        INPUT_MENU_FIELD = inputMenuField;
        INPUT_MENU_HELP_LABEL = inputMenuHelpLabel;

        TextFieldModifier.requireCaps(INPUT_MENU_FIELD);
    }

    public static void performRequest(InputMenuType inputMenuType) {
        Error.clear();
        reset();
        INPUT_MENU_TYPE = inputMenuType;
        switch (INPUT_MENU_TYPE) {
            case CORE -> {
                INPUT_MENU_TITLE.setText("Enter Core Price");
            }
            case SEQUENCE -> {
                INPUT_MENU_TITLE.setText("Enter Sequence Name");
                INPUT_MENU_FIELD.setText(ActiveSequence.SAVE_NAME);
            }
            case VEHICLE -> {
                INPUT_MENU_TITLE.setText("Enter Vehicle Description");
            }
            case SHIP_TO -> {
                INPUT_MENU_TITLE.setText("Enter Who To Deliver To");
            }
            case COST -> {
                INPUT_MENU_TITLE.setText("Enter Your Cost");
            }
            case FREIGHT -> {
                INPUT_MENU_TITLE.setText("Set Invoice Freight Amount");
            }
            case REPRINT -> {
                INPUT_MENU_TITLE.setText("Enter Invoice To Reprint");
            }
            case RELEASE_CODE -> {
                INPUT_MENU_TITLE.setText("Enter Release Code");
                INPUT_MENU_HELP_LABEL.setText("31-CHARGE   11-CASH   12-CHECK   13-PLASTIC");
            }
            case INVOICE_VIEW -> {
                INPUT_MENU_TITLE.setText("Enter Invoice # To View");
            }
        }

        INPUT_MENU_FIELD.requestFocus();
        INPUT_MENU_PANE.setOpacity(1);
    }

    static void reportKeyCode(KeyCode keyCode) {

        if(keyCode == KeyCode.ENTER && INPUT_MENU_FIELD.getText().equalsIgnoreCase("END")){
            if (LineBody.isFocusedHere())
                LineBody.reFocus();
            else
                Header.refocus();
            vanish();
            return;
        }

        switch (INPUT_MENU_TYPE) {

            case CORE -> {
                if (keyCode == KeyCode.ENTER) {
                    LineBody.applyCore(INPUT_MENU_FIELD.getText());
                    vanish();
                } else if (keyCode == KeyCode.ESCAPE || keyCode == KeyCode.F12) {
                    LineBody.reFocus();
                    vanish();
                }
            }

            case SEQUENCE -> {
                if (keyCode == KeyCode.ENTER) {
                    String saveName = INPUT_MENU_FIELD.getText();
                    if (InputVerifier.verifyDescription(saveName) && saveName.trim().length() > 0) {
                        Error.clear();
                        ActiveSequence.hold(saveName);
                        INPUT_MENU_PANE.setOpacity(0);
                        reset();
                    } else {
                        if (LineBody.isFocusedHere())
                            LineBody.reFocus();
                        else
                            Header.refocus();
                        vanish();
                        Error.send("Invalid Sequence Name");
                    }
                } else if (keyCode == KeyCode.ESCAPE || keyCode == KeyCode.F12) {
                    if (LineBody.isFocusedHere())
                        LineBody.reFocus();
                    else
                        Header.refocus();
                    vanish();
                }
            }

            case VEHICLE -> {
                if (keyCode == KeyCode.ENTER) {
                    Header.applyVehicleDescription(INPUT_MENU_FIELD.getText());
                    if (LineBody.isFocusedHere())
                        LineBody.reFocus();
                    else
                        Header.refocus();
                    vanish();
                } else if (keyCode == KeyCode.ESCAPE || keyCode == KeyCode.F12) {
                    if (LineBody.isFocusedHere())
                        LineBody.reFocus();
                    else
                        Header.refocus();
                    vanish();
                }
            }

            case SHIP_TO -> {
                if (keyCode == KeyCode.ENTER) {
                    Header.applyShipTo(INPUT_MENU_FIELD.getText());
                    if (LineBody.isFocusedHere())
                        LineBody.reFocus();
                    else
                        Header.refocus();
                    vanish();
                } else if (keyCode == KeyCode.ESCAPE || keyCode == KeyCode.F12) {
                    if (LineBody.isFocusedHere())
                        LineBody.reFocus();
                    else
                        Header.refocus();
                    vanish();
                }
            }


            case COST -> {
                if (keyCode == KeyCode.ENTER) {
                    String costString = INPUT_MENU_FIELD.getText();
                    if (!InputVerifier.verifyPrice(costString)) {
                        Error.send("Invalid cost specified");
                        vanish();
                        LineBody.reFocus();
                        return;
                    }
                    Error.clear();
                    vanish();
                    SelectionMenu.performRequest(SelectionMenuType.CALCULATOR,
                            General.getCalculatorOptions(Double.parseDouble(costString)));
                } else if (keyCode == KeyCode.ESCAPE || keyCode == KeyCode.F12) {
                    LineBody.reFocus();
                    vanish();
                }
            }

            case FREIGHT -> {
                if (keyCode == KeyCode.ENTER) {
                    String freight = INPUT_MENU_FIELD.getText();
                    if (!InputVerifier.verifyPrice(freight) || Double.parseDouble(freight) < 0) {
                        Error.send("Invalid freight amount");
                    } else {
                        Error.clear();
                        ActiveSequence.FREIGHT_TOTAL = Double.parseDouble(freight);
                        Totals.reload();
                    }
                    vanish();
                    if (LineBody.isFocusedHere())
                        LineBody.reFocus();
                    else
                        Header.refocus();
                } else if (keyCode == KeyCode.ESCAPE || keyCode == KeyCode.F12) {
                    if (LineBody.isFocusedHere())
                        LineBody.reFocus();
                    else
                        Header.refocus();
                    vanish();
                }
            }

            case REPRINT -> {
                if (keyCode == KeyCode.ENTER) {
                    String invoiceNum = INPUT_MENU_FIELD.getText();
                    if (!InputVerifier.verifyAllInvoiceSearch(invoiceNum)) {
                        Error.send("Invalid invoice number");
                    } else {
                        Error.clear();
                        Invoice invoice = Gateway.getInvoiceByNumber(InputRefiner.cleanInvoiceNumber(invoiceNum));
                        if (invoice == null) {
                            Error.send("Invoice not found");
                        } else {
                            if (!Invoicer.printInvoice(invoice)) {
                                Error.send("Failed to print invoice");
                            }
                        }
                        vanish();
                        if (LineBody.isFocusedHere())
                            LineBody.reFocus();
                        else
                            Header.refocus();
                    }
                } else if (keyCode == KeyCode.ESCAPE || keyCode == KeyCode.F12) {
                    if (LineBody.isFocusedHere())
                        LineBody.reFocus();
                    else
                        Header.refocus();
                    vanish();
                }
            }

            case RELEASE_CODE -> {
                if (keyCode == KeyCode.ENTER) {
                    String releaseCode = INPUT_MENU_FIELD.getText();
                    if (!InputVerifier.verifyReleaseCode(releaseCode)) {
                        Error.send("Invalid release code");

                    } else {
                        Error.clear();
                        vanish();
                        ActiveSequence.release(Integer.parseInt(releaseCode));
                    }
                } else if (keyCode == KeyCode.ESCAPE || keyCode == KeyCode.F12) {
                    if (LineBody.isFocusedHere())
                        LineBody.reFocus();
                    else
                        Header.refocus();
                    vanish();
                }
            }

            case INVOICE_VIEW -> {
                if (keyCode == KeyCode.ENTER) {

                    String invoiceNumber = INPUT_MENU_FIELD.getText();
                    if(!InputVerifier.verifyInvoiceNumber(invoiceNumber)){
                        Error.send("Invalid invoice number");
                        INPUT_MENU_FIELD.clear();
                        INPUT_MENU_FIELD.requestFocus();
                    }

                    Invoice invoice = Gateway.getInvoiceByNumber(
                            InputRefiner.cleanInvoiceNumber(invoiceNumber)
                    );

                    if (invoice == null) {
                        Error.send("Failed to open invoice");
                        INPUT_MENU_FIELD.clear();
                        INPUT_MENU_FIELD.requestFocus();
                        return;
                    }

                    vanish();
                    PinkInvoice.loadInvoice(invoice);
                    PinkInvoice.startDisplay(false);

                } else if (keyCode == KeyCode.ESCAPE || keyCode == KeyCode.F12) {
                    if (LineBody.isFocusedHere())
                        LineBody.reFocus();
                    else
                        Header.refocus();
                    vanish();
                }
            }
        }
    }

    private static void vanish() {
        reset();
        INPUT_MENU_PANE.setOpacity(0);
    }

    private static void reset() {
        INPUT_MENU_HELP_LABEL.setText("");
        INPUT_MENU_FIELD.clear();
        INPUT_MENU_TITLE.setText("");
    }


}
