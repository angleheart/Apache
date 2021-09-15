package Apache.workstation.vehicleselector;

import Apache.http.Gateway;
import Apache.workstation.pos.Header;
import Apache.workstation.pos.PointOfSale;
import Apache.workstation.SceneController;
import Apache.util.InputVerifier;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Locale;

public class VehicleSelector {

    private static Label MAKE_TITLE;
    private static Label MODEL_TITLE;
    private static Label ENGINE_TITLE;

    private static VBox MAKE_BOX;
    private static VBox MODEL_BOX;
    private static VBox ENGINE_BOX;


    private static final Label[] YEAR_LABELS = new Label[14];
    private static List<String> YEAR_SOURCE;
    private static int yearDisplayStartIndex;
    private static int yearSelectIndex;
    private static int yearFocusIndex;


    private static final Label[] MAKE_LABELS = new Label[8];
    private static List<String> MAKE_SOURCE;
    private static int makeDisplayStartIndex;
    private static int makeSelectIndex;
    private static int makeFocusIndex;


    private static final Label[] MODEL_LABELS = new Label[8];
    private static List<String> MODEL_SOURCE;
    private static int modelDisplayStartIndex;
    private static int modelSelectIndex;
    private static int modelFocusIndex;


    private static final Label[] ENGINE_LABELS = new Label[4];
    private static List<String> ENGINE_SOURCE;
    private static int engineDisplayStartIndex;
    private static int engineSelectIndex;
    private static int engineFocusIndex;

    private static TextField LOCK_FIELD;

    private static int category;
    private static String savedString = "";
    private static long lastAppendTime;

    static void initiate(
            VBox yearBox,
            Label yearLabel,
            VBox makeBox,
            Label makeLabel,
            VBox modelBox,
            Label modelLabel,
            VBox engineBox,
            Label engineLabel,
            TextField lock
    ) {
        MAKE_BOX = makeBox;
        MAKE_TITLE = makeLabel;
        MODEL_BOX = modelBox;
        MODEL_TITLE = modelLabel;
        ENGINE_BOX = engineBox;
        ENGINE_TITLE = engineLabel;
        LOCK_FIELD = lock;

        int index = 0;
        for (Node node : yearBox.getChildren())
            YEAR_LABELS[index++] = (Label) node;
        index = 0;
        for (Node node : MAKE_BOX.getChildren())
            MAKE_LABELS[index++] = (Label) node;
        index = 0;
        for (Node node : MODEL_BOX.getChildren())
            MODEL_LABELS[index++] = (Label) node;
        index = 0;
        for (Node node : ENGINE_BOX.getChildren())
            ENGINE_LABELS[index++] = (Label) node;
    }


    private static void setToBaseState() {
        MAKE_BOX.setOpacity(0);
        MAKE_TITLE.setOpacity(0);
        MODEL_BOX.setOpacity(0);
        MODEL_TITLE.setOpacity(0);
        ENGINE_BOX.setOpacity(0);
        ENGINE_TITLE.setOpacity(0);
        YEAR_SOURCE = Gateway.getVehicleYears();
        for (Label label : YEAR_LABELS) {
            label.setStyle("");
            label.setText("");
        }

        category = 0;
        yearDisplayStartIndex = 0;
        yearFocusIndex = 0;
        yearSelectIndex = 0;
        YEAR_LABELS[0].setStyle("-fx-background-color: darkgray");
        updateOnScroll();
    }

    public static void startRequest() {
        LOCK_FIELD.requestFocus();
        setToBaseState();
    }


    // Key press handlers
    static void reportKeyPress(KeyCode keyCode) {
        switch (keyCode) {
            case F12, ESCAPE -> {
                SceneController.setToPointOfSale();
                PointOfSale.refocus();
            }
            case UP -> {
                navUp();
                savedString = "";
            }
            case DOWN -> {
                navDown();
                savedString = "";
            }
            case ENTER, RIGHT -> {
                navRight();
                savedString = "";
            }
            case LEFT, BACK_SPACE -> {
                navLeft();
                savedString = "";
            }
            default -> smartSelect(keyCode);
        }
    }


