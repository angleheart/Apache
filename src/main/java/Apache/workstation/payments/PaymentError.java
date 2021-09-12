package Apache.workstation.payments;

import javafx.scene.control.Label;

public class PaymentError {

    private static Label errorLabel;

    public static void initiate(Label label){
        errorLabel = label;
    }

    static void sendError(String message){
        java.awt.Toolkit.getDefaultToolkit().beep();
        errorLabel.setText("[ERROR]: " + message);
    }

    static void clear(){
        errorLabel.setText("");
    }

    static void alertPastDue(){
        java.awt.Toolkit.getDefaultToolkit().beep();
        errorLabel.setText("This account is past due");
    }

}
