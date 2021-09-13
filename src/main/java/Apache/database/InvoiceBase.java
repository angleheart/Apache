package Apache.database;

import Apache.objects.*;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static Apache.database.Connector.*;

public class InvoiceBase {

    public static int getInvoiceNumber() {
        System.out.println("getting invoice number");
        try {
            Connection conn = getConnection();
            ResultSet highestNumber = conn.createStatement().executeQuery(
                    "SELECT MAX(InvoiceNumber) FROM UsedInvoiceNumbers;"
            );
            if (!highestNumber.next()){
                return -1;
            }

            int invoiceNumber = highestNumber.getInt(1);

            do {
                invoiceNumber++;
            } while (!secureNumber(invoiceNumber));

            return invoiceNumber;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return -1;
        }
    }

    private static boolean secureNumber(int invoiceNumber) {
        System.out.println("attempting to secure invoice");
        Connection connection;
        try {
            connection = getConnection();
            connection.createStatement().execute(
                    "INSERT INTO UsedInvoiceNumbers(InvoiceNumber) VALUES(" + invoiceNumber + ");"
            );
            return true;
        } catch (SQLException sqlException) {
            return false;
        }
    }

    public static boolean storeInvoice(Invoice invoice) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Connection connection;

        try{
            connection = getConnection();
        }catch(SQLException sqlException){
            return false;
        }

