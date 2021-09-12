package Apache.objects;

import static Apache.util.General.cleanDouble;

public class InvoiceTotals {

    private final double taxableNet;
    private final double nonTaxableNet;
    private final double freightTotal;
    private final double taxRate;

    public InvoiceTotals(
            double taxableNet,
            double nonTaxableNet,
            double freightTotal,
            double taxRate
    ) {
        this.taxableNet = taxableNet;
        this.nonTaxableNet = nonTaxableNet;
        this.freightTotal = freightTotal;
        this.taxRate = taxRate;
    }

    public double getTaxableNet() {
        return taxableNet;
    }

    public double getNonTaxableNet() {
        return nonTaxableNet;
    }

    public double getFreightTotal() {
        return freightTotal;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public double getSubTotal() {
        return taxableNet + nonTaxableNet;
    }

    public double getTaxTotal() {
        return Double.parseDouble(
                cleanDouble(taxableNet * taxRate, 2)
        );
    }

    public double getGrandTotal() {
        return getSubTotal() + getTaxTotal() + getFreightTotal();
    }

}
