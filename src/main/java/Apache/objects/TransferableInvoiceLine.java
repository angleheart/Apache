package Apache.objects;

import java.util.Date;

import static Apache.util.General.cleanDouble;

public class TransferableInvoiceLine implements InvoiceableLine {

    private final int indexKey;
    private final int invoiceNumber;
    private final long time;
    private final String transCode;
    private final String mfg;
    private final int qty;
    private final String partNumber;
    private final String description;
    private final double listPrice;
    private final double unitPrice;
    private final double extendedPrice;
    private final double corePrice;
    private final String tx;

    public TransferableInvoiceLine(
            int indexKey,
            int invoiceNumber,
            long time,
            String transCode,
            String mfg,
            int qty,
            String partNumber,
            String description,
            double listPrice,
            double unitPrice,
            double corePrice,
            String tx
    ) {
        this.indexKey = indexKey;
        this.invoiceNumber = invoiceNumber;
        this.time = time;
        this.transCode = transCode;
        this.mfg = mfg;
        this.qty = qty;
        this.partNumber = partNumber;
        this.description = description;
        this.listPrice = listPrice;
        this.unitPrice = unitPrice;
        this.extendedPrice =
                Double.parseDouble(
                        cleanDouble(
                                unitPrice * qty,
                                2
                        )
                );
        this.corePrice = corePrice;
        this.tx = tx;
    }

    public TransferableInvoiceLine(InvoiceLine invoiceLine) {
        indexKey = invoiceLine.getIndexKey();
        invoiceNumber = invoiceLine.getInvoiceNumber();
        time = invoiceLine.getDate().getTime();
        transCode = invoiceLine.getTransCode();
        mfg = invoiceLine.getMfg();
        qty = invoiceLine.getQty();
        partNumber = invoiceLine.getPartNumber();
        description = invoiceLine.getDescription();
        listPrice = invoiceLine.getListPrice();
        unitPrice = invoiceLine.getUnitPrice();
        extendedPrice = invoiceLine.getExtendedPrice();
        corePrice = invoiceLine.getCorePrice();
        tx = invoiceLine.getTx();
    }

    public int getIndexKey() {
        return indexKey;
    }

    public int getInvoiceNumber() {
        return invoiceNumber;
    }

    public Date getDate() {
        return new Date(time);
    }

    public String getTransCode() {
        return transCode;
    }

    @Override
    public String getMfg() {
        return mfg;
    }

    @Override
    public int getQty() {
        return qty;
    }

    @Override
    public String getPartNumber() {
        return partNumber;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public double getListPrice() {
        return listPrice;
    }

    @Override
    public double getUnitPrice() {
        return unitPrice;
    }

    @Override
    public double getExtendedPrice() {
        return extendedPrice;
    }

    @Override
    public double getCorePrice() {
        return corePrice;
    }

    @Override
    public String getTx() {
        return tx;
    }
}
