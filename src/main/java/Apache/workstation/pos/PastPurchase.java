package Apache.workstation.pos;

import Apache.objects.Selectable;

import java.text.SimpleDateFormat;
import java.util.Date;

import static Apache.util.General.cleanDouble;

public class PastPurchase implements Selectable {

    public int invoiceNumber;
    public String mfg;
    public String partNumber;
    private double unitPrice;
    private String transCode;
    private Date date;

    public PastPurchase(
            int invoiceNumber,
            String mfg,
            String partNumber,
            double unitPrice,
            String transCode,
            Date date
    ){
        this.invoiceNumber = invoiceNumber;
        this.mfg = mfg;
        this.partNumber = partNumber;
        this.unitPrice = unitPrice;
        this.transCode = transCode;
        this.date = date;
    }

    public String getMfg(){
        return mfg;
    }

    public String getPartNumber(){
        return partNumber;
    }

    public double getUnitPrice(){
        return unitPrice;
    }

    public int getInvoiceNumber(){
        return invoiceNumber;
    }

    @Override
    public String getSelectableName() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return "D" + invoiceNumber + "\t\t"
                + sdf.format(date)
                + "\t" + transCode
                + "\t$" + cleanDouble(unitPrice, 3);
    }

}
