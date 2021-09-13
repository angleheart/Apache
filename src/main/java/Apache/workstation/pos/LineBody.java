package Apache.workstation.pos;

import Apache.config.Config;
import Apache.database.PartBase;
import Apache.database.SaleBase;
import Apache.http.Gateway;
import Apache.objects.Part;
import Apache.objects.Transferable;
import Apache.util.InputVerifier;
import Apache.util.TextFieldModifier;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import Apache.objects.SequenceLine;

import java.util.ArrayList;
import java.util.List;

import static Apache.util.General.*;

public class LineBody {

    private static final Node[][] DISPLAY_GRID = new Node[10][13];

    private static int FOCUS_ROW = -1;
    private static int FOCUS_COLUMN = 0;
    private static int FOCUS_PAGE = 0;
    private static String STORED_TEXT;

    public static boolean isFocusedHere() {
        return FOCUS_ROW > -1;
    }

    public static void initiate(
            VBox lineNumberLabels,
            VBox tranFields,
            VBox qtyFields,
            VBox mfgFields,
            VBox partNumberFields,
            VBox descriptionFields,
            VBox listFields,
            VBox unitFields,
            VBox extendedLabels,
            VBox txFields,
            VBox avlLabels,
            VBox voidLabels,
            VBox gpLabels
    ) {
        loadChildrenIntoColumn(0, lineNumberLabels.getChildren());
        loadChildrenIntoColumn(1, tranFields.getChildren());
        loadChildrenIntoColumn(2, qtyFields.getChildren());
        loadChildrenIntoColumn(3, mfgFields.getChildren());
        loadChildrenIntoColumn(4, partNumberFields.getChildren());
        loadChildrenIntoColumn(5, descriptionFields.getChildren());
        loadChildrenIntoColumn(6, listFields.getChildren());
        loadChildrenIntoColumn(7, unitFields.getChildren());
        loadChildrenIntoColumn(8, extendedLabels.getChildren());
        loadChildrenIntoColumn(9, txFields.getChildren());
        loadChildrenIntoColumn(10, avlLabels.getChildren());
        loadChildrenIntoColumn(11, voidLabels.getChildren());
        loadChildrenIntoColumn(12, gpLabels.getChildren());
        for (int row = 0; row < 10; row++) {
            for (int col = 2; col < 10; col++) {
                if (col == 8)
                    continue;
                TextFieldModifier.requireCaps((TextField) DISPLAY_GRID[row][col]);
                DISPLAY_GRID[row][col].addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.TAB) {
                        event.consume();
                    }
                });
            }
        }
    }


    private static void loadChildrenIntoColumn(int col, ObservableList<Node> children) {
        int row = 0;
        for (Node node : children) {
            DISPLAY_GRID[row][col] = node;
            row++;
        }
    }

    public static void reportF8() {
        if (atNewLine() && FOCUS_COLUMN < 5)
            return;
        String mfg = targetLineGetMfg();
        String partNumber = targetLineGetPartNumber();
        String customerNumber = ActiveSequence.CUSTOMER.getNumber();
        List<Transferable> pp = SaleBase.getPastPurchases(
                customerNumber,
                mfg,
                partNumber
        );
        if (pp == null) {
            Error.send("A Apache.database error occurred");
            System.out.println("Past purchases returned null");
            return;
        }
        if (pp.size() == 0) {
            Error.send("No past purchases found");
            return;
        }
        SelectionMenu.performRequest(SelectionMenuType.PAST_PURCHASE, pp);
    }

    public static void postF8(PastPurchase pastPurchase) {
        targetLineSetUnit(cleanDouble(pastPurchase.getUnitPrice(), 3));
        if (atNewLine()) {
            targetLineComplete();
            targetLineSave();
            approveDown();
        } else {
            targetLineSave();
            reFocus();
        }
    }

    public static void reportGenericKey() {

    }

    public static boolean allowCalculator() {
        if (!isFocusedHere())
            return false;
        if (!atNewLine())
            return true;
        else return FOCUS_COLUMN == 7;
    }

    public static void applyNewUnitPrice(String unitPrice) {
        targetLineSetUnit(unitPrice);
        if (!atNewLine()) {
            if (targetLineNeedsListReload())
                targetLineReloadList();
            targetLineSave();
            FOCUS_COLUMN = 7;
        }
        reFocus();
    }

    public static void applyPart(Part part) {
        targetLineSetMfg(part.getLineCode());
        targetLineSetPartNumber(part.getPartNumber());
        targetLineSetDescription(part.getDescription());
        targetLineSetUnit(cleanDouble(part.getCost() * ActiveSequence.CUSTOMER.getPriceMultiplier(), 3));
        targetLineSetAvl(
                Integer.toString(PartBase.getAvailableQuantity(targetLineGetMfg(), targetLineGetPartNumber()))
        );
        targetLineSetGp(
                PartBase.getGp(
                        targetLineGetMfg(),
                        targetLineGetPartNumber(),
                        targetLineGetUnit()
                )
        );
        FOCUS_COLUMN += 3;
        reFocus();
    }

    public static void applyCore(String price) {
        if (InputVerifier.verifyPrice(price)) {
            Error.clear();
            double corePrice = Double.parseDouble(price);
            SequenceLine lastSequenceLine = ActiveSequence.LINES.get(getLineKey() - 1);
            int quantity = lastSequenceLine.getQty();
            String mfg = lastSequenceLine.getMfg();
            String partNumber = lastSequenceLine.getPartNumber();

            targetLineSetQuantity(Integer.toString(quantity));
            targetLineSetMfg(mfg.substring(0, 2).concat("9"));
            targetLineSetPartNumber(partNumber);
            targetLineSetUnit(cleanDouble(corePrice, 3));
            targetLineSetDescription("CORE");
            targetLineComplete();
            targetLineSave();
            approveDown();

            targetLineSetQuantity(Integer.toString(quantity * -1));
            targetLineSetMfg(mfg.substring(0, 2).concat("9"));
            targetLineSetPartNumber(partNumber);
            targetLineSetUnit(cleanDouble(corePrice, 3));
            targetLineSetDescription("CORE");
            targetLineComplete();
            targetLineSetTx("N");
            targetLineSave();
            approveDown();
        } else {
            targetBoxSetText(STORED_TEXT);
            Error.send("Invalid core price");
            reFocus();
        }
    }

    /**
     * Handle Navigation Requests
     * <p>
     * These methods are used to either grant or
     * deny permission to navigate around the grid.
     * <p>
     * Use approve methods after passing all tests.
     * Send errors to user if invalid entry is given
     */

    static void requestCarriageReturn() {
        String entry = targetBoxGetText();
        if (atNewLine()) {
            if (FOCUS_COLUMN == 3) {
                if (entry.trim().equals("")) {
                    targetLineSetMfg("*");
                    targetLineSetQuantity("1");
                    reFocus();
                    return;
                }
                if (entry.equals("K")) {
                    ActiveSequence.kill();
                    return;
                }
                if (entry.equals("H")) {
                    targetBoxSetText(STORED_TEXT);
                    InputMenu.performRequest(InputMenuType.SEQUENCE);
                    return;
                }
                if (entry.equals("END")) {
                    ActiveSequence.kill();
                    return;
                }
                if (entry.equals("CORE") && ActiveSequence.LINES.size() > 0) {
                    targetLineSetMfg(STORED_TEXT);
                    InputMenu.performRequest(InputMenuType.CORE);
                    return;
                }
                if (InputVerifier.verifyQty(targetLineGetMfg(), ActiveSequence.TRANS_CODE)) {
                    targetLineSetQuantity(entry);
                    targetLineSetMfg("*");
                    reFocus();
                    return;
                }

            }

            if (FOCUS_COLUMN == 7) {
                if (tryPriceSubmit(entry)) {
                    targetLineComplete();
                    targetLineSave();
                    FOCUS_COLUMN = 3;
                    approveDown();
                }
            } else
                requestRight();

        } else if (targetBoxChanged()) {
            switch (FOCUS_COLUMN) {
                case 2:
                    if (tryQuantitySubmit(entry)) {
                        targetLineSave();
                        FOCUS_COLUMN = 3;
                        approveDown();
                    }
                    break;
                case 3:
                    switch (entry) {
                        case "V":
                            targetLineVoid();
                            targetBoxSetText(STORED_TEXT);
                            FOCUS_COLUMN = 3;
                            approveDown();
                            break;
                        case "K":
                            ActiveSequence.kill();
                            break;
                        case "H":
                            targetBoxSetText(STORED_TEXT);
                            InputMenu.performRequest(InputMenuType.SEQUENCE);
                            break;
                        case "END":
                            ActiveSequence.startNew();
                            break;
                        default:
                            if (tryQuantitySubmit(entry)) {
                                targetLineSetQuantity(targetBoxGetText());
                                targetBoxReset();
                                targetLineSave();
//                                FOCUS_COLUMN = 3;
//                                approveDown();
                                reFocus();
                            }
                    }
                    break;
                case 6:
                    if (tryPriceSubmit(entry)) {
                        targetLineSetList(cleanDouble(Double.parseDouble(targetLineGetList()), 3));
                        targetLineSave();
//                        FOCUS_COLUMN = 3;
//                        approveDown();
                        reFocus();
                    }
                    break;
                case 7:
                    if (tryPriceSubmit(entry)) {
                        targetLineSetUnit(cleanDouble(Double.parseDouble(targetLineGetUnit()), 3));
                        targetLineSave();
                        if (targetLineNeedsListReload())
                            targetLineReloadList();
//                        FOCUS_COLUMN = 3;
//                        approveDown();
                        reFocus();
                    }
                    break;
                case 9:
                    if (tryTxSubmit(entry)) {
                        targetLineSave();
//                        FOCUS_COLUMN = 3;
//                        approveDown();
                        reFocus();
                    }
            }
        } else {
            FOCUS_COLUMN = 3;
            approveDown();
        }
    }


    static void requestUp() {
        if (atNewLine()) {
            targetLineClear();
            targetLinePrintNew();
            FOCUS_COLUMN = 3;
            approveUp();

        } else if (targetBoxChanged()) {
            String entry = targetBoxGetText();
            switch (FOCUS_COLUMN) {
                case 2:
                    if (tryQuantitySubmit(entry)) {
                        targetLineSave();
                        approveUp();
                    }
                    break;
                case 3:
                    targetBoxReset();
                    approveUp();
                    break;
                case 6:
                    if (tryPriceSubmit(entry)) {
                        double list = Double.parseDouble(entry);
                        targetLineSetList(cleanDouble(list, 3));
                        targetLineSave();
                        approveUp();
                    }
                    break;
                case 7:
                    if (tryPriceSubmit(entry)) {
                        double unit = Double.parseDouble(entry);
                        targetLineSetUnit(cleanDouble(unit, 3));
                        targetLineSave();
                        if (targetLineNeedsListReload())
                            targetLineReloadList();
                        approveUp();
                    }
                    break;
                case 9:
                    if (tryTxSubmit(entry)) {
                        targetLineSave();
                        approveUp();
                    }
            }
        } else {
            approveUp();
        }
    }

    static void requestDown() {
        Header.customerLocked = true;
        if (FOCUS_ROW == -1) {
            FOCUS_PAGE = 0;
            FOCUS_ROW = 0;
            FOCUS_COLUMN = 3;
            if (atNewLine())
                targetLinePrintNew();
            else if (ActiveSequence.LINES.get(getLineKey()).isVoided())
                approveDown();
            reFocus();
            return;
        }

        String entry = targetBoxGetText();
        if (atNewLine()) {
            if (FOCUS_COLUMN == 7)
                requestCarriageReturn();
            else
                requestRight();
        } else if (targetBoxChanged()) {
            switch (FOCUS_COLUMN) {
                case 2:
                    if (tryQuantitySubmit(entry)) {
                        targetLineSave();
                        approveDown();
                    }
                    break;
                case 3:
                    targetBoxReset();
                    approveDown();
                    break;
                case 6:
                    if (tryPriceSubmit(entry)) {
                        targetLineSetList(cleanDouble(Double.parseDouble(targetLineGetList()), 3));
                        targetLineSave();
                        approveDown();
                    }
                    break;
                case 7:
                    if (tryPriceSubmit(entry)) {
                        targetLineSave();
                        if (targetLineNeedsListReload())
                            targetLineReloadList();
                        approveDown();
                    }
                    break;
                case 9:
                    if (tryTxSubmit(entry)) {
                        targetLineSave();
                        approveDown();
                    }
            }
        } else {
            approveDown();
        }
    }

    static void requestRight() {
        if (FOCUS_COLUMN == 9) {
            reFocus();
            return;
        }

        String entry = targetBoxGetText();
        if (atNewLine()) {
            switch (FOCUS_COLUMN) {
                case 2:
                    if (tryQuantitySubmit(entry)) {
                        targetLineSetMfg("*");
                        approveRight();
                    }
                    break;
                case 3:
                    if (entry.equalsIgnoreCase("*") || tryMfgSubmit(entry))
                        approveRight();
                    break;
                case 4:
                    if (tryPartNumberSubmit(entry)) {

                        if (Config.STAND_ALONE) {
                            if (targetLineGetMfg().equals("*")) {

                                List<Transferable> parts = PartBase.getPartsByNumber(targetLineGetPartNumber());

                                if (parts.size() == 0) {
                                    targetLineSetMfg(PartBase.inferMfg(entry));
                                    targetLineSetDescription(
                                            PartBase.inferDescription(targetLineGetMfg(), targetLineGetPartNumber())
                                    );
                                    approveRight();
                                    return;
                                }

                                if (parts.size() == 1) {
                                    Part part = (Part) parts.get(0);
                                    targetLineSetMfg(part.getLineCode());
                                    targetLineSetPartNumber(part.getPartNumber());
                                    targetLineSetDescription(part.getDescription());
                                    targetLineSetUnit(
                                            cleanDouble(
                                                    part.getCost() *
                                                            ActiveSequence.CUSTOMER.getPriceMultiplier(),
                                                    3)
                                    );
                                    targetLineSetAvl(
                                            Integer.toString(PartBase.getAvailableQuantity(targetLineGetMfg(),
                                                    targetLineGetPartNumber()))
                                    );
                                    targetLineSetGp(
                                            PartBase.getGp(
                                                    targetLineGetMfg(),
                                                    targetLineGetPartNumber(),
                                                    targetLineGetUnit()
                                            )
                                    );
                                    FOCUS_COLUMN += 3;
                                    reFocus();
                                    return;
                                }

                                SelectionMenu.performRequest(SelectionMenuType.PART, parts);

                            } else {
                                Part part = PartBase.tryToCorrectPartNumber(targetLineGetMfg(),
                                        targetLineGetPartNumber());
                                if (part == null) {

                                    targetLineSetDescription(
                                            PartBase.inferDescription(targetLineGetMfg(),
                                                    targetLineGetPartNumber())
                                    );
                                    approveRight();
                                } else {
                                    targetLineSetPartNumber(part.getPartNumber());
                                    targetLineSetDescription(part.getDescription());
                                    targetLineSetUnit(
                                            cleanDouble(
                                                    part.getCost() *
                                                            ActiveSequence.CUSTOMER.getPriceMultiplier(),
                                                    3)
                                    );
                                    targetLineSetAvl(
                                            Integer.toString(PartBase.getAvailableQuantity(targetLineGetMfg(),
                                                    targetLineGetPartNumber()))
                                    );
                                    targetLineSetGp(
                                            PartBase.getGp(
                                                    targetLineGetMfg(),
                                                    targetLineGetPartNumber(),
                                                    targetLineGetUnit()
                                            )
                                    );
                                    FOCUS_COLUMN += 3;
                                    reFocus();
                                }
                            }


                        } else {
                            String mfgRequest = targetLineGetMfg();
                            String numberRequest = targetLineGetPartNumber();
                            List<Object> parts = Gateway.queryParts(mfgRequest, numberRequest);

                            if(parts == null){
                                Error.send("A server error occurred");
                                reFocus();
                                return;
                            }
                            else if(parts.size() == 0){
                                if(mfgRequest.equals("*"))
                                    targetLineSetMfg("MIS");
                                targetLineSetDescription("Part");
                                approveRight();
                                return;
                            } else if(parts.size() == 1){
                                Part part = (Part)parts.get(0);
                                applyPart(part);
                                return;
                            }else{
                                List<Transferable> selectableParts = new ArrayList<>();
                                for(Object o : parts)
                                    selectableParts.add((Part) o);
                                SelectionMenu.performRequest(SelectionMenuType.PART, selectableParts);
                                return;
                            }
                        }
                        return;
                    }
                    break;
                case 5:
                    if (tryDescriptionSubmit(entry)) {
                        approveRight();
                    }
                    break;
                case 7:
                    requestCarriageReturn();
            }
        } else if (targetBoxChanged()) {
            switch (FOCUS_COLUMN) {
                case 2:
                case 3:
                    targetBoxReset();
                    approveRight();
                    break;
                case 6:
                    if (tryPriceSubmit(entry)) {
                        targetLineSave();
                        approveRight();
                    }
                    break;
                case 7:
                    if (tryPriceSubmit(entry)) {
                        targetLineSave();
                        if (targetLineNeedsListReload())
                            targetLineReloadList();
                        approveRight();
                    }
                    break;
            }
        } else {
            approveRight();
        }
    }

    static void requestLeft() {
        if (FOCUS_COLUMN == 2) {
            reFocus();
            return;
        }

        String entry = targetBoxGetText();

        if (atNewLine()) {
            if (FOCUS_COLUMN == 4) {
                targetBoxClear();
            }

            if (FOCUS_COLUMN == 5) {
                targetBoxClear();
                targetLineSetUnit("");
                targetLineSetList("");
                targetLineSetAvl("");
                targetLineSetGp("");
            }

            if (FOCUS_COLUMN == 7) {
                if (!InputVerifier.verifyPrice(entry)) {
                    Error.send("Invalid price");
                    targetBoxSetText(STORED_TEXT);
                    reFocus();
                    return;
                }
                FOCUS_COLUMN -= 2;
                reFocus();
                return;
            }

            approveLeft();

            if (FOCUS_COLUMN == 3) {
                targetBoxSetText("*");
                reFocus();
            }

        } else if (targetBoxChanged()) {
            switch (FOCUS_COLUMN) {
                case 3:
                    targetBoxReset();
                    approveLeft();
                    break;
                case 6:
                    if (tryPriceSubmit(entry)) {
                        targetLineSave();
                        approveLeft();
                    }
                    break;
                case 7:
                    if (tryPriceSubmit(entry)) {
                        targetLineSave();
                        if (targetLineNeedsListReload())
                            targetLineReloadList();
                        approveLeft();
                    }
                    break;
                case 9:
                    if (tryTxSubmit(entry)) {
                        targetLineSave();
                        approveLeft();
                    }
            }
        } else {
            approveLeft();
        }
    }


    /**
     * Try Submission Methods
     * <p>
     * These methods will attempt to submit the value provided
     * They will return true WILL NOT SAVE
     * <p>
     * They will send error and clear box if illegal
     */


    private static boolean tryQuantitySubmit(String entry) {
        if (InputVerifier.verifyQty(entry, ActiveSequence.TRANS_CODE)) {
            if (Integer.parseInt(entry) < 0)
                targetLineSetTransCode("RET");
            else if (Integer.parseInt(entry) > 0 && ActiveSequence.TRANS_CODE.equals("SAL"))
                targetLineSetTransCode("SAL");
            return true;
        } else {
            Error.send("Invalid order quantity");
            targetBoxReset();
            reFocus();
            return false;
        }
    }

    private static boolean tryMfgSubmit(String entry) {
        if (InputVerifier.verifyMfg(entry)) {
            return true;
        } else {
            Error.send("Invalid mfg code");
            targetBoxReset();
            reFocus();
            return false;
        }
    }

    private static boolean tryPartNumberSubmit(String entry) {
        if (InputVerifier.verifyPartNumber(entry)) {
            return true;
        } else {
            Error.send("Invalid part number");
            targetBoxReset();
            reFocus();
            return false;
        }
    }


    private static boolean tryDescriptionSubmit(String entry) {
        if (InputVerifier.verifyDescription(entry)) {
            return true;
        } else {
            Error.send("Invalid description");
            targetBoxReset();
            reFocus();
            return false;
        }
    }

    private static boolean tryPriceSubmit(String entry) {
        if (InputVerifier.verifyPrice(entry)) {
            targetBoxSetText(cleanDouble(Double.parseDouble(entry), 3));
            return true;
        } else {
            Error.send("Invalid price");
            targetBoxReset();
            reFocus();
            return false;
        }
    }

    private static boolean tryTxSubmit(String entry) {
        if (InputVerifier.verifyTx(entry)) {
            return true;
        } else {
            Error.send("Invalid tax code");
            targetBoxReset();
            reFocus();
            return false;
        }
    }


    /**
     * Navigation Approval Methods
     * <p>
     * These methods are used to navigate around
     * the grid. You should always approve before
     * calling.
     */


    private static void approvePageUp() {
        if (FOCUS_PAGE == 0) {
            Header.enterFromLineBody();
            FOCUS_ROW = -1;
            return;
        }

        FOCUS_PAGE--;
        FOCUS_ROW = 9;
        reloadPage();

        if (ActiveSequence.LINES.get(getLineKey()).isVoided()) {
            approveUp();
            return;
        }

        reFocus();
    }

    private static void approvePageDown() {
        FOCUS_PAGE++;
        FOCUS_ROW = 0;
        reloadPage();

        if (atNewLine()) {
            targetLinePrintNew();
            FOCUS_COLUMN = 3;
        } else if (ActiveSequence.LINES.get(getLineKey()).isVoided()) {
            approveDown();
            return;
        }

        reFocus();
    }

    private static void approveUp() {
        Error.clear();
        if (FOCUS_ROW == 0) {
            approvePageUp();
            return;
        }

        FOCUS_ROW--;

        if (ActiveSequence.LINES.get(getLineKey()).isVoided()) {
            approveUp();
            return;
        }

        reFocus();
    }

    private static void approveDown() {
        Error.clear();
        if (FOCUS_ROW == 9) {
            approvePageDown();
            return;
        }

        FOCUS_ROW++;

        if (atNewLine()) {
            targetLinePrintNew();
            FOCUS_COLUMN = 3;
        } else if (ActiveSequence.LINES.get(getLineKey()).isVoided()) {
            approveDown();
            return;
        }

        reFocus();
    }

    private static void approveRight() {
        Error.clear();
        if (FOCUS_COLUMN == 9)
            return;
        if (atNewLine()) {
            if (FOCUS_COLUMN == 5)
                FOCUS_COLUMN++;
        } else {
            if (FOCUS_COLUMN == 3)
                FOCUS_COLUMN += 2;
            if (FOCUS_COLUMN == 7)
                FOCUS_COLUMN++;
        }
        FOCUS_COLUMN++;
        reFocus();
    }

    private static void approveLeft() {
        Error.clear();
        if (FOCUS_COLUMN == 2)
            return;
        if (!atNewLine()) {
            if (FOCUS_COLUMN == 6)
                FOCUS_COLUMN -= 2;
            if (FOCUS_COLUMN == 9)
                FOCUS_COLUMN--;
        }
        FOCUS_COLUMN--;
        reFocus();
    }


    /**
     * Target Methods
     * <p>
     * These methods are used to perform
     * operations on the currently selected
     * position in the display grid.
     */

    private static boolean targetBoxChanged() {
        return !targetBoxGetText().equalsIgnoreCase(STORED_TEXT);
    }

    private static void targetBoxReset() {
        ((TextField) DISPLAY_GRID[FOCUS_ROW][FOCUS_COLUMN]).setText(STORED_TEXT);
    }

    private static void targetBoxClear() {
        ((TextField) DISPLAY_GRID[FOCUS_ROW][FOCUS_COLUMN]).clear();
    }


    private static void targetLineClear() {
        for (int col = 1; col < 11; col++) {
            if (col == 8 || col == 10)
                ((Label) DISPLAY_GRID[FOCUS_ROW][col]).setText("");
            else
                ((TextField) DISPLAY_GRID[FOCUS_ROW][col]).clear();
        }
    }

    private static void targetLineComplete() {
        double unit = Double.parseDouble(targetLineGetUnit());
        double list = unit * Config.LIST_PRICE_MULTIPLIER;
        targetLineSetList(cleanDouble(list, 3));
        String tx;
        if (ActiveSequence.CUSTOMER.isTaxable())
            tx = "T";
        else
            tx = "N";
        targetLineSetTx(tx);
        targetLineSetAvl(
                Integer.toString(PartBase.getAvailableQuantity(targetLineGetMfg(), targetLineGetPartNumber()))
        );
        targetLineSetGp(
                PartBase.getGp(
                        targetLineGetMfg(),
                        targetLineGetPartNumber(),
                        targetLineGetUnit()
                )
        );
        if (Integer.parseInt(targetLineGetQuantity()) < 0)
            targetLineSetTransCode("RET");
    }

    private static void targetLineRecalculateExtended() {
        double extended = Double.parseDouble(targetLineGetUnit());
        extended = extended * Integer.parseInt(targetLineGetQuantity());
        if (ActiveSequence.TRANS_CODE.equalsIgnoreCase("RET"))
            extended *= -1;
        targetLineSetExtended(cleanDouble(extended, 2));
        targetLineSetGp(
                PartBase.getGp(
                        targetLineGetMfg(),
                        targetLineGetPartNumber(),
                        targetLineGetUnit()
                )
        );
    }

    private static void targetLineSave() {
        SequenceLine newSequenceLine = new SequenceLine(
                getLineKey(),
                targetLineGetTransCode(),
                Integer.parseInt(targetLineGetQuantity()),
                targetLineGetMfg(),
                targetLineGetPartNumber(),
                targetLineGetDescription(),
                Double.parseDouble(targetLineGetList()),
                Double.parseDouble(targetLineGetUnit()),
                targetLineGetTx()
        );
        ActiveSequence.putLine(newSequenceLine);
        targetLineRecalculateExtended();
        Totals.reload();
    }

    private static String targetBoxGetText() {
        return ((TextField) DISPLAY_GRID[FOCUS_ROW][FOCUS_COLUMN]).getText();
    }

    private static void targetBoxSetText(String text) {
        ((TextField) DISPLAY_GRID[FOCUS_ROW][FOCUS_COLUMN]).setText(text);
    }


    private static String targetLineGetTransCode() {
        return ((TextField) DISPLAY_GRID[FOCUS_ROW][1]).getText();
    }

    private static String targetLineGetQuantity() {
        return ((TextField) DISPLAY_GRID[FOCUS_ROW][2]).getText();
    }

    private static String targetLineGetMfg() {
        return ((TextField) DISPLAY_GRID[FOCUS_ROW][3]).getText();
    }

    private static String targetLineGetPartNumber() {
        return ((TextField) DISPLAY_GRID[FOCUS_ROW][4]).getText();
    }

    private static String targetLineGetDescription() {
        return ((TextField) DISPLAY_GRID[FOCUS_ROW][5]).getText();
    }

    private static String targetLineGetList() {
        return ((TextField) DISPLAY_GRID[FOCUS_ROW][6]).getText();
    }

    private static String targetLineGetUnit() {
        return ((TextField) DISPLAY_GRID[FOCUS_ROW][7]).getText();
    }

    private static String targetLineGetTx() {
        return ((TextField) DISPLAY_GRID[FOCUS_ROW][9]).getText();
    }


    private static void targetLineSetTransCode(String text) {
        ((TextField) DISPLAY_GRID[FOCUS_ROW][1]).setText(text);
    }

    private static void targetLineSetQuantity(String text) {
        ((TextField) DISPLAY_GRID[FOCUS_ROW][2]).setText(text);
    }

    private static void targetLineSetMfg(String text) {
        ((TextField) DISPLAY_GRID[FOCUS_ROW][3]).setText(text);
    }

    private static void targetLineSetPartNumber(String text) {
        ((TextField) DISPLAY_GRID[FOCUS_ROW][4]).setText(text);
    }

    private static void targetLineSetDescription(String text) {
        ((TextField) DISPLAY_GRID[FOCUS_ROW][5]).setText(text);
    }

    private static void targetLineSetList(String text) {
        ((TextField) DISPLAY_GRID[FOCUS_ROW][6]).setText(text);
    }

    private static void targetLineSetUnit(String text) {
        ((TextField) DISPLAY_GRID[FOCUS_ROW][7]).setText(text);
    }

    private static void targetLineSetExtended(String text) {
        ((Label) DISPLAY_GRID[FOCUS_ROW][8]).setText(text);
    }

    private static void targetLineSetTx(String text) {
        ((TextField) DISPLAY_GRID[FOCUS_ROW][9]).setText(text);
    }

    private static void targetLineSetAvl(String text) {
        ((Label) DISPLAY_GRID[FOCUS_ROW][10]).setText(text);
    }

    private static void targetLineSetGp(String text) {
        ((Label) DISPLAY_GRID[FOCUS_ROW][12]).setText(text);
    }

    private static void targetLineVoid() {
        DISPLAY_GRID[FOCUS_ROW][11].setOpacity(1);
        ActiveSequence.voidLine(getLineKey());
        Totals.reload();
    }

    private static void targetLineReloadList() {
        double unit = Double.parseDouble(targetLineGetUnit());
        double list = unit * Config.LIST_PRICE_MULTIPLIER;
        targetLineSetList(cleanDouble(list, 3));
    }

    private static boolean targetLineNeedsListReload() {
        try {
            double unit = Double.parseDouble(targetLineGetUnit());
            double list = Double.parseDouble(targetLineGetList());
            if (list < unit * Config.LIST_PRICE_MULTIPLIER)
                return true;
        } catch (Exception e) {
            return true;
        }
        return false;
    }


    /**
     * Utility Methods
     * <p>
     * These methods are used for basic utility
     * for the LineBody
     */
    static void reset() {
        FOCUS_PAGE = 0;
        reloadPage();
        FOCUS_ROW = -1;
        FOCUS_COLUMN = 0;
        FOCUS_PAGE = 0;
    }

    private static void clearPage() {
        for (int row = 0; row < 10; row++) {
            for (int col = 1; col < 13; col++) {
                if (col == 8 || col == 10 || col == 12)
                    ((Label) DISPLAY_GRID[row][col]).setText("");
                else if (col == 11)
                    DISPLAY_GRID[row][col].setOpacity(0);
                else
                    ((TextField) DISPLAY_GRID[row][col]).clear();
            }
        }
    }

    public static void reloadPage() {
        clearPage();
        for (int row = 0; row < 10; row++) {
            int lineNum = FOCUS_PAGE * 10 + row + 1;
            String lineNumStr = Integer.toString(lineNum);
            if (lineNum < 10)
                lineNumStr = "0".concat(lineNumStr);
            ((Label) DISPLAY_GRID[row][0]).setText(lineNumStr);
        }
        int row = 0;
        for (int startLineKey = FOCUS_PAGE * 10; row < 10; row++) {
            if (!ActiveSequence.LINES.containsKey(startLineKey))
                break;
            SequenceLine loadSequenceLine = ActiveSequence.LINES.get(startLineKey);
            ((TextField) DISPLAY_GRID[row][1]).setText(loadSequenceLine.getTransCode());
            ((TextField) DISPLAY_GRID[row][2]).setText(Integer.toString(loadSequenceLine.getQty()));
            ((TextField) DISPLAY_GRID[row][3]).setText(loadSequenceLine.getMfg());
            ((TextField) DISPLAY_GRID[row][4]).setText(loadSequenceLine.getPartNumber());
            ((TextField) DISPLAY_GRID[row][5]).setText(loadSequenceLine.getDescription());
            ((TextField) DISPLAY_GRID[row][6]).setText(cleanDouble(loadSequenceLine.getListPrice(), 3));
            ((TextField) DISPLAY_GRID[row][7]).setText(cleanDouble(loadSequenceLine.getUnitPrice(), 3));
            ((Label) DISPLAY_GRID[row][8]).setText(cleanDouble(loadSequenceLine.getExtendedPrice(), 2));
            ((TextField) DISPLAY_GRID[row][9]).setText(loadSequenceLine.getTx());
            ((Label) DISPLAY_GRID[row][10]).setText(Integer.toString(
                    PartBase.getAvailableQuantity(loadSequenceLine.getMfg(), loadSequenceLine.getPartNumber())));

            ((Label) DISPLAY_GRID[row][12]).setText(
                    PartBase.getGp(
                            loadSequenceLine.getMfg(),
                            loadSequenceLine.getPartNumber(),
                            Double.toString(loadSequenceLine.getUnitPrice())
                    )
            );
            if (loadSequenceLine.isVoided())
                DISPLAY_GRID[row][11].setOpacity(1);
            else
                DISPLAY_GRID[row][11].setOpacity(0);
            startLineKey++;
        }
    }

    private static void targetLinePrintNew() {
        ((TextField) DISPLAY_GRID[FOCUS_ROW][1]).setText(ActiveSequence.TRANS_CODE);
        ((TextField) DISPLAY_GRID[FOCUS_ROW][2]).setText("1");
        ((TextField) DISPLAY_GRID[FOCUS_ROW][3]).setText("*");
    }

    private static int getLineKey() {
        return FOCUS_PAGE * 10 + FOCUS_ROW;
    }


    public static boolean atNewLine() {
        return !ActiveSequence.LINES.containsKey(FOCUS_PAGE * 10 + FOCUS_ROW);
    }

    static void reFocus() {
        STORED_TEXT = ((TextField) DISPLAY_GRID[FOCUS_ROW][FOCUS_COLUMN]).getText();
        DISPLAY_GRID[0][0].requestFocus();
        DISPLAY_GRID[FOCUS_ROW][FOCUS_COLUMN].requestFocus();
    }


}
