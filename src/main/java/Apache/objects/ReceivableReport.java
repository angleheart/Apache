package Apache.objects;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;


import static Apache.database.Connector.getConnection;

public class ReceivableReport {

    private final String customerNumber;

    private double currentBalance = 0;
    private double day30Balance = 0;
    private double day60Balance = 0;
    private double day90Balance = 0;
    private double totalPaidThisMonth = 0;

    public ReceivableReport(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public boolean runReceivables() {
        try {
            Connection connection = getConnection();
            ResultSet set = connection.createStatement().executeQuery(
                    "SELECT * FROM Invoices WHERE Balance != 0 " +
                            "AND AccountingPeriod > 0 " +
                            "AND CustomerNumber = '" + customerNumber + "';"
            );
            while (set.next()) {
                double balance = set.getDouble("Balance");
                switch (set.getInt("AccountingPeriod")) {
                    case 1 -> currentBalance += balance;
                    case 2 -> day30Balance += balance;
                    case 3 -> day60Balance += balance;
                    case 4 -> day90Balance += balance;
                }
            }
            set = connection.createStatement().executeQuery(
                    "SELECT * FROM Payments WHERE AccountingPeriod = 1 AND " +
                            "CustomerNumber = '" + customerNumber +"';"
            );
            while(set.next()){
                totalPaidThisMonth += set.getDouble("PaymentAmount");
            }
            return true;
        } catch (SQLException sqlException) {
            return false;
        }
    }


    public String getCustomerNumber() {
        return customerNumber;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public double getDay30Balance() {
        return day30Balance;
    }

    public double getDay60Balance() {
        return day60Balance;
    }

    public double getDay90Balance() {
        return day90Balance;
    }

    public double getTotalBalance() {
        return
                day90Balance +
                        day60Balance +
                        day30Balance +
                        currentBalance;
    }

    public double getTotalPaidThisMonth(){
        return totalPaidThisMonth;
    }

}
