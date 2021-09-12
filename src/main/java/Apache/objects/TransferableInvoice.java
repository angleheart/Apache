package Apache.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TransferableInvoice implements Invoiceable {

    private final TransferableInvoiceLine[] lines;
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

    public TransferableInvoice(
            List<TransferableInvoiceLine> lines,
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
        this.lines = lines.toArray(TransferableInvoiceLine[]::new);
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

    public TransferableInvoice(Invoice invoice) {
        List<TransferableInvoiceLine> newLines = new ArrayList<>();
        for(InvoiceLine line : invoice.getLines())
            newLines.add(new TransferableInvoiceLine(line));
        lines = newLines.toArray(TransferableInvoiceLine[]::new);
        totals = invoice.getTotals();
        customer = invoice.getCustomer();
        vehicleDescription = invoice.getVehicleDescription();
        shipTo = invoice.getVehicleDescription();
        invoiceNumber = invoice.getInvoiceNumber();
        accountingPeriod = invoice.getAccountingPeriod();
        time = invoice.getDate().getTime();
        po = invoice.getPo();
        counterPersonNumber = invoice.getCounterPersonNumber();
        releaseCode = invoice.getReleaseCodeInt();
        transCode = invoice.getTransCode();
        balance = invoice.getBalance();
    }

    public List<TransferableInvoiceLine> getLines(){
        return new ArrayList<>(List.of(lines));
    }

    public double getBalance(){
        return balance;
    }

    public int getAccountingPeriod(){
        return accountingPeriod;
    }

    @Override
    public List<InvoiceableLine> getInvoiceableLines() {
        return Arrays.asList(lines);
    }

    @Override
    public InvoiceTotals getTotals() {
        return totals;
    }

    @Override
    public Customer getCustomer() {
        return customer;
    }

    @Override
    public String getVehicleDescription() {
        return vehicleDescription;
    }

    @Override
    public String getShipTo() {
        return shipTo;
    }

    @Override
    public int getInvoiceNumber() {
        return invoiceNumber;
    }

    @Override
    public Date getDate() {
        return new Date(time);
    }

    @Override
    public String getPo() {
        return po;
    }

    @Override
    public int getCounterPersonNumber() {
        return counterPersonNumber;
    }

    public int getReleaseCodeInt(){
        return releaseCode;
    }
    @Override
    public ReleaseCode getReleaseCode() {
        switch(releaseCode){
            case 11 -> {
                return ReleaseCode.CASH;
            }
            case 12 -> {
                return ReleaseCode.CHECK;
            }
            case 13 -> {
                return ReleaseCode.PLASTIC;
            }
            default -> {
                return ReleaseCode.CHARGE;
            }
        }
    }

    @Override
    public String getTransCode() {
        return transCode;
    }
}