        try{
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();

            statement.addBatch(
                    "INSERT INTO Invoices(" +
                            "InvoiceNumber, " +
                            "CustomerNumber, " +
                            "CounterPersonNumber, " +
                            "PurchaseOrder, " +
                            "VehicleDescription, " +
                            "ShipTo, " +
                            "ReleaseTime, " +
                            "TransCode, " +
                            "ReleaseCode, " +
                            "Balance, " +
                            "TaxableNet, " +
                            "NonTaxableNet, " +
                            "FreightTotal, " +
                            "TaxRate, " +
                            "AccountingPeriod" +
                            ") VALUES (" +
                            invoice.getInvoiceNumber() + ", " +
                            "'" + invoice.getCustomer().getNumber() + "', " +
                            invoice.getCounterPersonNumber() + ", " +
                            "'" + invoice.getPo() + "', " +
                            "'" + invoice.getVehicleDescription() + "', " +
                            "'" + invoice.getShipTo() + "', " +
                            "'" + sdf.format(invoice.getDate()) + "', " +
                            "'" + invoice.getTransCode() + "', " +
                            invoice.getReleaseCodeInt() + ", " +
                            invoice.getBalance() + ", " +
                            invoice.getTotals().getTaxableNet() + ", " +
                            invoice.getTotals().getNonTaxableNet() + ", " +
                            invoice.getTotals().getFreightTotal() + ", " +
                            invoice.getTotals().getTaxRate() + ", " +
                            invoice.getAccountingPeriod() +
                            ");"
            );


            for (InvoiceLine line : invoice.getLines()) {
                statement.addBatch(
                        "INSERT INTO InvoiceLines(" +
                                "IndexKey, " +
                                "InvoiceNumber, " +
                                "ReleaseTime, " +
                                "TransCode, " +
                                "Quantity, " +
                                "LineCode, " +
                                "PartNumber, " +
                                "Description, " +
                                "ListPrice, " +
                                "Price, " +
                                "CorePrice, " +
                                "TaxCode" +
                                ") VALUES (" +
                                line.getIndexKey() + ", " +
                                invoice.getInvoiceNumber() + ", " +
                                "'" + sdf.format(invoice.getDate()) + "', " +
                                "'" + line.getTransCode() + "', " +
                                line.getQty() + ", " +
                                "'" + line.getMfg() + "', " +
                                "'" + line.getPartNumber() + "', " +
                                "'" + line.getDescription() + "', " +
                                line.getListPrice() + ", " +
                                line.getUnitPrice() + ", " +
                                line.getCorePrice() + ", " +
                                "'" + line.getTx() + "'" +
                                ");"
                );
            }
            statement.executeBatch();
            connection.setAutoCommit(true);
            return true;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            System.out.println("attempting rollback");
            try{
                connection.rollback();
                connection.setAutoCommit(true);
                System.out.println("Rollback successful");
            }catch(SQLException sqlException1){
                sqlException1.printStackTrace();
                System.out.println("Rollback failed");
            }
            return false;
        }
    }


    public static Invoice getInvoiceByNumberFromAll(String numberString) {
        if (numberString.startsWith("D"))
            numberString = numberString.substring(1);
        try {
            return getInvoiceByNumberFromAll(Integer.parseInt(numberString));
        } catch (NumberFormatException e) {
            return null;
        }
    }


    public static Invoice getInvoiceByNumberFromAll(int number) {
        return getInvoiceByNumberFromAll(number, true);
    }

    public static Invoice getInvoiceByNumberFromAll(int number, boolean withLines) {

        try {
            Connection conn = getConnection();

            ResultSet invoiceResults = conn.createStatement().executeQuery(
                    "SELECT * FROM Invoices WHERE InvoiceNumber = " + number + ";"
            );

            if (!invoiceResults.next()){
                return null;
            }

            List<InvoiceLine> lines = new ArrayList<>();

            if(withLines){
                ResultSet lineResults = getConnection().createStatement().executeQuery(
                        "SELECT * FROM InvoiceLines WHERE InvoiceNumber = " + number + " ORDER BY IndexKey;"
                );

                while (lineResults.next()) {
                    lines.add(new InvoiceLine(
                            lineResults.getInt("IndexKey"),
                            lineResults.getInt("InvoiceNumber"),
                            new java.util.Date(lineResults.getTimestamp("ReleaseTime").getTime()),
                            lineResults.getString("TransCode"),
                            lineResults.getInt("Quantity"),
                            lineResults.getString("LineCode"),
                            lineResults.getString("PartNumber"),
                            lineResults.getString("Description"),
                            lineResults.getDouble("ListPrice"),
                            lineResults.getDouble("Price"),
                            lineResults.getDouble("CorePrice"),
                            lineResults.getString("TaxCode")
                    ));
                }
            }

            Customer customer = CustomerBase.getCustomerByNumber(invoiceResults.getString("CustomerNumber"));

            Invoice invoice = new Invoice(
                    invoiceResults.getInt("InvoiceNumber"),
                    customer,
                    invoiceResults.getInt("CounterPersonNumber"),
                    invoiceResults.getString("PurchaseOrder"),
                    invoiceResults.getString("VehicleDescription"),
                    invoiceResults.getString("ShipTo"),
                    new java.util.Date(invoiceResults.getTimestamp("ReleaseTime").getTime()),
                    invoiceResults.getString("TransCode"),
                    invoiceResults.getInt("ReleaseCode"),
                    invoiceResults.getDouble("Balance"),
                    new InvoiceTotals(
                            invoiceResults.getDouble("TaxableNet"),
                            invoiceResults.getDouble("NonTaxableNet"),
                            invoiceResults.getDouble("FreightTotal"),
                            invoiceResults.getDouble("TaxRate")
                    ),
                    invoiceResults.getInt("AccountingPeriod"),
                    lines
            );
            return invoice;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        }
    }


    public static List<Transferable> getAllOpenInvoices() {
        List<Transferable> invoices = new ArrayList<>();
        try {
            Connection conn = getConnection();
            ResultSet invoiceResults = conn.createStatement().executeQuery(
                    "SELECT * FROM Invoices WHERE AccountingPeriod = 0 ORDER BY ReleaseTime DESC;"
            );
            if (!invoiceResults.next()){
                return invoices;
            }

            do {
                Invoice invoice = getInvoiceByNumberFromAll(invoiceResults.getString("InvoiceNumber"));
                if (invoice == null){
                    return null;
                }
                invoices.add(invoice);
            } while (invoiceResults.next());
            return invoices;
        } catch (SQLException exception) {
            return null;
        }
    }


    public static boolean forceDeleteInvoice(int invoiceNumber) {
        Connection connection;

        try {
            connection = getConnection();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return false;
        }

        try {
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();

            statement.addBatch(
                    "DELETE FROM InvoiceLines WHERE InvoiceNumber = " + invoiceNumber + ";"
            );
            statement.addBatch(
                    "DELETE FROM Invoices WHERE InvoiceNumber = " + invoiceNumber + ";"
            );
            statement.executeBatch();
            connection.setAutoCommit(true);
            return true;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException sqlException1) {
                sqlException.printStackTrace();
            }
            return false;
        }
    }


    public static boolean setReleaseCode(Invoice invoice, int code) {
        if (!invoice.isVoidable())
            return false;

        Connection connection;
        try {
            connection = getConnection();
        } catch (SQLException sqlException) {
            return false;
        }
        try {
            connection = getConnection();
            Statement statement = connection.createStatement();
            connection.setAutoCommit(false);

            statement.addBatch("UPDATE Invoices " +
                    "SET ReleaseCode = " + code + " WHERE InvoiceNumber = " + invoice.getInvoiceNumber() + ";");
            if (code == 31)
                statement.addBatch("UPDATE Invoices " +
                        "SET Balance = " + invoice.getTotals().getGrandTotal() +
                        " WHERE InvoiceNumber = " +  invoice.getInvoiceNumber() +";");
            else
                statement.addBatch("UPDATE Invoices " +
                        "SET Balance = 0 WHERE InvoiceNumber = " + invoice.getInvoiceNumber() + ";");

            statement.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
            return true;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    public static List<Invoice> getPayableInvoices(Customer customer) {
        List<Invoice> invoices = new ArrayList<>();
        try {
            Connection conn = getConnection();
            ResultSet invoiceResults = conn.createStatement().executeQuery(
                    "SELECT * FROM Invoices WHERE Balance != 0 AND AccountingPeriod != 0 " +
                            "AND CustomerNumber = '" + customer.getNumber() + "' " +
                            "ORDER BY ReleaseTime;"
            );

            while (invoiceResults.next()) {
                invoices.add(
                        new Invoice(
                                invoiceResults.getInt("InvoiceNumber"),
                                customer,
                                invoiceResults.getInt("CounterPersonNumber"),
                                invoiceResults.getString("PurchaseOrder"),
                                invoiceResults.getString("VehicleDescription"),
                                invoiceResults.getString("ShipTo"),
                                new java.util.Date(invoiceResults.getTimestamp("ReleaseTime").getTime()),
                                invoiceResults.getString("TransCode"),
                                invoiceResults.getInt("ReleaseCode"),
                                invoiceResults.getDouble("Balance"),
                                new InvoiceTotals(
                                        invoiceResults.getDouble("TaxableNet"),
                                        invoiceResults.getDouble("NonTaxableNet"),
                                        invoiceResults.getDouble("FreightTotal"),
                                        invoiceResults.getDouble("TaxRate")
                                ),
                                invoiceResults.getInt("AccountingPeriod"),
                                new ArrayList<>()
                        )
                );
            }
            return invoices;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        }
    }

    public static boolean forceInvoice(
            int invoiceNumber,
            String customerNumber,
            int month,
            int day,
            int year,
            double balance
    ) {

        int accountingPeriod;
        if (month == 7 && year == 2021)
            accountingPeriod = 2;
        else if (month == 6 && year == 2021)
            accountingPeriod = 3;
        else if (month < 6 && year == 2021)
            accountingPeriod = 4;
        else
            return false;

        try {
            Connection connection = getConnection();
            Invoice invoice = getInvoiceByNumberFromAll(invoiceNumber);
            if (invoice != null) return false;

            String monthStr = Integer.toString(month);
            if (month < 10)
                monthStr = "0" + monthStr;

            String dayStr = Integer.toString(day);
            if (day < 10)
                dayStr = "0" + dayStr;

            String yearStr = Integer.toString(year);
            if (yearStr.length() != 4)
                return false;
            if (dayStr.length() != 2)
                return false;
            if (monthStr.length() != 2)
                return false;

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date;
            try {
                date = simpleDateFormat.parse(dayStr + "/" + monthStr + "/" + yearStr);
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }

            SimpleDateFormat sdfFinal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            connection.createStatement().execute(
                    "INSERT INTO Invoices(" +
                            "InvoiceNumber, " +
                            "CustomerNumber, " +
                            "CounterPersonNumber, " +
                            "PurchaseOrder, " +
                            "VehicleDescription, " +
                            "ShipTo, " +
                            "ReleaseTime, " +
                            "TransCode, " +
                            "ReleaseCode, " +
                            "Balance, " +
                            "TaxableNet, " +
                            "NonTaxableNet, " +
                            "FreightTotal, " +
                            "TaxRate, " +
                            "AccountingPeriod" +
                            ") VALUES (" +
                            invoiceNumber + ", " +
                            "'" + customerNumber + "', " +
                            "5" + ", " +
                            "''" + ", " +
                            "''" + ", " +
                            "''" + ", " +
                            "'" + sdfFinal.format(date) + "', " +
                            "'SAL', " +
                            "31, " +
                            balance + ", " +
                            "0, 0, 0, .08, " +
                            accountingPeriod +
                            ");"
            );
            return true;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return false;
        }
    }


}
