package Apache.console.eod;

import Apache.objects.Invoice;

import java.util.List;

public class SalesSummary {

    private double netCash = 0;
    private double netCharge = 0;
    private double netFreight = 0;
    private double netSalesTax = 0;
    private double netInterStore = 0;

    public SalesSummary(
            double netCash,
            double netCharge,
            double netFreight,
            double netSalesTax,
            double netInnerStore
    ){
        this.netCash = netCash;
        this.netCharge = netCharge;
        this.netFreight = netFreight;
        this.netSalesTax = netSalesTax;
        this.netInterStore = netInnerStore;
    }


    public SalesSummary(List<Invoice> invoices) {
        for(Invoice invoice : invoices){
            if(invoice.getCustomer().getNumber().equalsIgnoreCase("1001"))
                netInterStore += invoice.getTotals().getSubTotal();

            if(invoice.getReleaseCode() == 31)
                netCharge += invoice.getTotals().getGrandTotal();
            else
                netCash += invoice.getTotals().getGrandTotal();

            netFreight += invoice.getTotals().getFreightTotal();
            netSalesTax += invoice.getTotals().getTaxTotal();
        }
    }

    public double getNetInvoiceAmount(){
        return netCash + netCharge;
    }

    public double getNetSales(){
        return getNetInvoiceAmount() - netFreight - netSalesTax - netInterStore;
    }

    public double getNetCash() {
        return netCash;
    }

    public double getNetCharge() {
        return netCharge;
    }

    public double getNetFreight() {
        return netFreight;
    }

    public double getNetSalesTax() {
        return netSalesTax;
    }

    public double getNetInterStore() {
        return netInterStore;
    }


}
