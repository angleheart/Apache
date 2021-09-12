package Apache.objects;

import java.util.Date;
import java.util.List;

public interface Invoiceable {
    List<InvoiceableLine> getInvoiceableLines();
    InvoiceTotals getTotals();
    Customer getCustomer();
    String getVehicleDescription();
    String getShipTo();
    int getInvoiceNumber();
    Date getDate();
    String getPo();
    int getCounterPersonNumber();
    ReleaseCode getReleaseCode();
    String getTransCode();
}
