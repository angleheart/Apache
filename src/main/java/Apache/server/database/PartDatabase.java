package Apache.server.database;

import Apache.objects.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PartDatabase extends Database {

    public List<Transferable> getParts(String mfg, String partNumber) throws SQLException {

        partNumber = partNumber.replaceAll("/", "");
        partNumber = partNumber.replaceAll("-", "");
        partNumber = partNumber.replaceAll(" ", "");

        List<Transferable> toReturn = new ArrayList<>();
        PreparedStatement prepStatement;
        ResultSet resultSet;

        if (mfg.equals("*")) {
            prepStatement = connection.prepareStatement(
                    "SELECT * FROM Parts WHERE " +
                            "REPLACE(REPLACE(PartNumber, '-', ''), '/', '') LIKE ?;"
            );
            prepStatement.setString(1, partNumber);
        } else {
            prepStatement = connection.prepareStatement(
                    "SELECT * FROM Parts WHERE LineCode = ? AND " +
                            "REPLACE(REPLACE(PartNumber, '-', ''), '/', '') LIKE ?;"
            );
            prepStatement.setString(1, mfg);
            prepStatement.setString(2, partNumber);
        }
        resultSet = prepStatement.executeQuery();

        while(resultSet.next())
            toReturn.add(
                    new Part(
                            resultSet.getString("LineCode"),
                            resultSet.getString("PartNumber"),
                            resultSet.getString("Description"),
                            resultSet.getDouble("Cost"),
                            resultSet.getInt("StockQuantity"),
                            resultSet.getInt("AvailableQuantity")
                    )
            );
        return toReturn;
    }

    public boolean postInventoryUpdate(Invoice invoice) throws SQLException {
        return postInventoryUpdate(invoice, false);
    }

    public boolean postInventoryUpdate(Invoice invoice, boolean rollback) throws SQLException {

        PreparedStatement prepStatement = connection.prepareStatement(
                "UPDATE Parts " +
                        "SET AvailableQuantity = AvailableQuantity - ? " +
                        "WHERE LineCode = ? AND PartNumber = ?;"

        );

        for (InvoiceLine line : invoice.getLines()) {
            int quantity = line.getQty();
            if(rollback)
                quantity*=-1;
            prepStatement.setInt(1, quantity);
            prepStatement.setString(2, line.getMfg());
            prepStatement.setString(3, line.getPartNumber());
            prepStatement.execute();
        }
        return true;
    }


}
