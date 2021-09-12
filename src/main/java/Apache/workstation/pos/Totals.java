package Apache.workstation.pos;

import Apache.objects.InvoiceTotals;
import javafx.scene.control.Label;
import Apache.objects.Sequence;

import static Apache.util.General.cleanDouble;

public class Totals {

    static Label SUB_TOTAL;
    static Label TAX_TOTAL;
    static Label FREIGHT_LABEL;
    static Label FREIGHT_TOTAL;
    static Label GRAND_TOTAL;

    public static void initiate(
            Label subTotalLabel,
            Label taxTotalLabel,
            Label freightLabel,
            Label freightTotalLabel,
            Label grandTotalLabel
    ) {
        SUB_TOTAL = subTotalLabel;
        TAX_TOTAL = taxTotalLabel;
        FREIGHT_LABEL = freightLabel;
        FREIGHT_TOTAL = freightTotalLabel;
        GRAND_TOTAL = grandTotalLabel;
    }

    public static void reload() {
        Sequence sequence = ActiveSequence.getSequenceInstance();
        InvoiceTotals totals = sequence.getTotals();

        if (ActiveSequence.FREIGHT_TOTAL > 0) {
            FREIGHT_LABEL.setOpacity(1);
        } else {
            FREIGHT_LABEL.setOpacity(0);
        }

        FREIGHT_TOTAL.setText(cleanDouble(totals.getFreightTotal(), 2));
        SUB_TOTAL.setText(cleanDouble(totals.getSubTotal(), 2));
        TAX_TOTAL.setText(cleanDouble(totals.getTaxTotal(), 2));
        GRAND_TOTAL.setText(cleanDouble(totals.getGrandTotal(), 2));
    }

}
