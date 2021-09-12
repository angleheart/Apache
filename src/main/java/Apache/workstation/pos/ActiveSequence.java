package Apache.workstation.pos;

import Apache.config.Config;
import Apache.database.CounterPersonBase;
import Apache.database.InventoryBase;
import Apache.database.InvoiceBase;
import Apache.database.SequenceBase;
import Apache.invoicer.Invoicer;
import Apache.objects.*;
import Apache.http.Gateway;

import java.util.*;


public class ActiveSequence {

    static String SAVE_NAME;
    static String VEHICLE_DESCRIPTION;
    static String SHIP_TO;
    static String TRANS_CODE;
    static String PURCHASE_ORDER;
    static Customer CUSTOMER = null;
    static CounterPerson COUNTER_PERSON = null;
    static double FREIGHT_TOTAL;
    static Map<Integer, SequenceLine> LINES = new HashMap<>();

    public static boolean allowRelease() {
        return LINES.size() > 0;
    }

    static Sequence getSequenceInstance() {
        List<SequenceLine> lines = new ArrayList<>(LINES.values());

        return new Sequence(
                SAVE_NAME,
                CUSTOMER,
                COUNTER_PERSON,
                PURCHASE_ORDER,
                TRANS_CODE,
                VEHICLE_DESCRIPTION,
                SHIP_TO,
                FREIGHT_TOTAL,
                lines
        );
    }

    static void load(Sequence sequence) {
        startNew();
        SAVE_NAME = sequence.getSaveName();
        PURCHASE_ORDER = sequence.getPo();
        VEHICLE_DESCRIPTION = sequence.getVehicleDescription();
        SHIP_TO = sequence.getShipTo();
        TRANS_CODE = sequence.getTransCode();
        CUSTOMER = sequence.getCustomer();
        COUNTER_PERSON = CounterPersonBase.getCounterPersonByNumber(Integer.toString(sequence.getCounterPerson().getNumber()));
        FREIGHT_TOTAL = sequence.getFreightTotal();
        for (SequenceLine sequenceLine : sequence.getSequenceLines())
            LINES.put(sequenceLine.getIndexKey(), sequenceLine);

        Header.loadAll();
        Totals.reload();
        LineBody.reloadPage();
        LineBody.requestDown();
    }

    static void hold(String saveName) {
        SAVE_NAME = saveName;
        if (SequenceBase.holdSequence(getSequenceInstance())) {
            startNew();
        } else {
            Error.send("Failed to hold sequence");
            LineBody.reFocus();
        }
    }

    static void kill() {
        SequenceBase.killSequence(SAVE_NAME);
        startNew();
    }

    public static void release(int code) {
        long l;
        l = System.currentTimeMillis();
        Sequence sequence = getSequenceInstance();
        if (!SAVE_NAME.equals(""))
            SequenceBase.killSequence(SAVE_NAME);

        if (Config.STAND_ALONE) {
            Invoice invoice = sequence.releaseToInvoice(InvoiceBase.getInvoiceNumber(), code);
            if (!InvoiceBase.storeInvoice(invoice) ||
                    !InventoryBase.postInventoryUpdate(invoice) ||
                    !Invoicer.printInvoice(invoice)
            ) Error.send("Sorry, a database error occurred");
        } else {
            Gateway.postSequenceRelease(sequence, code);
        }

        System.out.println("Starting new elapsed " + (System.currentTimeMillis() - l) + "ms");
        startNew();
    }

    public static void startNew() {

        SAVE_NAME = "";
        VEHICLE_DESCRIPTION = "";
        SHIP_TO = "";
        PURCHASE_ORDER = "";
        TRANS_CODE = "";
        LINES = new HashMap<>();
        CUSTOMER = null;
        COUNTER_PERSON = null;
        FREIGHT_TOTAL = 0;

        Header.reset();
        LineBody.reset();
        Error.clear();
        Totals.reload();
        Header.focusCustomerNumberField();
    }

    static void putLine(SequenceLine sequenceLine) {
        LINES.put(sequenceLine.getIndexKey(), sequenceLine);
    }

    static void voidLine(int key) {
        LINES.get(key).voidSale();
    }


}