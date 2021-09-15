package Apache.workstation.inventory;

import Apache.database.PartBase;
import Apache.database.SaleBase;
import Apache.objects.Part;
import Apache.workstation.SceneController;
import Apache.objects.Selectable;
import Apache.util.InputVerifier;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.List;

import static Apache.util.General.cleanDouble;
import static Apache.util.TextFieldModifier.requireCaps;

public class InventoryManagerController {

    @FXML
    private Label availableLabel;
    @FXML
    private Label stockQuantityLabel;
    @FXML
    private Label costLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private Label lineLabel;
    @FXML
    private Label partNumberLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private TextField lineCodeField;
    @FXML
    private TextField partNumberField;
    @FXML
    private AnchorPane descriptionRequestBox;
    @FXML
    private TextField descriptionRequestField;
    @FXML
    private TextField recountRequestField;
    @FXML
    private AnchorPane recountRequestBox;
    @FXML
    private AnchorPane costRequestBox;
    @FXML
    private TextField costRequestField;
    @FXML
    private AnchorPane stockRequestBox;
    @FXML
    private TextField stockRequestField;
    @FXML
    private AnchorPane deleteRequestBox;
    @FXML
    private Label deleteRequestLabel;
    @FXML
    private TextField deleteRequestField;

    // Part Registration Tool
    @FXML
    private AnchorPane partRegTool;
    @FXML
    private TextField partRegLineField;
    @FXML
    private TextField partRegDescriptionField;
    @FXML
    private TextField partRegPartNumberField;
    @FXML
    private TextField partRegAvailField;
    @FXML
    private TextField partRegStockField;
    @FXML
    private TextField partRegCostField;
    @FXML
    private Label partRegMessageLabel;


    // Part selection
    @FXML
    private AnchorPane partSelect;
    @FXML
    private VBox labelContainer;
    @FXML
    private TextField selectorLock;
    @FXML
    private Label partSelectorTitleLabel;


    private void sendPartRegSuccess(String message) {
        partRegMessageLabel.setTextFill(Color.DARKGREEN);
        partRegMessageLabel.setText(message);
    }

    private void sendPartRegError(String message) {
        partRegMessageLabel.setTextFill(Color.RED);
        partRegMessageLabel.setText(message);
        java.awt.Toolkit.getDefaultToolkit().beep();

    }

    private void clearPartRegMessage() {
        partRegMessageLabel.setText("");
    }

    static class RegistrationRequest {
        static String LINE;
        static String DESC;
        static String PART;
        static String AVAIL;
        static String STOCK;
        static String COST;
    }

    public void launchPartRegistrationTool() {
        clearPartRegMessage();
        partRegTool.setOpacity(1);
        partRegLineField.requestFocus();
    }

