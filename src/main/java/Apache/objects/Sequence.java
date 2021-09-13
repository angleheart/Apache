package Apache.objects;

import Apache.config.Config;

import java.util.ArrayList;
import java.util.List;

public class Sequence implements Transferable {

    private final String saveName;
    private final Customer customer;
    private final CounterPerson counterPerson;
    private final String po;
    private final String transCode;
    private final String vehicleDescription;
    private final String shipTo;
    private final double freightTotal;
    private final List<SequenceLine> sequenceLines;

    public Sequence(
            String saveName,
            Customer customer,
            CounterPerson counterPerson,
            String po,
            String transCode,
            String vehicleDescription,
            String shipTo,
            double freightTotal,
            List<SequenceLine> sequenceLines
    ) {
        this.saveName = saveName;
        this.customer = customer;
        this.counterPerson = counterPerson;
        this.po = po;
        this.transCode = transCode;
        this.vehicleDescription = vehicleDescription;
        this.shipTo = shipTo;
        this.freightTotal = freightTotal;
        this.sequenceLines = sequenceLines;
    }

    public String getSaveName() {
        return saveName;
    }

    public Customer getCustomer() {
        return customer;
    }

    public CounterPerson getCounterPerson() {
        return counterPerson;
    }

    public String getPo() {
        return po;
    }

    public String getTransCode() {
        return transCode;
    }

    public String getVehicleDescription() {
        return vehicleDescription;
    }

    public String getShipTo() {
        return shipTo;
    }

    public double getFreightTotal() {
        return freightTotal;
    }

    public List<SequenceLine> getSequenceLines() {
        return sequenceLines;
    }

    public Invoice releaseToInvoice(int invoiceNumber, int releaseCode) {
        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
        if (invoiceNumber == -1)
            return null;

        List<InvoiceLine> invoiceLines = new ArrayList<>();

        int invoiceIndex = 0;
        for (int i = 0; i < sequenceLines.size(); i++) {
            SequenceLine sequenceLine = sequenceLines.get(i);
            if (sequenceLine.isVoided())
                continue;

            if (sequenceLine.getMfg().endsWith("9") &&
                    i - 1 > -1 &&
                    !sequenceLines.get(i - 1).getMfg().endsWith("9")
            ) {
                invoiceLines.get(invoiceIndex - 1).setCorePrice(sequenceLine.getExtendedPrice());
                continue;
            }
            invoiceLines.add(new InvoiceLine(
                    invoiceIndex,
                    invoiceNumber,
                    date,
                    sequenceLines.get(i).getTransCode(),
                    sequenceLines.get(i).getQty(),
                    sequenceLines.get(i).getMfg(),
                    sequenceLines.get(i).getPartNumber(),
                    sequenceLines.get(i).getDescription(),
                    sequenceLines.get(i).getListPrice(),
                    sequenceLines.get(i).getUnitPrice(),
                    0,
                    sequenceLines.get(i).getTx()
            ));
            invoiceIndex++;
        }

        InvoiceTotals totals = getTotals();
        double balance = 0;
        if (releaseCode == 31)
            balance = totals.getGrandTotal();

        return new Invoice(
                invoiceNumber,
                customer,
                counterPerson.getNumber(),
                po,
                vehicleDescription,
                shipTo,
                date,
                transCode,
                releaseCode,
                balance,
                totals,
                0,
                invoiceLines
        );
    }

    public InvoiceTotals getTotals() {
        double taxableNet = 0;
        double nonTaxableNet = 0;

        for (SequenceLine line : sequenceLines) {
            if (line.isVoided())
                continue;

            if (line.getTx().equalsIgnoreCase("T")) {
                taxableNet += line.getExtendedPrice();
            } else {
                nonTaxableNet += line.getExtendedPrice();
            }
        }

        nonTaxableNet += freightTotal;

        return new InvoiceTotals(
                taxableNet,
                nonTaxableNet,
                freightTotal,
                Config.TAX_RATE
        );
    }

    @Override
    public String getSelectableName() {
        return saveName;
    }
}