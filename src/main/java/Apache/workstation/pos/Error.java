package Apache.workstation.pos;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class Error {

    private static Label ERROR_LABEL;

    static void initiate(Label errorLabel){
        ERROR_LABEL = errorLabel;
    }

    static void send(String message){
        java.awt.Toolkit.getDefaultToolkit().beep();
        ERROR_LABEL.setTextFill(Color.RED);
        ERROR_LABEL.setText("[ERROR]: " + message);
    }

    static void sendSuccess(String message){
        ERROR_LABEL.setTextFill(Color.LIGHTGREEN);
        ERROR_LABEL.setText("[SUCCESS]: " + message);
    }

    static void sendMessage(String message){
        ERROR_LABEL.setTextFill(Color.YELLOW);
        ERROR_LABEL.setText(message);
    }

    static void clear(){
        ERROR_LABEL.setText("");
    }

}
