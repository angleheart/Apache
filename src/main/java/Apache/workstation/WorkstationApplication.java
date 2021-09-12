package Apache.workstation;

import Apache.workstation.SceneController;
import javafx.application.Application;
import javafx.event.Event;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class WorkstationApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Epicor Free Apache Auto Parts");
        primaryStage.addEventFilter(MouseEvent.ANY, Event::consume);
        SceneController.setStage(primaryStage);
        SceneController.overrideLogin();
    }

    public static void launchWorkstation(String[] args) {
        launch(args);
    }


}
