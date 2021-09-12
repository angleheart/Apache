package Apache.console.eod;

import Apache.objects.Invoice;

import java.util.List;

public class SalesTaxSummary {

    private double netTaxable;
    private double netNonTaxable;
    private double totalSalesTax;


    public SalesTaxSummary(
            double netTaxable,
            double netNonTaxable,
            double totalSalesTax
    ){
        this.netTaxable = netTaxable;
        this.netNonTaxable = netNonTaxable;
        this.totalSalesTax = totalSalesTax;
    }

    public SalesTaxSummary(List<Invoice> invoiceList){
        for(Invoice invoice : invoiceList){
            netTaxable += invoice.getTotals().getTaxableNet();
            netNonTaxable += invoice.getTotals().getNonTaxableNet();
            totalSalesTax += invoice.getTotals().getTaxTotal();
        }
    }

    public double getNetTaxable() {
        return netTaxable;
    }

    public double getNetNonTaxable() {
        return netNonTaxable;
    }

    public double getTotalSalesTax() {
        return totalSalesTax;
    }


}
