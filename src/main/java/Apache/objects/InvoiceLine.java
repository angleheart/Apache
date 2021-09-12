package Apache.objects;

import java.util.Date;

public class InvoiceLine extends Line implements Selectable, InvoiceableLine {

    private final int invoiceNumber;
    private final Date date;
    private double corePrice;

    public InvoiceLine(TransferableInvoiceLine transferLine){
        super(
                transferLine.getIndexKey(),
                transferLine.getTransCode(),
                transferLine.getQty(),
                transferLine.getMfg(),
                transferLine.getPartNumber(),
                transferLine.getDescription(),
                transferLine.getListPrice(),
                transferLine.getUnitPrice(),
                transferLine.getTx()
        );
        invoiceNumber = transferLine.getInvoiceNumber();
        date = transferLine.getDate();
    }

    public InvoiceLine(
            int indexKey,
            int invoiceNumber,
            Date date,
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
        super(indexKey, transCode, qty, mfg, partNumber, description, listPrice, unitPrice, tx);
        this.invoiceNumber = invoiceNumber;
        this.date = date;
        this.corePrice = corePrice;
    }

    public Date getDate(){
        return date;
    }

    public int getInvoiceNumber(){
        return invoiceNumber;
    }

    public double getCorePrice(){
        return corePrice;
    }

    public void setCorePrice(double corePrice){
        this.corePrice = corePrice;
    }

    @Override
    public String getSelectableName() {
        return "";
    }
}
