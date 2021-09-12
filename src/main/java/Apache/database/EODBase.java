package Apache.database;

import Apache.console.eod.EODReport;
import Apache.objects.Selectable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static Apache.database.Connector.getConnection;


public class EODBase {

    public static List<Selectable> getAllOpenReleases() {

        List<Selectable> releases = new ArrayList<>();

        try {
            List<Selectable> invoices = InvoiceBase.getAllOpenInvoices();
            if (invoices == null)
                return null;

            List<Selectable> payments = PaymentBase.getAllOpenBasicPayments();
            if (payments == null)
                return null;

            releases.addAll(invoices);
            releases.addAll(payments);

            return releases;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static boolean saveEODReport(
            int month,
            int day,
            int year,
            EODReport eodReport
    ) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Connection connection = getConnection();
            connection.createStatement().execute(
                    "INSERT INTO EndOfDayReports(" +
                            "Month, " +
                            "Day, " +
                            "Year, " +
                            "GenerationTime, " +
                            "NetCashInvoices, " +
                            "NetChargeInvoices, " +
                            "NetFreight, " +
                            "NetSalesTax, " +
                            "NetInterstore, " +
                            "NetTaxable, " +
                            "NetNonTaxable, " +
                            "TaxTotal" +
                            ") VALUES (" +
                            month + ", " +
                            day + ", " +
                            year + ", " +
                            "'" + sdf.format(eodReport.getDate()) + "', " +
                            eodReport.getSalesSummary().getNetCash() + ", " +
                            eodReport.getSalesSummary().getNetCharge() + ", " +
                            eodReport.getSalesSummary().getNetFreight() + ", " +
                            eodReport.getSalesSummary().getNetSalesTax() + ", " +
                            eodReport.getSalesSummary().getNetInterStore() + ", " +
                            eodReport.getSalesTaxSummary().getNetTaxable() + ", " +
                            eodReport.getSalesTaxSummary().getNetNonTaxable() + ", " +
                            eodReport.getSalesTaxSummary().getTotalSalesTax() +
                            ");"
            );
            return true;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return false;
        }
    }

    public static Boolean eodAlreadyExists(int month, int day, int year) {
        try {
            Connection conn = getConnection();
            ResultSet resultSet = conn.createStatement().executeQuery(
                    "SELECT * FROM EndOfDayReports WHERE " +
                            "Month = " + month + " AND Day = " + day + " AND Year = " + year + ";"
            );
            return resultSet.next();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        }
    }


    public static boolean closeDay(Date cutOff) {
        Connection connection;
        try {
            connection = getConnection();
        } catch (SQLException sqlException) {
            return false;
        }

        try {
            connection.setAutoCommit(false);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Statement statement = connection.createStatement();

            statement.addBatch(
                    "UPDATE Invoices " +
                            "SET AccountingPeriod = " + 1 +
                            " WHERE AccountingPeriod = 0" +
                            " AND ReleaseTime < '" + sdf.format(cutOff) + "';"
            );

            statement.addBatch(
                    "UPDATE Payments " +
                            "SET AccountingPeriod = " + 1 +
                            " WHERE AccountingPeriod = 0" +
                            " AND ReleaseTime < '" + sdf.format(cutOff) + "';"
            );

            statement.executeBatch();
            connection.setAutoCommit(true);
            return true;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            System.out.println("Attempting rollback...");
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException sqlException1) {
                sqlException1.printStackTrace();
                System.out.println("Rollback failed. Not good.");
            }
            return false;
        }
    }

    public static EODReport getMonthToDateEODReport(int month, int day, int year) {
        double mtdCash = 0;
        double mtdCharge = 0;
        double mtdFreight = 0;
        double mtdSalesTax = 0;
        double mtdInterStore = 0;
        double mtdTaxable = 0;
        double mtdNonTaxable = 0;
        double mtdTaxTotal = 0;

        try {

            while (day > 0) {
                EODReport add = getEODReport(month, day, year);
                if (add == null) {
                    day--;
                    continue;
                }
                if (!add.isSane())
                    return null;

                mtdCash += add.getSalesSummary().getNetCash();
                mtdCharge += add.getSalesSummary().getNetCharge();
                mtdFreight += add.getSalesSummary().getNetFreight();
                mtdSalesTax += add.getSalesSummary().getNetSalesTax();
                mtdInterStore += add.getSalesSummary().getNetInterStore();
                mtdTaxable += add.getSalesTaxSummary().getNetTaxable();
                mtdNonTaxable += add.getSalesTaxSummary().getNetNonTaxable();
                mtdTaxTotal += add.getSalesTaxSummary().getTotalSalesTax();
                day--;
            }

        } catch (SQLException sqlException) {
            return null;
        }

        return new EODReport(
                new Date(),
                mtdCash,
                mtdCharge,
                mtdFreight,
                mtdSalesTax,
                mtdInterStore,
                mtdTaxable,
                mtdNonTaxable,
                mtdTaxTotal
        );
    }

    public static EODReport getEODReport(int month, int day, int year) throws SQLException {

        Connection connection = getConnection();
        ResultSet result = connection.createStatement().executeQuery(
                "SELECT * FROM EndOfDayReports WHERE " +
                        "Month = " + month + " AND " +
                        "Day = " + day + " AND " +
                        "Year = " + year + ";"
        );
        if (!result.next()) {
            return null;
        }

        return new EODReport(
                new Date(result.getTimestamp("GenerationTime").getTime()),
                result.getDouble("NetCashInvoices"),
                result.getDouble("NetChargeInvoices"),
                result.getDouble("NetFreight"),
                result.getDouble("NetSalesTax"),
                result.getDouble("NetInterStore"),
                result.getDouble("NetTaxable"),
                result.getDouble("NetNonTaxable"),
                result.getDouble("TaxTotal")
        );
    }

}
