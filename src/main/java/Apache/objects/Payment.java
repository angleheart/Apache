package Apache.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static Apache.objects.ReleaseType.PAYMENT;

public class Payment extends Release {

    private final double amount;
    private final String documentDetail;
    private final List<PerInvoicePayment> perInvoicePayments;

    public Payment(
            double amount,
            int releaseCode,
            int accountingPeriod,
            String documentDetail,
            Date date
    ){
        this(
                null,
                amount,
                releaseCode,
                accountingPeriod,
                documentDetail,
                date,
                new ArrayList<>()
        );
    }

    public Payment(
            Customer customer,
            double amount,
            int releaseCode,
            int accountingPeriod,
            String documentDetail,
            Date date,
            List<PerInvoicePayment> perInvoicePayments
    ) {
        super(PAYMENT,
                releaseCode,
                date,
                accountingPeriod,
                amount,
                customer
        );
        this.amount = amount;
        this.documentDetail = documentDetail;
        this.perInvoicePayments = perInvoicePayments;
    }


    public double getAmount() {
        return amount;
    }

    public String getDocumentDetail() {
        return documentDetail;
    }

    public List<PerInvoicePayment> getPerInvoicePayments(){
        return perInvoicePayments;
    }

    @Override
    public String getSelectableName() {
        return "";
    }

}
