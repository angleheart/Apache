package Apache.objects;

public interface InvoiceableLine {
    String getMfg();
    int getQty();
    String getPartNumber();
    String getDescription();
    double getListPrice();
    double getUnitPrice();
    double getExtendedPrice();
    double getCorePrice();
    String getTx();

}