    private static void smartSelect(KeyCode keyCode) {
        if (
                !keyCode.isDigitKey() &&
                        !keyCode.isLetterKey()
        ) return;

        switch (category) {
            case 0 -> {
                String append = keyCode.getName();
                if (append.length() > 1)
                    append = append.substring(append.length() - 1);

                if (savedString.length() == 2)
                    savedString = "";

                if (System.currentTimeMillis() - lastAppendTime > 2_000) {
                    savedString = "";
                }

                savedString += append;
                lastAppendTime = System.currentTimeMillis();

                if (savedString.length() == 0) {
                    savedString = "";
                    return;
                }

                if (!InputVerifier.isNumeric(savedString)) {
                    savedString = "";
                    return;
                }
                if (savedString.length() > 2) {
                    savedString = savedString.substring(savedString.length() - 2);
                }


                int saveFocusIndex = yearFocusIndex;
                int saveSelectIndex = yearSelectIndex;
                int saveStartIndex = yearDisplayStartIndex;
                if (savedString.length() == 1) {

                    // Try down

                    while (yearSelectIndex < YEAR_SOURCE.size()) {
                        if (YEAR_SOURCE.get(yearSelectIndex).charAt(2) == savedString.charAt(0)) {
                            updateOnScroll();
                            updateFocus();
                            return;
                        }
                        if (yearFocusIndex == YEAR_LABELS.length - 1)
                            yearDisplayStartIndex++;
                        else
                            yearFocusIndex++;
                        yearSelectIndex++;
                    }

                    yearFocusIndex = saveFocusIndex;
                    yearSelectIndex = saveSelectIndex;
                    yearDisplayStartIndex = saveStartIndex;

                    while (yearSelectIndex >= 0) {
                        if (YEAR_SOURCE.get(yearSelectIndex).charAt(2) == savedString.charAt(0)) {
                            updateOnScroll();
                            updateFocus();
                            return;
                        }
                        if (yearFocusIndex == 0)
                            yearDisplayStartIndex--;
                        else
                            yearFocusIndex--;
                        yearSelectIndex--;
                    }

                    yearFocusIndex = saveFocusIndex;
                    yearSelectIndex = saveSelectIndex;
                    yearDisplayStartIndex = saveStartIndex;

                } else {
                    // Try down

                    while (yearSelectIndex < YEAR_SOURCE.size()) {
                        if (YEAR_SOURCE.get(yearSelectIndex).substring(2, 4).equals(savedString)) {
                            updateOnScroll();
                            updateFocus();
                            return;
                        }
                        if (yearFocusIndex == YEAR_LABELS.length - 1)
                            yearDisplayStartIndex++;
                        else
                            yearFocusIndex++;
                        yearSelectIndex++;
                    }
                    yearFocusIndex = saveFocusIndex;
                    yearSelectIndex = saveSelectIndex;
                    yearDisplayStartIndex = saveStartIndex;

                    while (yearSelectIndex >= 0) {
                        if (YEAR_SOURCE.get(yearSelectIndex).substring(2, 4).equals(savedString)) {
                            updateOnScroll();
                            updateFocus();
                            return;
                        }
                        if (yearFocusIndex == 0)
                            yearDisplayStartIndex--;
                        else
                            yearFocusIndex--;
                        yearSelectIndex--;
                    }

                    yearFocusIndex = saveFocusIndex;
                    yearSelectIndex = saveSelectIndex;
                    yearDisplayStartIndex = saveStartIndex;
                    savedString = append;
                }

            }


            case 1 -> {
                String append = keyCode.getName().toUpperCase(Locale.ROOT);
                if (append.length() > 1)
                    append = append.substring(append.length() - 1);

                if (System.currentTimeMillis() - lastAppendTime > 5_000) {
                    savedString = "";
                }

                savedString += append;
                lastAppendTime = System.currentTimeMillis();

                int saveFocusIndex = makeFocusIndex;
                int saveSelectIndex = makeSelectIndex;
                int saveStartIndex = makeDisplayStartIndex;


                // Try Down
                while (makeSelectIndex < MAKE_SOURCE.size()) {
                    if (MAKE_SOURCE.get(makeSelectIndex)
                            .replaceAll("[^a-zA-Z0-9]", "")
                            .startsWith(savedString)) {
                        updateOnScroll();
                        updateFocus();
                        return;
                    }
                    if (makeFocusIndex == MAKE_LABELS.length - 1)
                        makeDisplayStartIndex++;
                    else
                        makeFocusIndex++;
                    makeSelectIndex++;
                }


                // Try up
                makeFocusIndex = saveFocusIndex;
                makeSelectIndex = saveSelectIndex;
                makeDisplayStartIndex = saveStartIndex;

                while (makeSelectIndex >= 0) {
                    if (MAKE_SOURCE.get(makeSelectIndex)
                            .replaceAll("[^a-zA-Z0-9]", "")
                            .startsWith(savedString)) {
                        updateOnScroll();
                        updateFocus();
                        return;
                    }
                    if (makeFocusIndex == 0)
                        makeDisplayStartIndex--;
                    else
                        makeFocusIndex--;
                    makeSelectIndex--;
                }

                makeFocusIndex = saveFocusIndex;
                makeSelectIndex = saveSelectIndex;
                makeDisplayStartIndex = saveStartIndex;
                savedString = append;
            }


            case 2 -> {
                String append = keyCode.getName().toUpperCase(Locale.ROOT);
                if (append.length() > 1)
                    append = append.substring(append.length() - 1);

                if (System.currentTimeMillis() - lastAppendTime > 5_000) {
                    savedString = "";
                }

                savedString += append;
                lastAppendTime = System.currentTimeMillis();

                int saveFocusIndex = modelFocusIndex;
                int saveSelectIndex = modelSelectIndex;
                int saveStartIndex = modelDisplayStartIndex;


                // Try Down
                while (modelSelectIndex < MODEL_SOURCE.size()) {
                    if (
                            MODEL_SOURCE.get(modelSelectIndex)
                                    .replaceAll("[^a-zA-Z0-9]", "")
                                    .startsWith(savedString)) {
                        updateOnScroll();
                        updateFocus();
                        return;
                    }
                    if (modelFocusIndex == MODEL_LABELS.length - 1)
                        modelDisplayStartIndex++;
                    else
                        modelFocusIndex++;
                    modelSelectIndex++;
                }


                // Try up
                modelFocusIndex = saveFocusIndex;
                modelSelectIndex = saveSelectIndex;
                modelDisplayStartIndex = saveStartIndex;

                while (modelSelectIndex >= 0) {
                    if (MODEL_SOURCE.get(modelSelectIndex)
                            .replaceAll("[^a-zA-Z0-9]", "")
                            .startsWith(savedString)) {
                        updateOnScroll();
                        updateFocus();
                        return;
                    }
                    if (modelFocusIndex == 0)
                        modelDisplayStartIndex--;
                    else
                        modelFocusIndex--;
                    modelSelectIndex--;
                }

                modelFocusIndex = saveFocusIndex;
                modelSelectIndex = saveSelectIndex;
                modelDisplayStartIndex = saveStartIndex;
                savedString = append;
            }

            case 3 -> {
                String append = keyCode.getName().toUpperCase(Locale.ROOT);

                if (append.length() > 1)
                    append = append.substring(append.length() - 1);

                if (System.currentTimeMillis() - lastAppendTime > 5_000) {
                    savedString = "";
                }

                savedString += append;
                lastAppendTime = System.currentTimeMillis();

                int saveFocusIndex = engineFocusIndex;
                int saveSelectIndex = engineSelectIndex;
                int saveStartIndex = engineDisplayStartIndex;


                // Try Down
                while (engineSelectIndex < ENGINE_SOURCE.size()) {
                    if (ENGINE_SOURCE.get(engineSelectIndex)
                            .replaceAll("[^a-zA-Z0-9]", "")
                            .startsWith(savedString)) {
                        updateOnScroll();
                        updateFocus();
                        return;
                    }
                    if (engineFocusIndex == ENGINE_LABELS.length - 1)
                        engineDisplayStartIndex++;
                    else
                        engineFocusIndex++;
                    engineSelectIndex++;
                }


                // Try up
                engineFocusIndex = saveFocusIndex;
                engineSelectIndex = saveSelectIndex;
                engineDisplayStartIndex = saveStartIndex;

                while (engineSelectIndex >= 0) {
                    if (ENGINE_SOURCE.get(engineSelectIndex)
                            .replaceAll("[^a-zA-Z0-9]", "")
                            .startsWith(savedString)) {
                        updateOnScroll();
                        updateFocus();
                        return;
                    }
                    if (engineFocusIndex == 0)
                        engineDisplayStartIndex--;
                    else
                        engineFocusIndex--;
                    engineSelectIndex--;
                }

                engineFocusIndex = saveFocusIndex;
                engineSelectIndex = saveSelectIndex;
                engineDisplayStartIndex = saveStartIndex;
                savedString = append;
            }

        }

    }


