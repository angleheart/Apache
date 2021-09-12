package Apache.console.eod;

import Apache.objects.Invoice;

import java.util.Date;
import java.util.List;

import static Apache.util.General.cleanDouble;

public class EODReport {


    private final SalesSummary salesSummary;
    private final SalesTaxSummary salesTaxSummary;
    private final Date date;

    public EODReport(
            Date date,
            double netCashInv,
            double netChargeInv,
            double netFreight,
            double netSalesTax,
            double netInterStore,
            double netTaxable,
            double netNonTaxable,
            double taxTotal
    ) {
        this.date = date;
        salesSummary = new SalesSummary(
                netCashInv,
                netChargeInv,
                netFreight,
                netSalesTax,
                netInterStore
        );
        salesTaxSummary = new SalesTaxSummary(
                netTaxable,
                netNonTaxable,
                taxTotal
        );
    }

    public EODReport(List<Invoice> invoices, Date date) {
        this.date = date;
        System.out.println("Running sales summary...");
        salesSummary = new SalesSummary(invoices);
        System.out.println("Running sales tax summary...");
        salesTaxSummary = new SalesTaxSummary(invoices);
    }

    public SalesSummary getSalesSummary() {
        return salesSummary;
    }

    public SalesTaxSummary getSalesTaxSummary() {
        return salesTaxSummary;
    }

    public Date getDate() {
        return date;
    }

    public boolean isSane() {
        if (salesTaxSummary == null || salesSummary == null)
            return false;
        return
                Double.parseDouble(
                        cleanDouble(salesSummary.getNetSalesTax(), 2)
                )
                        ==
                        Double.parseDouble(
                                cleanDouble(salesTaxSummary.getTotalSalesTax(), 2)
                        );
    }

}

