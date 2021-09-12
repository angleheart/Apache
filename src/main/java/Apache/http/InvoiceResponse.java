package Apache.http;

import Apache.objects.Invoice;
import Apache.objects.Invoiceable;
import Apache.objects.TransferableInvoice;
import Apache.objects.TransferableInvoiceLine;

import java.util.ArrayList;
import java.util.List;

public class InvoiceResponse {

    private final List<TransferableInvoice> invoices;

    public InvoiceResponse(TransferableInvoice transferableInvoice){
        if(transferableInvoice == null)
            invoices = new ArrayList<>();
        else{
            List<TransferableInvoice> toSet = new ArrayList<>();
            toSet.add(transferableInvoice);
            invoices = toSet;
        }
    }

    public InvoiceResponse(List<TransferableInvoice> invoices){
        this.invoices = invoices;
    }

    public List<Invoice> getInvoices(){
        List<Invoice> toReturn = new ArrayList<>();
        for(TransferableInvoice tranInvoice : invoices)
            toReturn.add(new Invoice(tranInvoice));
        return toReturn;
    }

    public Invoice getInvoice(){
        return invoices.size() > 0 ? new Invoice(invoices.get(0)) : null;
    }

}