    private static void navRight() {


        switch (category) {
            case 0 -> {
                category = 1;
                MAKE_SOURCE = Gateway.getVehicleMakes(YEAR_SOURCE.get(yearSelectIndex));
                makeDisplayStartIndex = 0;
                makeFocusIndex = 0;
                makeSelectIndex = 0;
                for (Label label : MAKE_LABELS) {
                    label.setStyle("");
                    label.setText("");
                }
                MAKE_LABELS[0].setStyle("-fx-background-color: darkgray");
                MAKE_BOX.setOpacity(1);
                MAKE_TITLE.setOpacity(1);
                updateOnScroll();
            }

            case 1 -> {
                category = 2;
                MODEL_SOURCE = Gateway.getVehicleModels(
                        YEAR_SOURCE.get(yearSelectIndex), MAKE_SOURCE.get(makeSelectIndex)
                );
                modelDisplayStartIndex = 0;
                modelFocusIndex = 0;
                modelSelectIndex = 0;
                for (Label label : MODEL_LABELS) {
                    label.setStyle("");
                    label.setText("");
                }
                MODEL_LABELS[0].setStyle("-fx-background-color: darkgray");
                MODEL_BOX.setOpacity(1);
                MODEL_TITLE.setOpacity(1);
                updateOnScroll();
            }

            case 2 -> {
                category = 3;
                ENGINE_SOURCE = Gateway.getVehicleEngines(
                        YEAR_SOURCE.get(yearSelectIndex),
                        MAKE_SOURCE.get(makeSelectIndex),
                        MODEL_SOURCE.get(modelSelectIndex)
                );
                engineDisplayStartIndex = 0;
                engineFocusIndex = 0;
                engineSelectIndex = 0;
                for (Label label : ENGINE_LABELS) {
                    label.setText("");
                    label.setStyle("");
                }
                ENGINE_LABELS[0].setStyle("-fx-background-color: darkgray");
                ENGINE_BOX.setOpacity(1);
                ENGINE_TITLE.setOpacity(1);
                updateOnScroll();
            }

            case 3 -> {
                String description =
                        YEAR_SOURCE.get(yearSelectIndex) + " " +
                                MAKE_SOURCE.get(makeSelectIndex) + " " +
                                MODEL_SOURCE.get(modelSelectIndex);
                if (!ENGINE_SOURCE.get(engineSelectIndex).equalsIgnoreCase("I DON'T KNOW")) {
                    description += " ".concat(ENGINE_SOURCE.get(engineSelectIndex));
                }
                Header.applyVehicleDescription(description);
                SceneController.setToPointOfSale();
                setToBaseState();
            }
        }
    }

