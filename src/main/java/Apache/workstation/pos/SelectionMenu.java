package Apache.workstation.pos;

import Apache.database.InvoiceBase;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import Apache.objects.*;

import java.util.ArrayList;
import java.util.List;

public class SelectionMenu {

    private static Label TITLE_LABEL;
    private static AnchorPane SELECTOR_PANE;
    private static Label[] LABEL_ARRAY;
    private static TextField SELECTOR_LOCK;

    private static SelectionMenuType type;
    private static List<Selectable> selectables;

    private static int selectableDisplayStartIndex;
    private static int selectableIndex;
    private static int focusIndex;

    private static String costEntered = "";

    public static void setCostEntered(String cost) {
        costEntered = cost;
    }

    private static final List<Selectable> specialFunctions = new ArrayList<>();

    public static void initiate(
            Label titleLabel,
            AnchorPane selectorPane,
            VBox selectionContainer,
            TextField selectorLock) {
        TITLE_LABEL = titleLabel;
        SELECTOR_PANE = selectorPane;
        ObservableList<Node> labelList = selectionContainer.getChildren();
        LABEL_ARRAY = new Label[labelList.size()];
        int index = 0;
        for (Node node : labelList)
            LABEL_ARRAY[index++] = (Label) node;
        SELECTOR_LOCK = selectorLock;

        specialFunctions.add(new SpecialFunction("Add Freight To Invoice"));
        specialFunctions.add(new SpecialFunction("View Invoice By Number"));
        specialFunctions.add(new SpecialFunction("Review Open Invoices"));
    }

    public static void performRequest(SelectionMenuType typeSet, List<Selectable> listSet) {
        type = typeSet;

        if (type == SelectionMenuType.INVOICE_VIEW_CALLBACK) {
            SELECTOR_LOCK.requestFocus();
            SELECTOR_PANE.setOpacity(1);
            return;
        }


        if (typeSet == SelectionMenuType.SPECIAL_FUNCTIONS)
            listSet = specialFunctions;

        if (listSet != null && listSet.size() == 0) {
            switch (typeSet) {
                case SEQUENCE:
                    Error.send("No sequences available to load");
                    break;
                case INVOICE_VIEW:
                    Error.send("There are currently no open invoices");
                    break;
                default:
                    Error.send("No options available");
            }
            return;
        }


        resetLabels();
        LABEL_ARRAY[0].setStyle("-fx-background-color: darkgray");
        selectableDisplayStartIndex = 0;
        focusIndex = 0;
        selectableIndex = 0;
        selectables = listSet;
        updateTitle();
        updateOnScroll();

        SELECTOR_LOCK.requestFocus();
        SELECTOR_PANE.setOpacity(1);
    }

    private static void updateTitle() {
        switch (type) {
            case CUSTOMER -> TITLE_LABEL.setText("Select Customer");
            case PART -> TITLE_LABEL.setText("Select Part");
            case SEQUENCE -> TITLE_LABEL.setText("Select Sequence");
            case CALCULATOR -> TITLE_LABEL.setText("Your Cost: " + costEntered);
            case SPECIAL_FUNCTIONS -> TITLE_LABEL.setText("Special Functions");
            case INVOICE_VIEW, INVOICE_VIEW_CALLBACK -> TITLE_LABEL.setText("Select Invoice");
            case PAST_PURCHASE -> TITLE_LABEL.setText(
                    ((PastPurchase) selectables.get(0)).getMfg() + " " +
                            ((PastPurchase) selectables.get(0)).getPartNumber()
            );
        }
    }

    public static void reportKeyCode(KeyCode keyCode) {
        switch (keyCode) {
            case UP:
                navUp();
                break;
            case DOWN:
                navDown();
                break;
            case ENTER:
                submitResponse();
                break;
            case F12:
            case ESCAPE:
                cancel();
        }
    }

    private static void navUp() {
        if (selectableIndex == 0)
            return;
        if (focusIndex == 0) {
            scrollUp();
            return;
        }
        LABEL_ARRAY[focusIndex].setStyle("");
        focusIndex--;
        selectableIndex--;
        LABEL_ARRAY[focusIndex].setStyle("-fx-background-color: darkgray");
    }


