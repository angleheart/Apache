package Apache.objects;

import java.util.Date;

import static Apache.util.General.cleanDouble;

public class InvoiceLine extends Line implements Selectable {

    private final int invoiceNumber;
    private final long time;
    private double corePrice;

    public InvoiceLine(
            int indexKey,
            int invoiceNumber,
            long time,
            String transCode,
            int qty,
            String mfg,
            String partNumber,
            String description,
            double listPrice,
            double unitPrice,
            double corePrice,
            String tx
    ) {
        super(
                indexKey,
                transCode,
                qty,
                mfg,
                partNumber,
                description,
                listPrice,
                unitPrice,
                tx
        );

        this.invoiceNumber = invoiceNumber;
        this.time = time;
        this.corePrice = corePrice;
    }

    public int getInvoiceNumber(){
        return invoiceNumber;
    }

    public long getTime(){
        return time;
    }

    public Date getDate(){
        return new Date(time);
    }

    public double getCorePrice() {
        return corePrice;
    }

    public void setCorePrice(double corePrice) {
        this.corePrice = corePrice;
    }

    public double getExtendedPrice() {
        return Double.parseDouble(
                cleanDouble(
                        getUnitPrice() * getQty(),
                        2
                )
        );
    }

    @Override
    public String getSelectableName() {
        return "";
    }

}
