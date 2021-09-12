package Apache.database;

import Apache.objects.Invoice;
import Apache.objects.InvoiceLine;

import java.sql.Connection;
import java.sql.SQLException;

import static Apache.database.Connector.getConnection;

public class InventoryBase {

    public static boolean postInventoryUpdate(Invoice invoice){
        return postInventoryUpdate(invoice, false);
    }

    public static boolean postInventoryUpdate(Invoice invoice, boolean rollback) {
        for (InvoiceLine line : invoice.getLines()) {

            String mfg = line.getMfg();
            String partNumber = line.getPartNumber();
            int quantity = line.getQty();
            if(rollback)
                quantity*=-1;

            try {
                Connection conn = getConnection();
                conn.createStatement().execute(
                        "UPDATE Parts" +
                                " SET AvailableQuantity = AvailableQuantity - " + quantity +
                                " WHERE LineCode = '" + mfg + "' AND" +
                                " PartNumber = '" + partNumber + "';"
                );
            } catch (SQLException e) {
                return false;
            }
        }
        return true;
    }



}