    private static void navLeft() {


        switch (category) {
            case 1 -> {
                category = 0;
                MAKE_BOX.setOpacity(0);
                MAKE_TITLE.setOpacity(0);
            }
            case 2 -> {
                category = 1;
                MODEL_BOX.setOpacity(0);
                MODEL_TITLE.setOpacity(0);
            }
            case 3 -> {
                category = 2;
                ENGINE_BOX.setOpacity(0);
                ENGINE_TITLE.setOpacity(0);
            }
        }

    }

    private static void navUp() {
        switch (category) {
            case 0 -> {
                if (yearSelectIndex == 0)
                    return;
                if (yearFocusIndex == 0) {
                    scrollUp();
                    return;
                }
                YEAR_LABELS[yearFocusIndex].setStyle("");
                yearFocusIndex--;
                yearSelectIndex--;
                YEAR_LABELS[yearFocusIndex].setStyle("-fx-background-color: darkgray");
            }

            case 1 -> {
                if (makeSelectIndex == 0)
                    return;
                if (makeFocusIndex == 0) {
                    scrollUp();
                    return;
                }
                MAKE_LABELS[makeFocusIndex].setStyle("");
                makeFocusIndex--;
                makeSelectIndex--;
                MAKE_LABELS[makeFocusIndex].setStyle("-fx-background-color: darkgray");
            }

            case 2 -> {
                if (modelSelectIndex == 0)
                    return;
                if (modelFocusIndex == 0) {
                    scrollUp();
                    return;
                }
                MODEL_LABELS[modelFocusIndex].setStyle("");
                modelFocusIndex--;
                modelSelectIndex--;
                MODEL_LABELS[modelFocusIndex].setStyle("-fx-background-color: darkgray");
            }

            case 3 -> {

                if (engineSelectIndex == 0)
                    return;
                if (engineFocusIndex == 0) {
                    scrollUp();
                    return;
                }
                ENGINE_LABELS[engineFocusIndex].setStyle("");
                engineFocusIndex--;
                engineSelectIndex--;
                ENGINE_LABELS[engineFocusIndex].setStyle("-fx-background-color: darkgray");
            }
        }
    }


