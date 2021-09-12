package Apache.objects;

import static Apache.util.General.cleanDouble;

public abstract class Line {

    private final int indexKey;
    private final String transCode;
    private final int qty;
    private final String mfg;
    private final String partNumber;
    private final String description;
    private final double listPrice;
    private final double unitPrice;
    private final String tx;

    public Line(
            int indexKey,
            String transCode,
            int qty,
            String mfg,
            String partNumber,
            String description,
            double listPrice,
            double unitPrice,
            String tx
    ) {
        this.indexKey = indexKey;
        this.transCode = transCode;
        this.qty = qty;
        this.mfg = mfg;
        this.partNumber = partNumber;
        this.description = description;
        this.listPrice = listPrice;
        this.unitPrice = unitPrice;
        this.tx = tx;
    }

    public int getIndexKey() {
        return indexKey;
    }

    public String getTransCode() {
        return transCode;
    }

    public int getQty() {
        return qty;
    }

    public String getMfg() {
        return mfg;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public String getDescription() {
        return description;
    }

    public double getListPrice() {
        return listPrice;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public String getTx() {
        return tx;
    }

    public double getExtendedPrice(){
        return Double.parseDouble(cleanDouble(qty*unitPrice, 2));
    }


}
