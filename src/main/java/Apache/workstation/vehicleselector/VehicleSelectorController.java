package Apache.workstation.vehicleselector;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

public class VehicleSelectorController {

    @FXML
    private VBox yearBox;
    @FXML
    private VBox makeBox;
    @FXML
    private VBox modelBox;
    @FXML
    private VBox engineBox;

    @FXML
    private Label yearLabel;
    @FXML
    private Label makeLabel;
    @FXML
    private Label modelLabel;
    @FXML
    private Label engineLabel;

    @FXML
    private TextField lockField;


    public void initiate(){
        VehicleSelector.initiate(
                yearBox,
                yearLabel,
                makeBox,
                makeLabel,
                modelBox,
                modelLabel,
                engineBox,
                engineLabel,
                lockField
        );
    }

    public void readKey(KeyEvent keyEvent){
        VehicleSelector.reportKeyPress(keyEvent.getCode());
    }

}
