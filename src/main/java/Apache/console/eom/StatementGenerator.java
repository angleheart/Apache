package Apache.console.eom;

import Apache.objects.Customer;
import Apache.database.InvoiceBase;
import Apache.database.PaymentBase;
import Apache.objects.Invoice;
import Apache.objects.Payment;
import Apache.objects.Release;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import static Apache.database.Connector.getConnection;
import static Apache.util.General.cleanDouble;

public class StatementGenerator {

    private final int month;
    private final int year;

    private static class Amounts {
        double og;
        double app;
        double bal;
    }

    public StatementGenerator(int month, int year) {
        this.month = month;
        this.year = year;
    }


    public static List<StatementLine> getStatementLines(Customer customer) {
        return customer.paysByInvoice() ? getPayByInvoiceLines(customer.getNumber()) :
                getBalanceForwardLines(customer.getNumber());
    }

    private static List<StatementLine> getBalanceForwardLines(String customerNumber) {
        return getPayByInvoiceLines(customerNumber);
    }

    private static List<StatementLine> getPayByInvoiceLines(String customerNumber) {
        List<StatementLine> toReturn = new ArrayList<>();
        Map<Date, List<Release>> releases = new TreeMap<>();
        List<Integer> invoiceNumbersToPost = getInvoiceNumbersToPost(customerNumber);
        List<Integer> paymentNumbersToPost = getPaymentNumbersToPost(customerNumber);

        if (invoiceNumbersToPost == null || paymentNumbersToPost == null)
            return null;

        for (Integer invNum : invoiceNumbersToPost) {
            Invoice invoice = InvoiceBase.getInvoiceByNumberFromAll(invNum);
            if (invoice == null)
                return null;

            List<Release> releaseList;
            if (releases.containsKey(invoice.getDate()))
                releaseList = releases.get(invoice.getDate());
            else
                releaseList = new ArrayList<>();

            releaseList.add(invoice);
            releases.put(invoice.getDate(), releaseList);
        }
        for (Integer payNum : paymentNumbersToPost) {
            Payment payment = PaymentBase.getPaymentByID(payNum);
            if (payment == null)
                return null;
            List<Release> releaseList;
            if (releases.containsKey(payment.getDate()))
                releaseList = releases.get(payment.getDate());
            else
                releaseList = new ArrayList<>();
            releaseList.add(payment);
            releases.put(payment.getDate(), releaseList);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        int indexKey = 0;
        double lastDue = 0;
        Amounts amounts;

        for (List<Release> releaseList : releases.values())
            for (Release release : releaseList) {
                switch (release.getType()) {
                    case INVOICE -> {
                        Invoice invoice = (Invoice) release;
                        amounts = getPerInvoiceAmounts(invoice);
                        if (amounts == null)
                            return null;
                        toReturn.add(
                                new StatementLine(
                                        indexKey,
                                        new String[]{
                                                sdf.format(invoice.getDate()),
                                                "D-" + invoice.getInvoiceNumber(),
                                                invoice.getPo(),
                                                cleanDouble(amounts.og, 2),
                                                cleanDouble(amounts.app, 2),
                                                cleanDouble(amounts.bal, 2),
                                                cleanDouble(amounts.bal + lastDue, 2)
                                        }
                                )
                        );
                        lastDue = amounts.bal + lastDue;
                    }
                    case PAYMENT -> {
                        Payment payment = (Payment) release;
                        toReturn.add(
                                new StatementLine(
                                        indexKey,
                                        new String[]{
                                                sdf.format(payment.getDate()),
                                                "PAYMENT",
                                                payment.getDocumentDetail(),
                                                cleanDouble(payment.getAmount(), 2) + "-",
                                                cleanDouble(payment.getAmount(), 2),
                                                cleanDouble(0, 2),
                                                cleanDouble(lastDue, 2)
                                        }
                                )
                        );
                    }
                }
                indexKey++;
            }
        return toReturn;
    }


    private static List<Integer> getInvoiceNumbersToPost(String customerNumber) {
        try {
            Connection connection = getConnection();
            List<Integer> invoiceNumbers = new ArrayList<>();

            ResultSet resultSet = connection.createStatement().executeQuery(
                    "SELECT InvoiceNumber FROM Invoices WHERE AccountingPeriod = 1 " +
                            "AND CustomerNumber = '" + customerNumber + "' AND " +
                            "ReleaseCode = 31;"
            );

            while (resultSet.next()) {
                invoiceNumbers.add(resultSet.getInt("InvoiceNumber"));
            }

            resultSet = connection.createStatement().executeQuery(
                    "SELECT DISTINCT InvoiceNumber " +
                            "FROM PerInvoicePayments " +
                            "WHERE PaymentID IN " +
                            "( " +
                            "SELECT PaymentID " +
                            "FROM Payments " +
                            "WHERE AccountingPeriod = 1 " +
                            ") " +
                            "AND InvoiceNumber IN " +
                            "( " +
                            "SELECT InvoiceNumber " +
                            "FROM Invoices " +
                            "WHERE CustomerNumber = '" + customerNumber + "' " +
                            ")"
            );

            while (resultSet.next()) {
                int invoiceNumber = resultSet.getInt("InvoiceNumber");
                if (!invoiceNumbers.contains(invoiceNumber))
                    invoiceNumbers.add(invoiceNumber);
            }

            resultSet = connection.createStatement().executeQuery(
                    "SELECT InvoiceNumber FROM Invoices WHERE Balance != 0 AND " +
                            "CustomerNumber = '" + customerNumber + "' AND " +
                            "ReleaseCode = 31;"
            );

            while (resultSet.next()) {
                int invoiceNumber = resultSet.getInt("InvoiceNumber");
                if (!invoiceNumbers.contains(invoiceNumber))
                    invoiceNumbers.add(invoiceNumber);
            }
            return invoiceNumbers;
        } catch (SQLException sqlException) {
            return null;
        }
    }

    private static List<Integer> getPaymentNumbersToPost(String customerNumber) {
        try {
            Connection connection = getConnection();
            List<Integer> paymentIDs = new ArrayList<>();

            ResultSet resultSet = connection.createStatement().executeQuery(
                    "SELECT PaymentID FROM Payments WHERE CustomerNumber = '" + customerNumber + "' " +
                            "AND AccountingPeriod = 1;"
            );
            while (resultSet.next())
                paymentIDs.add(resultSet.getInt("PaymentID"));
            return paymentIDs;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        }
    }

    private static Amounts getPerInvoiceAmounts(Invoice invoice) {
        try {
            int invoiceNumber = invoice.getInvoiceNumber();
            Amounts amounts = new Amounts();


            amounts.bal = invoice.getBalance();

            Connection connection = getConnection();
            ResultSet resultSet;
            resultSet = connection.createStatement().executeQuery(
                    "SELECT AmountApplied FROM PerInvoicePayments WHERE InvoiceNumber = " + invoiceNumber +
                            " AND PaymentID IN (SELECT PaymentID FROM Payments WHERE AccountingPeriod = 1);"

            );

            amounts.app = 0;
            while (resultSet.next())
                amounts.app += resultSet.getDouble("AmountApplied");

            resultSet = connection.createStatement().executeQuery(
                    "SELECT AmountApplied FROM PerInvoicePayments WHERE InvoiceNumber = " + invoiceNumber + ";"
            );

            double totalAmountApplied = 0;
            while (resultSet.next())
                totalAmountApplied += resultSet.getDouble("AmountApplied");
            amounts.og = amounts.bal + totalAmountApplied;

            return amounts;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        }
    }


}
