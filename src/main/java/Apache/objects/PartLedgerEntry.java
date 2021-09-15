package Apache.objects;

import java.text.SimpleDateFormat;
import java.util.Date;

import static Apache.util.General.cleanDouble;

public class PartLedgerEntry implements Selectable {

    private final int invoiceNumber;
    private final long time;
    private final String customerNumber;
    private final String transCode;
    private final int quantity;
    private final double unitPrice;

    public PartLedgerEntry(
            int invoiceNumber,
            long time,
            String customerNumber,
            String transCode,
            int quantity,
            double unitPrice
    ) {
        this.invoiceNumber = invoiceNumber;
        this.time = time;
        this.customerNumber = customerNumber;
        this.transCode = transCode;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    @Override
    public String getSelectableName() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        int cleanQuantity = quantity;
        if (cleanQuantity < 0)
            cleanQuantity *= -1;
        return
                "D" + invoiceNumber + " " +
                        sdf.format(new Date(time)) + " " +
                        customerNumber + " " +
                        transCode + "  " +
                        cleanQuantity + " @ " +
                        "$" + cleanDouble(unitPrice, 2);
    }

}
