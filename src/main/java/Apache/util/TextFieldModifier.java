package Apache.util;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class TextFieldModifier {

    public static void requireCaps(TextField textField){
        textField.setTextFormatter(new TextFormatter<>((change) -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));
    }

}
