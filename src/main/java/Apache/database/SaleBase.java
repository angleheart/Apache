package Apache.database;

import Apache.objects.PartLedgerEntry;
import Apache.workstation.pos.PastPurchase;
import Apache.objects.Transferable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static Apache.database.Connector.getConnection;

public class SaleBase {

    public static List<Transferable> getPastPurchases(
            String customerNumber,
            String mfg,
            String partNumber
    ) {
        List<Transferable> pastPurchases = new ArrayList<>();
        try {
            Connection conn = getConnection();
            ResultSet results = conn.createStatement().executeQuery(
                    "SELECT InvoiceNumber, ReleaseTime, Price, TransCode FROM InvoiceLines WHERE InvoiceNumber IN " +
                            "(SELECT InvoiceNumber FROM Invoices WHERE CustomerNumber = '" + customerNumber + "') " +
                            "AND LineCode = '" + mfg + "' AND PartNumber = '" + partNumber + "';"
            );
            while (results.next()) {
                pastPurchases.add(
                        new PastPurchase(
                                results.getInt("InvoiceNumber"),
                                mfg,
                                partNumber,
                                results.getDouble("Price"),
                                results.getString("TransCode"),
                                new java.util.Date(results.getTimestamp("ReleaseTime").getTime())
                        )
                );
            }
            return pastPurchases;
        } catch (SQLException sqlException) {
            return null;
        }
    }

    public static List<Transferable> getPartLedgerEntries(
            String mfg,
            String partNumber
    ){
        List<Transferable> entries = new ArrayList<>();
        try{
            Connection conn = getConnection();
            ResultSet resultSet = conn.createStatement().executeQuery(
                    "SELECT * FROM InvoiceLines WHERE LineCode = '" + mfg +
                            "' AND PartNumber = '" + partNumber + "' ORDER BY InvoiceNumber;"
            );
            while(resultSet.next()){
                int invoiceNumber = resultSet.getInt("InvoiceNumber");
                String customerNumber;

                ResultSet resultSet1 = conn.createStatement().executeQuery(
                        "SELECT CustomerNumber FROM Invoices WHERE InvoiceNumber = " + invoiceNumber + ";"
                );
                if(!resultSet1.next())
                    continue;
                customerNumber = resultSet1.getString("CustomerNumber");

                entries.add(
                        new PartLedgerEntry(
                                invoiceNumber,
                                resultSet.getTimestamp("ReleaseTime").getTime(),
                                customerNumber,
                                resultSet.getString("TransCode"),
                                resultSet.getInt("Quantity"),
                                resultSet.getDouble("Price")
                        )
                );
            }
            return entries;
        }catch(SQLException sqlException){
            return null;
        }
    }

}
