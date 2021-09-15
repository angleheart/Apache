package Apache.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Payment implements Accountable, Selectable {

    private final int releaseCode;
    private final Customer customer;
    private final long time;
    private final int accountingPeriod;
    private final double amount;
    private final String documentDetail;
    private final List<PaymentLine> perInvoicePayments;

    public Payment(
            double amount,
            int releaseCode,
            int accountingPeriod,
            String documentDetail,
            Long time
    ){
        this(
                null,
                amount,
                releaseCode,
                accountingPeriod,
                documentDetail,
                time,
                new ArrayList<>()
        );
    }

    public Payment(
            Customer customer,
            double amount,
            int releaseCode,
            int accountingPeriod,
            String documentDetail,
            long time,
            List<PaymentLine> paymentLines
    ) {
        this.releaseCode = releaseCode;
        this.time = time;
        this.accountingPeriod = accountingPeriod;
        this.customer = customer;
        this.amount = amount;
        this.documentDetail = documentDetail;
        this.perInvoicePayments = paymentLines;
    }

    public int getReleaseCode() {
        return releaseCode;
    }

    public Customer getCustomer() {
        return customer;
    }

    public long getTime() {
        return time;
    }

    public int getAccountingPeriod() {
        return accountingPeriod;
    }

    public double getAmount() {
        return amount;
    }

    public String getDocumentDetail() {
        return documentDetail;
    }

    public List<PaymentLine> getPerInvoicePayments() {
        return perInvoicePayments;
    }

    public Date getDate(){
        return new Date(time);
    }

    @Override
    public String getSelectableName() {
        return "";
    }

}
