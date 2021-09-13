package Apache.database;

import Apache.objects.Payment;
import Apache.objects.PerInvoicePayment;
import Apache.objects.Transferable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static Apache.database.Connector.*;

public class PaymentBase {

    public static List<Transferable> getAllOpenBasicPayments() {
        List<Transferable> payments = new ArrayList<>();
        try {
            Connection connection = getConnection();
            ResultSet paymentResults = connection.createStatement().executeQuery(
                    "SELECT * FROM Payments WHERE AccountingPeriod = 0;"
            );

            while (paymentResults.next()) {
                payments.add(
                        new Payment(
                                paymentResults.getDouble("PaymentAmount"),
                                paymentResults.getInt("ReleaseCode"),
                                0,
                                paymentResults.getString("DocumentDetail"),
                                paymentResults.getDate("ReleaseTime")
                        )
                );
            }
            return payments;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        }
    }


    public static Payment getPaymentByID(int paymentId) {
        try {
            Connection connection = getConnection();
            ResultSet resultSet = connection.createStatement().executeQuery(
                    "SELECT * FROM Payments WHERE PaymentID = " + paymentId + ";"
            );
            if (!resultSet.next()) {
                return null;
            }
            return new Payment(
                    resultSet.getDouble("PaymentAmount"),
                    resultSet.getInt("ReleaseCode"),
                    resultSet.getInt("AccountingPeriod"),
                    resultSet.getString("DocumentDetail"),
                    new Date(resultSet.getTimestamp("ReleaseTime").getTime())
            );
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        }
    }

    public static boolean savePayment(Payment payment) {
        Connection connection;
        try {
            connection = getConnection();
        } catch (SQLException sqlException) {
            return false;
        }

        try {
            connection.setAutoCommit(true);
            int securedKey = getPaymentId(connection);
            if (securedKey == -1)
                return false;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Statement statement = connection.createStatement();
            connection.setAutoCommit(false);

            statement.addBatch(
                    "INSERT INTO Payments(" +
                            "PaymentID, " +
                            "CustomerNumber, " +
                            "PaymentAmount, " +
                            "ReleaseCode, " +
                            "AccountingPeriod, " +
                            "DocumentDetail, " +
                            "ReleaseTime" +
                            ") VALUES (" +
                            securedKey + ", " +
                            "'" + payment.getCustomer().getNumber() + "', " +
                            payment.getAmount() + ", " +
                            payment.getReleaseCodeInt() + ", " +
                            payment.getAccountingPeriod() + ", " +
                            "'" + payment.getDocumentDetail() + "', " +
                            "'" + sdf.format(new Date()) + "');"
            );
            for (
                    PerInvoicePayment perInvoicePayment :
                    payment.getPerInvoicePayments()
            ) {
                statement.addBatch(
                        "UPDATE Invoices " +
                                "SET Balance = Balance - " + perInvoicePayment.getAmount() +
                                " WHERE InvoiceNumber = " + perInvoicePayment.getInvoiceNumber() + ";"
                );
                statement.addBatch(
                        "INSERT INTO PerInvoicePayments(" +
                                "PaymentID, " +
                                "InvoiceNumber, " +
                                "AmountApplied, " +
                                "OriginalBalance" +
                                ") VALUES (" +
                                securedKey + ", " +
                                perInvoicePayment.getInvoiceNumber() + ", " +
                                perInvoicePayment.getAmount() + ", " +
                                perInvoicePayment.getOriginalBalance() + ");"
                );
            }

            statement.executeBatch();
            connection.setAutoCommit(true);
            return true;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            System.out.println("attempting rollback");
            try {
                connection.rollback();
                connection.setAutoCommit(true);
                System.out.println("Rollback success");
            } catch (SQLException sqlException1) {
                System.out.println("Rollback failed");
                sqlException1.printStackTrace();
            }
        }
        return false;
    }

    public static int getPaymentId(Connection connection) {
        System.out.println("getting payment id");
        try {
            ResultSet highestNumber = connection.createStatement().executeQuery(
                    "SELECT MAX(PaymentId) FROM UsedPaymentIDs;"
            );

            if (!highestNumber.next()){
                return -1;
            }

            int paymentId = highestNumber.getInt(1);

            do {
                paymentId++;
            } while (!secureNumber(paymentId, connection));
            return paymentId;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return -1;
        }
    }

    private static boolean secureNumber(int paymentId, Connection connection) {
        System.out.println("attempting to secure payment id");
        try {
            connection.createStatement().execute(
                    "INSERT INTO UsedPaymentIDs(PaymentID) VALUES(" + paymentId + ");"
            );
            return true;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return false;
        }
    }

}
