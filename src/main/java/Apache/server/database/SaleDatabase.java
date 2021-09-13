package Apache.server.database;

import Apache.objects.PartLedgerEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SaleDatabase extends Database {

    public List<PartLedgerEntry> getPartLedgerEntries(String mfg, String partNumber) throws SQLException {
        List<PartLedgerEntry> toReturn = new ArrayList<>();

        PreparedStatement prepStatement;
        if(mfg.equals("*")){
            prepStatement = connection.prepareStatement(
                    "SELECT * FROM InvoiceLines WHERE PartNumber = ? ORDER BY InvoiceNumber DESC;"
            );
            prepStatement.setString(1, partNumber);
        }else{
            prepStatement = connection.prepareStatement(
                    "SELECT * FROM InvoiceLines WHERE LineCode = ? AND PartNumber = ? " +
                            "ORDER BY InvoiceNumber DESC;"
            );
            prepStatement.setString(1, mfg);
            prepStatement.setString(2, partNumber);
        }
        ResultSet resultSet = prepStatement.executeQuery();

        PreparedStatement prepCustomer = connection.prepareStatement(
                "SELECT CustomerNumber FROM Invoices WHERE InvoiceNumber = ?;"
        );

        while(resultSet.next()){
            int invoiceNumber = resultSet.getInt("InvoiceNumber");
            prepCustomer.setInt(1, invoiceNumber);
            ResultSet customerResult = prepCustomer.executeQuery();
            if(!resultSet.next()){
                continue;
            }
            toReturn.add(
                    new PartLedgerEntry(
                            invoiceNumber,
                            resultSet.getTimestamp("ReleaseTime").getTime(),
                            customerResult.getString("CustomerNumber"),
                            resultSet.getString("TransCode"),
                            resultSet.getInt("Quantity"),
                            resultSet.getDouble("Price")
                    )
            );
        }
        return toReturn;
    }






}
