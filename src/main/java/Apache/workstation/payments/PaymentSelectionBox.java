package Apache.workstation.payments;

import Apache.database.PartBase;
import Apache.database.SaleBase;
import Apache.objects.Customer;
import Apache.objects.Part;
import Apache.objects.Selectable;
import Apache.util.InputVerifier;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.List;

class PaymentSelectionBox {

    private static List<Selectable> selectables;
    private static Label[] labels;
    private static int selectableDisplayStartIndex;
    private static int selectableIndex;
    private static int focusIndex;

    private static VBox selectionBox;
    private static TextField selectorLock;


    static void initiate(VBox box, TextField lock) {
        selectionBox = box;
        selectionBox.setOpacity(0);
        selectorLock = lock;
        labels = new Label[7];
        ObservableList<Node> nodes = box.getChildren();
        for (int index = 0; index < 7; index++)
            labels[index] = (Label) nodes.get(index);
    }

    static void performCustomerRequest(List<Selectable> customers) {
        clearLabels();
        selectables = customers;
        focusIndex = 0;
        selectableIndex = 0;
        selectableDisplayStartIndex = 0;
        updateOnScroll();
        selectorLock.requestFocus();
        labels[focusIndex].setStyle("-fx-background-color: lightgray");
        selectionBox.setOpacity(1);
    }

    private static void clearLabels() {
        for (Label label : labels) {
            label.setText("");
            label.setStyle("");
        }
    }

    private static void updateOnScroll() {
        int labelIndex = 0;
        for (int i = selectableDisplayStartIndex; labelIndex < labels.length; i++) {
            if (i == selectables.size())
                break;
            labels[labelIndex++].setText(selectables.get(i).getSelectableName());
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
                selectionBox.setOpacity(0);
                PaymentHeader.postCustomer((Customer)selectables.get(selectableIndex));
                break;
            case F12:
            case ESCAPE:
                selectionBox.setOpacity(0);
                PaymentHeader.focusAtTop();
        }
    }

    private static void navUp() {
        if (selectableIndex == 0)
            return;
        if (focusIndex == 0) {
            scrollUp();
            return;
        }
        labels[focusIndex].setStyle("");
        focusIndex--;
        selectableIndex--;
        labels[focusIndex].setStyle("-fx-background-color: lightgray");
    }

    private static void navDown() {
        if (selectableIndex == selectables.size() - 1)
            return;
        if (focusIndex == labels.length - 1) {
            scrollDown();
            return;
        }
        labels[focusIndex].setStyle("");
        focusIndex++;
        selectableIndex++;
        labels[focusIndex].setStyle("-fx-background-color: lightgray");
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


}
