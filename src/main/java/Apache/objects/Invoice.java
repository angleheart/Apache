package Apache.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static Apache.objects.ReleaseType.INVOICE;
import static Apache.util.General.cleanDouble;

public class Invoice extends Release implements Selectable, Invoiceable {

    private final int invoiceNumber;
    private final int counterPersonNumber;
    private final String po;
    private final String vehicleDescription;
    private final String shipTo;
    private final String transCode;
    private final double balance;
    private final InvoiceTotals totals;
    private final List<InvoiceLine> lines;


    public Invoice(TransferableInvoice transferableInvoice) {
        super(INVOICE,
                transferableInvoice.getReleaseCodeInt(),
                transferableInvoice.getDate(),
                transferableInvoice.getAccountingPeriod(),
                transferableInvoice.getTotals().getGrandTotal(),
                transferableInvoice.getCustomer()
                );
        this.invoiceNumber = transferableInvoice.getInvoiceNumber();
        this.counterPersonNumber = transferableInvoice.getCounterPersonNumber();
        this.po = transferableInvoice.getPo();
        this.vehicleDescription = transferableInvoice.getVehicleDescription();
        this.shipTo = transferableInvoice.getShipTo();
        this.transCode = transferableInvoice.getTransCode();
        this.balance = transferableInvoice.getBalance();
        this.totals = transferableInvoice.getTotals();
        List<InvoiceLine> toSet = new ArrayList<>();
        for(TransferableInvoiceLine transferLine : transferableInvoice.getLines())
            toSet.add(new InvoiceLine(transferLine));
        lines = toSet;
    }

    public Invoice(
            int invoiceNumber,
            Customer customer,
            int counterPersonNumber,
            String po,
            String vehicleDescription,
            String shipTo,
            Date date,
            String transCode,
            int releaseCode,
            double balance,
            InvoiceTotals totals,
            int accountingPeriod,
            List<InvoiceLine> lines
    ) {
        super(INVOICE,
                releaseCode,
                date,
                accountingPeriod,
                totals.getGrandTotal(),
                customer
        );
        this.invoiceNumber = invoiceNumber;
        this.counterPersonNumber = counterPersonNumber;
        this.po = po;
        this.vehicleDescription = vehicleDescription;
        this.shipTo = shipTo;
        this.transCode = transCode;
        this.balance = balance;
        this.totals = totals;
        this.lines = lines;
    }

    public int getInvoiceNumber() {
        return invoiceNumber;
    }

    public int getCounterPersonNumber() {
        return counterPersonNumber;
    }

    public String getPo() {
        return po;
    }

    public String getVehicleDescription() {
        return vehicleDescription;
    }

    public String getShipTo() {
        return shipTo;
    }

    public String getTransCode() {
        return transCode;
    }

    public InvoiceTotals getTotals() {
        return totals;
    }

    public List<InvoiceLine> getLines() {
        return lines;
    }

    public List<InvoiceableLine> getInvoiceableLines() {
        return new ArrayList<>(lines);
    }

    public double getBalance() {
        return balance;
    }

    @Override
    public String getSelectableName() {
        String name = getCustomer().getName();
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