    private static void navDown() {
        if (selectableIndex == selectables.size() - 1)
            return;
        if (focusIndex == LABEL_ARRAY.length - 1) {
            scrollDown();
            return;
        }
        LABEL_ARRAY[focusIndex].setStyle("");
        focusIndex++;
        selectableIndex++;
        LABEL_ARRAY[focusIndex].setStyle("-fx-background-color: darkgray");
    }

    private static void scrollUp() {
        selectableIndex--;
        selectableDisplayStartIndex--;
        updateOnScroll();
    }

    private static void scrollDown() {
        selectableIndex++;
        selectableDisplayStartIndex++;
        updateOnScroll();
    }

    private static void updateOnScroll() {
        int labelIndex = 0;
        for (int i = selectableDisplayStartIndex; labelIndex < LABEL_ARRAY.length; i++) {
            if (i == selectables.size())
                break;
            LABEL_ARRAY[labelIndex++].setText(selectables.get(i).getSelectableName());
        }
    }

    private static void resetLabels() {
        for (Label label : LABEL_ARRAY) {
            label.setText("");
            label.setStyle("");
        }
    }

    private static void cancel() {
        switch (type) {
            case CUSTOMER:
                ActiveSequence.startNew();
                Header.reset();
                Header.focusCustomerNumberField();
                break;
            case INVOICE_VIEW_CALLBACK:
            case INVOICE_VIEW:
            case PART:
            case SEQUENCE:
            case SPECIAL_FUNCTIONS:
                if (LineBody.isFocusedHere())
                    LineBody.reFocus();
                else
                    Header.refocus();
                break;
            case PAST_PURCHASE:
            case CALCULATOR:
                LineBody.reFocus();
                break;
        }
        close();
    }

    private static void close() {
        resetLabels();
        SELECTOR_PANE.setOpacity(0);
    }

    private static void submitResponse() {
        switch (type) {
            case CUSTOMER -> {
                ActiveSequence.CUSTOMER = (Customer) selectables.get(selectableIndex);
                Header.loadCustomer();
                Header.focusCounterPersonField();
                close();
            }
            case SEQUENCE -> {
                ActiveSequence.load((Sequence) selectables.get(selectableIndex));
                close();
            }
            case PART -> {
                LineBody.applyPart((Part) selectables.get(selectableIndex));
                close();
            }
            case CALCULATOR -> {
                LineBody.applyNewUnitPrice(((CalculatorPricingOption) selectables.get(selectableIndex)).getPrice());
                close();
            }
            case SPECIAL_FUNCTIONS -> {
                if (selectables.get(selectableIndex).getSelectableName().contains("Freight")) {
                    close();
                    InputMenu.performRequest(InputMenuType.FREIGHT);
                } else if (selectables.get(selectableIndex).getSelectableName().contains("Reprint")) {
                    close();
                    InputMenu.performRequest(InputMenuType.REPRINT);
                } else if (selectables.get(selectableIndex).getSelectableName().contains("Invoice By Number")) {
                    close();
                    InputMenu.performRequest(InputMenuType.INVOICE_VIEW);
                } else if (selectables.get(selectableIndex).getSelectableName().contains("Review Open")) {
                    List<Selectable> invoices = InvoiceBase.getAllOpenInvoices();
                    if (invoices == null) {
                        Error.send("A Apache.database error occurred");
                        cancel();
                        return;
                    }
                    if (invoices.size() == 0) {
                        Error.send("There are currently no open invoices");
                        cancel();
                        return;
                    }

                    SelectionMenu.performRequest(SelectionMenuType.INVOICE_VIEW, invoices);
                }
            }
            case INVOICE_VIEW, INVOICE_VIEW_CALLBACK -> {
                PinkInvoice.loadInvoice((Invoice) selectables.get(selectableIndex));
                SELECTOR_PANE.setOpacity(0);
                PinkInvoice.startDisplay(true);
            }
            case PAST_PURCHASE -> {
                LineBody.postF8((PastPurchase) selectables.get(selectableIndex));
                close();
            }

        }
    }


}
