package Apache.workstation.pos;

import Apache.database.SequenceBase;
import Apache.workstation.inventory.InventoryManagerController;
import Apache.workstation.SceneController;
import Apache.workstation.vehicleselector.VehicleSelector;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class PointOfSale {

    @FXML
    private TextField customerField;
    @FXML
    private TextField cntrpersnField;
    @FXML
    private TextField poField;
    @FXML
    private TextField transCodeField;
    @FXML
    private VBox tranFields;
    @FXML
    private VBox qtyFields;
    @FXML
    private VBox mfgFields;
    @FXML
    private VBox partNumberFields;
    @FXML
    private VBox descriptionFields;
    @FXML
    private VBox listFields;
    @FXML
    private VBox unitFields;
    @FXML
    private VBox txFields;
    @FXML
    private VBox gpLabels;
    @FXML
    private Label customerNameLabel;
    @FXML
    private Label customerAddressLabel;
    @FXML
    private Label customerAddressLabel2;
    @FXML
    private Label customerPhoneLabel;
    @FXML
    private Label counterPersonNameLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private Label freightLabel;
    @FXML
    private Label freightAmountLabel;
    @FXML
    private Label subTotalAmountLabel;
    @FXML
    private Label salesTaxAmountLabel;
    @FXML
    private Label totalAmountLabel;
    @FXML
    private VBox avlLabels;
    @FXML
    private VBox lineNumberLabels;
    @FXML
    private VBox extendedLabels;
    @FXML
    private VBox voidLabels;
    @FXML
    private Label vehicleDescriptionLabel;
    @FXML
    private Label shipToLabel;


    @FXML
    private AnchorPane inputMenuBox;
    @FXML
    private TextField inputMenuField;
    @FXML
    private Label inputMenuTitle;
    @FXML
    private Label inputHelpLabel;


    @FXML
    private AnchorPane selectorBox;
    @FXML
    private VBox selectorContainer;
    @FXML
    private Label selectorTitle;
    @FXML
    private TextField selectorTextFieldLock;

    @FXML
    private AnchorPane pinkPane;
    @FXML
    private Label pinkNumber;
    @FXML
    private Label pinkName;
    @FXML
    private Label pinkDateTime;
    @FXML
    private Label pinkTerms;
    @FXML
    private Label pinkVehicle;
    @FXML
    private Label pinkShipTo;
    @FXML
    private Label pinkTotal;
    @FXML
    private VBox pinkQty;
    @FXML
    private VBox pinkMfgPart;
    @FXML
    private VBox pinkUnit;
    @FXML
    private VBox pinkExt;
    @FXML
    private VBox pinkTx;
    @FXML
    private TextField pinkField;

    public void initiate() {

        Header.initiate(
                customerField,
                cntrpersnField,
                poField,
                transCodeField,
                customerNameLabel,
                customerAddressLabel,
                customerAddressLabel2,
                customerPhoneLabel,
                counterPersonNameLabel,
                vehicleDescriptionLabel,
                shipToLabel
        );

        SelectionMenu.initiate(selectorTitle, selectorBox, selectorContainer, selectorTextFieldLock);
        InputMenu.initiate(inputMenuBox, inputMenuTitle, inputMenuField, inputHelpLabel);

        LineBody.initiate(
                lineNumberLabels,
                tranFields,
                qtyFields,
                mfgFields,
                partNumberFields,
                descriptionFields,
                listFields,
                unitFields,
                extendedLabels,
                txFields,
                avlLabels,
                voidLabels,
                gpLabels
        );

        Totals.initiate(
                subTotalAmountLabel,
                salesTaxAmountLabel,
                freightLabel,
                freightAmountLabel,
                totalAmountLabel
        );

        PinkInvoice.initiate(
                pinkPane,
                pinkNumber,
                pinkName,
                pinkDateTime,
                pinkTerms,
                pinkVehicle,
                pinkShipTo,
                pinkTotal,
                pinkQty,
                pinkMfgPart,
                pinkUnit,
                pinkExt,
                pinkTx,
                pinkField
        );

        Error.initiate(errorLabel);
        ActiveSequence.startNew();
    }

    public void customerFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().isFunctionKey())
            handleFunctions(keyEvent.getCode(), false);
        else
        Header.requestMoveCustomerNumberField(keyEvent.getCode());
    }

    public void cntrpersnFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().isFunctionKey())
            handleFunctions(keyEvent.getCode(), false);
        else
        Header.requestMoveCounterPersonField(keyEvent.getCode());
    }

    public void poFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().isFunctionKey())
            handleFunctions(keyEvent.getCode(), false);
        else
            Header.requestMovePoField(keyEvent.getCode());
    }

    public void gridKeyPressed(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();
        switch (code) {
            case ENTER -> LineBody.requestCarriageReturn();
            case UP -> LineBody.requestUp();
            case DOWN -> LineBody.requestDown();
            case RIGHT -> LineBody.requestRight();
            case LEFT -> LineBody.requestLeft();
            default -> handleFunctions(code, true);
        }

    }

    public static void refocus(){
        if(LineBody.isFocusedHere())
            LineBody.reFocus();
        else
            Header.refocus();
    }

    private void handleFunctions(KeyCode code, boolean grid) {
        switch (code) {
            case F1:
                if (LineBody.allowCalculator())
                    InputMenu.performRequest(InputMenuType.COST);
                break;
            case F2:
                if (!ActiveSequence.allowRelease())
                    return;
                ActiveSequence.release(11);
                break;
            case F3:
                if (!ActiveSequence.allowRelease())
                    return;
                ActiveSequence.release(31);
                break;
            case F4:
                if (!ActiveSequence.allowRelease())
                    return;
                InputMenu.performRequest(InputMenuType.RELEASE_CODE);
                break;
            case F5:
                VehicleSelector.startRequest();
                SceneController.setToVehicleDescriptionRequest();
                break;
            case F6:
                InputMenu.performRequest(InputMenuType.SHIP_TO);
                break;
            case F7:
                SelectionMenu.performRequest(SelectionMenuType.SEQUENCE, SequenceBase.getSequences());
                break;
            case F8:
                LineBody.reportF8();
                break;
            case F9:
                InventoryManagerController.openedFromPointOfSale = true;
                SceneController.setToInventoryManager();
                break;
            case F11:
                SelectionMenu.performRequest(SelectionMenuType.SPECIAL_FUNCTIONS, null);
                break;
            case F12:
                ActiveSequence.startNew();
                SceneController.setToPointOfSale();
                break;
            default:
                if(grid)
                    LineBody.reportGenericKey();
        }
    }

    public void inputMenuKeyPressed(KeyEvent keyEvent) {
        InputMenu.reportKeyCode(keyEvent.getCode());
    }

    public void selectorMenuKeyPress(KeyEvent keyEvent) {
        SelectionMenu.reportKeyCode(keyEvent.getCode());
    }

    public void pinkFieldKeyPressed(KeyEvent keyEvent) {
        PinkInvoice.reportKeyPress(keyEvent.getCode());
    }

}
