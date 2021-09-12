package Apache.console.eom;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class StatementLauncher extends Application {


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(StatementImagerController.class.getResource("/statement.fxml"));
        Scene statementScene = new Scene(loader.load(), 2550, 3300);
        stage.setScene(statementScene);
        StatementImagerController statementImagerController = loader.getController();
        statementImagerController.init();
        StatementImager.printAllStatements();
        System.out.println("Initialization Complete");
    }

    public static void run(List<CustomerStatement> statements) {
        StatementImager.statements = statements;
        launch();
    }

}
