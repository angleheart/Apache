package Apache.objects;

import java.util.Date;
import java.util.List;

import static Apache.util.General.cleanDouble;

public class Invoice implements Accountable, Selectable {

    private final List<InvoiceLine> lines;
    private final InvoiceTotals totals;
    private final Customer customer;
    private final String vehicleDescription;
    private final String shipTo;
    private final int invoiceNumber;
    private final int accountingPeriod;
    private final long time;
    private final String po;
    private final int counterPersonNumber;
    private final double balance;
    private final int releaseCode;
    private final String transCode;

    public Invoice(
            List<InvoiceLine> lines,
            InvoiceTotals totals,
            Customer customer,
            String vehicleDescription,
            String shipTo,
            int invoiceNumber,
            int accountingPeriod,
            long time,
            String po,
            int counterPersonNumber,
            double balance,
            int releaseCode,
            String transCode
    ){
        this.lines = lines;
        this.totals = totals;
        this.customer = customer;
        this.vehicleDescription = vehicleDescription;
        this.shipTo = shipTo;
        this.invoiceNumber = invoiceNumber;
        this.accountingPeriod = accountingPeriod;
        this.time = time;
        this.po = po;
        this.counterPersonNumber = counterPersonNumber;
        this.balance = balance;
        this.releaseCode = releaseCode;
        this.transCode = transCode;
    }

    public List<InvoiceLine> getLines() {
        return lines;
    }

    public InvoiceTotals getTotals() {
        return totals;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getVehicleDescription() {
        return vehicleDescription;
    }

    public String getShipTo() {
        return shipTo;
    }

    public int getInvoiceNumber() {
        return invoiceNumber;
    }

    public int getAccountingPeriod() {
        return accountingPeriod;
    }

    public long getTime() {
        return time;
    }

    public Date getDate(){
        return new Date(time);
    }

    public String getPo() {
        return po;
    }

    public int getCounterPersonNumber() {
        return counterPersonNumber;
    }

    public double getBalance() {
        return balance;
    }

    public int getReleaseCode() {
        return releaseCode;
    }

    public String getTransCode() {
        return transCode;
    }

    @Override
    public String getSelectableName() {
        String name = customer.getName();
        if (name.length() > 15)
            name = name.substring(0, 15);
        else
            while (name.length() < 15)
                name = name.concat(" ");

        String vehicle = vehicleDescription;
        if (vehicleDescription.length() > 20)
            vehicle = vehicle.substring(0, 20);
        else
            while (vehicle.length() < 20)
                vehicle = vehicle.concat(" ");
        return name + " | " + vehicle + " | $" + cleanDouble(totals.getGrandTotal(), 2);
    }
}
