package Apache.workstation.pos;

import Apache.workstation.SceneController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class VehicleDescriptionRequestController {

    @FXML
    public Label requestLabel;
    @FXML
    public TextField requestField;

    public void initiate() {
        requestField.setTextFormatter(new TextFormatter<>((change) -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));
    }

    public void requestFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            SceneController.setToPointOfSale();
            Header.applyVehicleDescription(requestField.getText());
            requestField.clear();
        }
    }

}
