package Apache.objects;

public class PaymentLine implements Selectable {

    private final int invoiceNumber;
    private final double amount;
    private final double originalBalance;

    public PaymentLine(
            int invoiceNumber,
            double amount,
            double originalBalance
    ){
        this.invoiceNumber = invoiceNumber;
        this.amount = amount;
        this.originalBalance = originalBalance;
    }

    public int getInvoiceNumber(){
        return invoiceNumber;
    }

    public double getAmount(){
        return amount;
    }

    public double getOriginalBalance(){
        return originalBalance;
    }

    @Override
    public String getSelectableName() {
        return null;
    }

}