    public void partRegLineFieldKeyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case ENTER:
            case DOWN:
                clearPartRegMessage();
                partRegDescriptionField.requestFocus();
        }
    }

    public void partRegDescriptionFieldKeyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case ENTER:
            case DOWN:
                clearPartRegMessage();
                partRegPartNumberField.requestFocus();
                break;
            case UP:
                clearPartRegMessage();
                partRegLineField.requestFocus();
        }
    }

    public void partRegPartNumberFieldKeyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case ENTER, DOWN -> {
                clearPartRegMessage();
                partRegAvailField.requestFocus();
            }
            case UP -> {
                clearPartRegMessage();
                partRegDescriptionField.requestFocus();
            }
        }
    }

    public void partRegAvailFieldKeyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case ENTER, DOWN -> {
                clearPartRegMessage();
                partRegStockField.requestFocus();
            }
            case UP -> {
                clearPartRegMessage();
                partRegPartNumberField.requestFocus();
            }
        }
    }

    public void partRegStockFieldKeyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case ENTER, DOWN -> {
                clearPartRegMessage();
                partRegCostField.requestFocus();
            }
            case UP -> {
                clearPartRegMessage();
                partRegAvailField.requestFocus();
            }
        }
    }

    public void partRegCostFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.UP) {
            clearPartRegMessage();
            partRegStockField.requestFocus();
        }
    }

    private void attemptPartRegistration() {
        setRegFieldsLock(true);
        String line = partRegLineField.getText();
        String desc = partRegDescriptionField.getText();
        String part = partRegPartNumberField.getText();
        String avail = partRegAvailField.getText();
        String stock = partRegStockField.getText();
        String cost = partRegCostField.getText();

        if (line.equals(RegistrationRequest.LINE) &&
                desc.equals(RegistrationRequest.DESC) &&
                part.equals(RegistrationRequest.PART) &&
                avail.equals(RegistrationRequest.AVAIL) &&
                stock.equals(RegistrationRequest.STOCK) &&
                cost.equals(RegistrationRequest.COST)
        ) return;

        RegistrationRequest.LINE = line;
        RegistrationRequest.DESC = desc;
        RegistrationRequest.PART = part;
        RegistrationRequest.AVAIL = avail;
        RegistrationRequest.STOCK = stock;
        RegistrationRequest.COST = cost;

        if (!InputVerifier.verifyMfg(RegistrationRequest.LINE)) {
            sendPartRegError("Invalid line code");
            partRegMessageLabel.requestFocus();
            partRegLineField.requestFocus();
            return;
        }

        if (!InputVerifier.verifyDescription(RegistrationRequest.DESC)) {
            sendPartRegError("Invalid description");
            partRegMessageLabel.requestFocus();
            partRegDescriptionField.requestFocus();
            return;
        }

        if (!InputVerifier.verifyPartNumber(RegistrationRequest.PART)) {
            sendPartRegError("Invalid part number");
            partRegMessageLabel.requestFocus();
            partRegPartNumberField.requestFocus();
            return;
        }

        if (!InputVerifier.verifyAvailQty(RegistrationRequest.AVAIL)) {
            sendPartRegError("Invalid available quantity");
            partRegMessageLabel.requestFocus();
            partRegAvailField.requestFocus();
            return;
        }

        if (!InputVerifier.verifyStockQuantity(RegistrationRequest.STOCK)) {
            sendPartRegError("Invalid stock quantity");
            partRegMessageLabel.requestFocus();
            partRegStockField.requestFocus();
            return;
        }

        if (!InputVerifier.verifyCost(RegistrationRequest.COST)) {
            sendPartRegError("Invalid cost");
            partRegMessageLabel.requestFocus();
            partRegCostField.requestFocus();
            return;
        }

        if (PartBase.addNewPart(new Part(
                RegistrationRequest.LINE,
                RegistrationRequest.PART,
                RegistrationRequest.DESC,
                Double.parseDouble(RegistrationRequest.COST),
                Integer.parseInt(RegistrationRequest.STOCK),
                Integer.parseInt(RegistrationRequest.AVAIL)
        ))) {
            sendPartRegSuccess(
                    "Added part " + RegistrationRequest.LINE + " " + RegistrationRequest.PART + " to PartBase"
            );
        } else {
            sendPartRegError("Failed to add part, already exists");
        }
    }

    private void setRegFieldsLock(boolean lock) {
        partRegLineField.setEditable(lock);
        partRegDescriptionField.setEditable(lock);
        partRegPartNumberField.setEditable(lock);
        partRegAvailField.setEditable(lock);
        partRegStockField.setEditable(lock);
        partRegCostField.setEditable(lock);
    }

    public static boolean openedFromPointOfSale;
    private Part loadedPart;


    public void deleteRequestFieldKeyPressed(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();
        if (code == KeyCode.F12 || code == KeyCode.ESCAPE) {
            deleteRequestBox.setOpacity(0);
            partNumberField.requestFocus();
            deleteRequestField.clear();
            return;
        }

        if (code == KeyCode.ENTER) {
            if (deleteRequestField.getText().equalsIgnoreCase("CONFIRM")) {

                if (!PartBase.deletePart(loadedPart.getLineCode(), loadedPart.getPartNumber())) {
                    sendError("Database Error: Failed to delete part");
                    deleteRequestBox.setOpacity(0);
                    partNumberField.requestFocus();
                    deleteRequestField.clear();
                    return;
                }

                deleteRequestBox.setOpacity(0);
                partNumberField.requestFocus();
                deleteRequestField.clear();
                sendSuccess("Update Successful");
            } else {
                deleteRequestBox.setOpacity(0);
                partNumberField.requestFocus();
                deleteRequestField.clear();
                sendError("Delete Aborted");
            }
        }
    }

    public void costRequestFieldKeyPressed(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();
        if (code == KeyCode.F12 || code == KeyCode.ESCAPE) {
            costRequestBox.setOpacity(0);
            partNumberField.requestFocus();
            costRequestField.clear();
            return;
        }
        if (code == KeyCode.ENTER) {
            String newCost = costRequestField.getText();

            if (InputVerifier.verifyPrice(newCost)) {
                clearError();
                if (!PartBase.updateCost(
                        loadedPart.getLineCode(), loadedPart.getPartNumber(), Double.parseDouble(newCost))) {
                    sendError("Database update error");
                } else {
                    loadPartData();
                    sendSuccess("Update Successful");
                }
            } else {
                sendError("Invalid cost");
            }
            costRequestBox.setOpacity(0);
            partNumberField.requestFocus();
            costRequestField.clear();
        }
    }

    public void stockRequestFieldKeyPressed(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();
        if (code == KeyCode.F12 || code == KeyCode.ESCAPE) {
            stockRequestBox.setOpacity(0);
            partNumberField.requestFocus();
            stockRequestField.clear();
            return;
        }
        if (code == KeyCode.ENTER) {
            String newStock = stockRequestField.getText();

            if (InputVerifier.verifyStockQuantity(newStock)) {
                clearError();
                if (!PartBase.updateStockQuantity(
                        loadedPart.getLineCode(), loadedPart.getPartNumber(), Integer.parseInt(newStock))) {
                    sendError("Database update error");
                } else {
                    loadPartData();
                    sendSuccess("Update Successful");
                }
            } else {
                sendError("Invalid stock quantity");
            }
            stockRequestBox.setOpacity(0);
            partNumberField.requestFocus();
            stockRequestField.clear();
        }
    }

    public void recountRequestFieldKeyPressed(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();
        if (code == KeyCode.F12 || code == KeyCode.ESCAPE) {
            recountRequestBox.setOpacity(0);
            partNumberField.requestFocus();
            recountRequestField.clear();
            return;
        }
        if (code == KeyCode.ENTER) {
            String newAvailable = recountRequestField.getText();

            if (InputVerifier.verifyAvailQty(newAvailable)) {
                clearError();
                if (!PartBase.updateAvailableQuantity(
                        loadedPart.getLineCode(), loadedPart.getPartNumber(), Integer.parseInt(newAvailable))) {
                    sendError("Database update error");
                } else {
                    loadPartData();
                    sendSuccess("Update Successful");
                }
            } else {
                sendError("Invalid available quantity");
            }
            recountRequestBox.setOpacity(0);
            partNumberField.requestFocus();
            recountRequestField.clear();
        }
    }

    public void descriptionRequestFieldKeyPressed(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();
        if (code == KeyCode.F12 || code == KeyCode.ESCAPE) {
            descriptionRequestBox.setOpacity(0);
            partNumberField.requestFocus();
            descriptionRequestField.clear();
            return;
        }
        if (code == KeyCode.ENTER) {
            String newDescription = descriptionRequestField.getText();

            if (InputVerifier.verifyDescription(newDescription)) {
                clearError();
                if (!PartBase.updateDescription(loadedPart.getLineCode(), loadedPart.getPartNumber(), newDescription)) {
                    sendError("Database update error");
                } else {
                    loadPartData();
                    sendSuccess("Update Successful");
                }
            } else {
                sendError("Invalid description");
            }
            descriptionRequestBox.setOpacity(0);
            partNumberField.requestFocus();
            descriptionRequestField.clear();
        }
    }


    public void initiate() {
        partRegTool.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case F12 -> {
                    partRegLineField.clear();
                    partRegDescriptionField.clear();
                    partRegPartNumberField.clear();
                    partRegAvailField.clear();
                    partRegStockField.clear();
                    partRegCostField.clear();
                    partRegTool.setOpacity(0);
                    partNumberField.clear();
                    lineCodeField.requestFocus();
                }
                case F5 -> {
                    attemptPartRegistration();
                }
            }
        });

        requireCaps(lineCodeField);
        requireCaps(partNumberField);

        requireCaps(descriptionRequestField);
        requireCaps(recountRequestField);
        requireCaps(costRequestField);
        requireCaps(stockRequestField);
        requireCaps(deleteRequestField);

        requireCaps(partRegLineField);
        requireCaps(partRegDescriptionField);
        requireCaps(partRegPartNumberField);
        requireCaps(partRegAvailField);
        requireCaps(partRegStockField);
        requireCaps(partRegCostField);

        reset();
    }

    public void lineCodeFieldKeyPressed(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();

        if ((code == KeyCode.ENTER && lineCodeField.getText().equalsIgnoreCase("END")) ||
                code == KeyCode.F12) {
            SceneController.setToPointOfSale();
            reset();
            return;
        }

        if (code == KeyCode.ENTER || code == KeyCode.DOWN) {
            //Wild card
            if (lineCodeField.getText().equalsIgnoreCase("*")) {
                partNumberField.clear();
                partNumberField.requestFocus();
                return;
            }


            // Force in legal mfg
            if (InputVerifier.verifyMfg(lineCodeField.getText())) {
                clearError();
                partNumberField.requestFocus();
            } else {
                sendError("Invalid line code");
                lineCodeField.clear();
                lineCodeField.setText("*");
                partRegLineField.requestFocus();
                lineCodeField.requestFocus();
            }
        } else {
            handleFunctions(code);
        }
    }


    public void partNumberFieldKeyPressed(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();

        if (code == KeyCode.ENTER && partNumberField.getText().equalsIgnoreCase("END") ||
                code == KeyCode.F12) {
            SceneController.setToPointOfSale();
            reset();
            return;
        }

        if (code == KeyCode.UP) {
            clearLabels();
            clearError();
            loadedPart = null;
            partNumberField.clear();
            lineCodeField.setText("*");
            partRegMessageLabel.requestFocus();
            lineCodeField.requestFocus();
            return;
        }

        if (code == KeyCode.ENTER || code == KeyCode.DOWN) {
            if (lineCodeField.getText().equalsIgnoreCase("*")) {
                selectorLock.requestFocus();
                if (!preparePartSelect(false)) {
                    partNumberField.clear();
                    partNumberField.requestFocus();
                    sendError("No parts found");
                    return;
                }
                if (selectables.size() == 1) {
                    lineCodeField.setText(((Part) selectables.get(0)).getLineCode());
                    partNumberField.setText(((Part) selectables.get(0)).getPartNumber());
                    loadPartData();
                    partNumberField.requestFocus();
                    partSelect.setOpacity(0);
                    return;
                }
                requestPartSelection();
                return;
            }

            if (InputVerifier.verifyPartNumber(partNumberField.getText())) {
                clearError();
                availableLabel.requestFocus();
                partNumberField.requestFocus();
                loadPartData();


            } else {
                sendError("Invalid part number");
                partNumberField.clear();
                partNumberField.requestFocus();

            }
        } else {
            handleFunctions(code);
        }
    }


    private void handleFunctions(KeyCode code) {

        switch (code) {
            case F3:
                if (preparePartSelect(true))
                    requestPartSelection();
                else {
                    partNumberField.requestFocus();
                    sendError("No part ledger entries");
                }

                break;
            case F4:
                if (partLoaded()) {
                    setInputFieldsToLoadedPart();
                    descriptionRequestBox.setOpacity(1);
                    descriptionRequestField.requestFocus();
                }
                break;
            case F5:
                if (partLoaded()) {
                    setInputFieldsToLoadedPart();
                    recountRequestBox.setOpacity(1);
                    recountRequestField.requestFocus();
                }
                break;
            case F6:
                if (partLoaded()) {
                    setInputFieldsToLoadedPart();
                    costRequestBox.setOpacity(1);
                    costRequestField.requestFocus();
                }
                break;
            case F7:
                if (partLoaded()) {
                    setInputFieldsToLoadedPart();
                    stockRequestBox.setOpacity(1);
                    stockRequestField.requestFocus();
                }
                break;
            case F9:
                if (partLoaded()) {
                    deleteRequestBox.setOpacity(1);
                    setInputFieldsToLoadedPart();
                    deleteRequestLabel.setText(
                            "Type \"Confirm\" to delete part " + loadedPart.getLineCode() + " "
                                    + loadedPart.getPartNumber());
                    deleteRequestField.requestFocus();
                }
                break;
            case F8:
                launchPartRegistrationTool();
                break;
        }
    }

    private boolean partLoaded() {
        if (loadedPart == null) {
            sendError("No part loaded");
            return false;
        }
        clearError();
        return true;
    }


    private void loadPartData() {
        loadedPart = PartBase.tryToCorrectPartNumber(lineCodeField.getText(), partNumberField.getText());
        if (loadedPart == null) {
            sendError("Part not found");
            clearLabels();
            availableLabel.requestFocus();
            partNumberField.requestFocus();
            return;
        }
        clearError();
        lineLabel.setText(loadedPart.getLineCode());
        partNumberLabel.setText(loadedPart.getPartNumber());
        availableLabel.setText(Integer.toString(loadedPart.getAvailableQuantity()));
        stockQuantityLabel.setText(Integer.toString(loadedPart.getStockQuantity()));
        costLabel.setText("$".concat(cleanDouble(loadedPart.getCost(), 2)));
        descriptionLabel.setText(loadedPart.getDescription());
    }

    public void focusStart(){
        selectorLock.requestFocus();
        lineCodeField.requestFocus();
    }

    private void reset() {
        lineCodeField.setText("*");
        partNumberField.clear();
        clearError();
        clearLabels();
        partRegLineField.requestFocus();
        lineCodeField.requestFocus();
        openedFromPointOfSale = false;
    }

    private void clearLabels() {
        availableLabel.setText("");
        costLabel.setText("");
        stockQuantityLabel.setText("");
        descriptionLabel.setText("");
        lineLabel.setText("");
        partNumberLabel.setText("");
    }

    private void sendSuccess(String message) {
        errorLabel.setTextFill(Color.LIGHTGREEN);
        errorLabel.setText(message);
    }

    private void sendError(String message) {
        errorLabel.setTextFill(Color.RED);
        java.awt.Toolkit.getDefaultToolkit().beep();
        errorLabel.setText(message);
    }

    private void clearError() {
        errorLabel.setText("");
    }

    private void setInputFieldsToLoadedPart() {
        lineCodeField.setText(loadedPart.getLineCode());
        partNumberField.setText(loadedPart.getPartNumber());
    }


    private List<Selectable> selectables;
    private final Label[] labels = new Label[9];
    private int selectableDisplayStartIndex;
    private int selectableIndex;
    private int focusIndex;

    private boolean preparePartSelect(boolean ledger) {
        if (ledger) {
            if(loadedPart != null){
                selectables = SaleBase.getPartLedgerEntries(
                        loadedPart.getLineCode(),
                        loadedPart.getPartNumber()

                );
                partSelectorTitleLabel.setText(loadedPart.getLineCode() + " " + loadedPart.getPartNumber());

            }else{

                String mfg = lineCodeField.getText();
                String partNumber = partNumberField.getText();
                if(!InputVerifier.verifyMfg(mfg) || !InputVerifier.verifyPartNumber(partNumber))
                    return false;
                else{
                    selectables = SaleBase.getPartLedgerEntries(
                            mfg,
                            partNumber
                    );
                    partSelectorTitleLabel.setText(mfg + " " + partNumber);
                }
            }
        } else {
            selectables = PartBase.getPartsByNumber(partNumberField.getText());
            partSelectorTitleLabel.setText("Select Part");
        }

        if (selectables == null || selectables.size() == 0)
            return false;


        int index = 0;
        for (Node node : labelContainer.getChildren()) {
            labels[index] = (Label) node;
            index++;
        }
        for (Label label : labels) {
            label.setText("");
            label.setTextFill(Color.BLACK);
        }
        labels[0].setTextFill(Color.WHITE);
        updateOnScroll();
        return true;
    }

    private void requestPartSelection() {
        focusIndex = 0;
        selectableIndex = 0;
        selectableDisplayStartIndex = 0;
        selectorLock.requestFocus();
        partSelect.setOpacity(1);
    }

    private void updateOnScroll() {
        int labelIndex = 0;
        for (int i = selectableDisplayStartIndex; labelIndex < labels.length; i++) {
            if (i == selectables.size())
                break;
            labels[labelIndex++].setText(selectables.get(i).getSelectableName());
        }
    }

    public void selectorKeyPress(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        switch (keyCode) {
            case UP:
                navUp();
                break;
            case DOWN:
                navDown();
                break;
            case ENTER:
                Part part = (Part) selectables.get(selectableIndex);
                lineCodeField.setText(part.getLineCode());
                partNumberField.setText(part.getPartNumber());
                loadPartData();
                partNumberField.requestFocus();
                partSelect.setOpacity(0);
                break;
            case F12:
            case ESCAPE:
                partNumberField.clear();
                partNumberField.requestFocus();
                partSelect.setOpacity(0);
        }
    }

    private void navUp() {
        if (selectableIndex == 0)
            return;
        if (focusIndex == 0) {
            scrollUp();
            return;
        }
        labels[focusIndex].setTextFill(Color.BLACK);
        focusIndex--;
        selectableIndex--;
        labels[focusIndex].setTextFill(Color.WHITE);
    }

    private void navDown() {
        if (selectableIndex == selectables.size() - 1)
            return;
        if (focusIndex == labels.length - 1) {
            scrollDown();
            return;
        }
        labels[focusIndex].setTextFill(Color.BLACK);
        focusIndex++;
        selectableIndex++;
        labels[focusIndex].setTextFill(Color.WHITE);
    }

    private void scrollUp() {
        selectableIndex--;
        selectableDisplayStartIndex--;
        updateOnScroll();
    }

    private void scrollDown() {
        selectableIndex++;
        selectableDisplayStartIndex++;
        updateOnScroll();
    }


}
