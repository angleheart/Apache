package Apache.server.database;

import Apache.objects.Receivable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReceivableDatabase extends Database {

    public List<Receivable> getReceivable(String customerNumber) throws SQLException {
        PreparedStatement prepStatement;
        ResultSet resultSet;
        List<Receivable> toReturn = new ArrayList<>();

        if(customerNumber.equals("*")){
            prepStatement = connection.prepareStatement(
                    "SELECT * FROM Invoices WHERE Balance != 0 AND AccountingPeriod > 0 " +
                            "AND CustomerNumber != '1001';"
            );
        }else{
            prepStatement = connection.prepareStatement(
                    "SELECT * FROM Invoices WHERE Balance != 0 AND AccountingPeriod > 0 " +
                            "AND CustomerNumber = ?;"
            );
            prepStatement.setString(1, customerNumber);
        }
        resultSet = prepStatement.executeQuery();
        double curr = 0;
        double day30 = 0;
        double day60 = 0;
        double day90 = 0;
        double paid = 0;
        while (resultSet.next()) {
            double balance = resultSet.getDouble("Balance");
            switch (resultSet.getInt("AccountingPeriod")) {
                case 1 -> curr += balance;
                case 2 -> day30 += balance;
                case 3 -> day60 += balance;
                case 4 -> day90 += balance;
            }
        }
        prepStatement = connection.prepareStatement(
                "SELECT * FROM Payments WHERE AccountingPeriod = 1 AND CustomerNumber = ?;"
        );
        prepStatement.setString(1, customerNumber);
        resultSet = prepStatement.executeQuery();
        while(resultSet.next())
            paid += resultSet.getDouble("PaymentAmount");
        Receivable receivable = new Receivable(
                curr,
                day30,
                day60,
                day90,
                paid
        );
        toReturn.add(receivable);
        return toReturn;
    }

}