    private static void navDown() {
        switch (category) {

            case 0 -> {
                if (yearSelectIndex == YEAR_SOURCE.size() - 1)
                    return;
                if (yearFocusIndex == YEAR_LABELS.length - 1) {
                    scrollDown();
                    return;
                }
                YEAR_LABELS[yearFocusIndex].setStyle("");
                yearFocusIndex++;
                yearSelectIndex++;
                YEAR_LABELS[yearFocusIndex].setStyle("-fx-background-color: darkgray");
            }

            case 1 -> {
                if (makeSelectIndex == MAKE_SOURCE.size() - 1)
                    return;
                if (makeFocusIndex == MAKE_LABELS.length - 1) {
                    scrollDown();
                    return;
                }
                MAKE_LABELS[makeFocusIndex].setStyle("");
                makeFocusIndex++;
                makeSelectIndex++;
                MAKE_LABELS[makeFocusIndex].setStyle("-fx-background-color: darkgray");
            }

            case 2 -> {
                if (modelSelectIndex == MODEL_SOURCE.size() - 1)
                    return;
                if (modelFocusIndex == MODEL_LABELS.length - 1) {
                    scrollDown();
                    return;
                }
                MODEL_LABELS[modelFocusIndex].setStyle("");
                modelFocusIndex++;
                modelSelectIndex++;
                MODEL_LABELS[modelFocusIndex].setStyle("-fx-background-color: darkgray");
            }

            case 3 -> {
                if (engineSelectIndex == ENGINE_SOURCE.size() - 1)
                    return;
                if (engineFocusIndex == ENGINE_LABELS.length - 1) {
                    scrollDown();
                    return;
                }
                ENGINE_LABELS[engineFocusIndex].setStyle("");
                engineFocusIndex++;
                engineSelectIndex++;
                ENGINE_LABELS[engineFocusIndex].setStyle("-fx-background-color: darkgray");
            }
        }
    }

    private static void scrollUp() {
        switch (category) {
            case 0 -> {
                yearSelectIndex--;
                yearDisplayStartIndex--;
            }
            case 1 -> {
                makeSelectIndex--;
                makeDisplayStartIndex--;
            }
            case 2 -> {
                modelSelectIndex--;
                modelDisplayStartIndex--;
            }
            case 3 -> {
                engineSelectIndex--;
                engineDisplayStartIndex--;
            }
        }
        updateOnScroll();
    }

    private static void scrollDown() {
        switch (category) {
            case 0 -> {
                yearSelectIndex++;
                yearDisplayStartIndex++;
            }
            case 1 -> {
                makeSelectIndex++;
                makeDisplayStartIndex++;
            }
            case 2 -> {
                modelSelectIndex++;
                modelDisplayStartIndex++;
            }
            case 3 -> {
                engineSelectIndex++;
                engineDisplayStartIndex++;
            }
        }
        updateOnScroll();
    }

    private static void updateFocus() {
        switch (category) {
            case 0 -> {
                for (Label label : YEAR_LABELS)
                    label.setStyle("");
                YEAR_LABELS[yearFocusIndex].setStyle("-fx-background-color: darkgray");
            }
            case 1 -> {
                for (Label label : MAKE_LABELS)
                    label.setStyle("");
                MAKE_LABELS[makeFocusIndex].setStyle("-fx-background-color: darkgray");
            }
            case 2 -> {
                for (Label label : MODEL_LABELS)
                    label.setStyle("");
                MODEL_LABELS[modelFocusIndex].setStyle("-fx-background-color: darkgray");
            }
            case 3 -> {
                for (Label label : ENGINE_LABELS)
                    label.setStyle("");
                ENGINE_LABELS[engineFocusIndex].setStyle("-fx-background-color: darkgray");
            }

        }
    }

    private static void updateOnScroll() {
        int labelIndex = 0;
        int startIndex;
        Label[] labels;
        List<String> source;

        switch (category) {
            case 1 -> {
                startIndex = makeDisplayStartIndex;
                labels = MAKE_LABELS;
                source = MAKE_SOURCE;
            }
            case 2 -> {
                startIndex = modelDisplayStartIndex;
                labels = MODEL_LABELS;
                source = MODEL_SOURCE;
            }
            case 3 -> {
                startIndex = engineDisplayStartIndex;
                labels = ENGINE_LABELS;
                source = ENGINE_SOURCE;
            }
            default -> {
                startIndex = yearDisplayStartIndex;
                labels = YEAR_LABELS;
                source = YEAR_SOURCE;
            }
        }

        for (int i = startIndex; labelIndex < labels.length; i++) {
            if (i == source.size())
                break;
            labels[labelIndex++].setText(source.get(i));
        }
    }

}
