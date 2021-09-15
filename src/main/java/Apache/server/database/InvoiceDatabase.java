package Apache.server.database;

import Apache.objects.*;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static Apache.database.Connector.getConnection;

public class InvoiceDatabase extends Database {

    public List<Invoice> getOpenInvoices() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT InvoiceNumber FROM Invoices WHERE AccountingPeriod = 0;"
        );
        ResultSet openNumbers = preparedStatement.executeQuery();
        List<Invoice> toReturn = new ArrayList<>();
        while (openNumbers.next())
            toReturn.add(getInvoiceByNumber(openNumbers.getInt("InvoiceNumber")));
        return toReturn;
    }

    public Invoice getInvoiceByNumber(int invoiceNumber, boolean withLines) throws SQLException {
        return getInvoiceByNumber(invoiceNumber, null, withLines);
    }

    public Invoice getInvoiceByNumber(int invoiceNumber) throws SQLException {
        return getInvoiceByNumber(invoiceNumber, null, true);
    }

    public Invoice getInvoiceByNumber(int invoiceNumber, Customer customer, boolean withLines) throws SQLException {

        PreparedStatement prepStatement = connection.prepareStatement(
                "SELECT * FROM Invoices WHERE InvoiceNumber = ?;"
        );
        prepStatement.setInt(1, invoiceNumber);
        ResultSet invoiceResults = prepStatement.executeQuery();
        if (!invoiceResults.next())
            return null;

        List<InvoiceLine> invoiceLines = new ArrayList<>();

        if (withLines) {
            prepStatement = connection.prepareStatement(
                    "SELECT * FROM InvoiceLines WHERE InvoiceNumber = ? ORDER BY IndexKey;"
            );
            prepStatement.setInt(1, invoiceNumber);
            ResultSet lineResults = prepStatement.executeQuery();
            while (lineResults.next())
                invoiceLines.add(
                        new InvoiceLine(
                                lineResults.getInt("IndexKey"),
                                lineResults.getInt("InvoiceNumber"),
                                lineResults.getTimestamp("ReleaseTime").getTime(),
                                lineResults.getString("TransCode"),
                                lineResults.getInt("Quantity"),
                                lineResults.getString("LineCode"),
                                lineResults.getString("PartNumber"),
                                lineResults.getString("Description"),
                                lineResults.getDouble("ListPrice"),
                                lineResults.getDouble("Price"),
                                lineResults.getDouble("CorePrice"),
                                lineResults.getString("TaxCode")
                        )
                );
        }

        if (customer == null) {
            List<Customer> customers;
            try (CustomerDatabase customerDatabase = new CustomerDatabase()) {
                customers = customerDatabase.getCustomers(
                        invoiceResults.getString("CustomerNumber"));
            }
            customer = customers.get(0);
        }

        return new Invoice(
                invoiceLines,
                new InvoiceTotals(
                        invoiceResults.getDouble("TaxableNet"),
                        invoiceResults.getDouble("NonTaxableNet"),
                        invoiceResults.getDouble("FreightTotal"),
                        invoiceResults.getDouble("TaxRate")
                ),
                customer,
                invoiceResults.getString("VehicleDescription"),
                invoiceResults.getString("ShipTo"),
                invoiceResults.getInt("InvoiceNumber"),
                invoiceResults.getInt("AccountingPeriod"),
                invoiceResults.getTimestamp("ReleaseTime").getTime(),
                invoiceResults.getString("PurchaseOrder"),
                invoiceResults.getInt("CounterPersonNumber"),
                invoiceResults.getDouble("Balance"),
                invoiceResults.getInt("ReleaseCode"),
                invoiceResults.getString("TransCode")
        );
    }

    public int getInvoiceNumber() {
        try {
            ResultSet highestNumber = connection.createStatement().executeQuery(
                    "SELECT MAX(InvoiceNumber) FROM UsedInvoiceNumbers;"
            );
            if (!highestNumber.next())
                return -1;

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

    private boolean secureNumber(int invoiceNumber) {
        try {
            connection.createStatement().execute(
                    "INSERT INTO UsedInvoiceNumbers(InvoiceNumber) VALUES(" + invoiceNumber + ");"
            );
            return true;
        } catch (SQLException sqlException) {
            return false;
        }
    }

    public boolean storeInvoice(Invoice invoice) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
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
                            invoice.getReleaseCode() + ", " +
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
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException sqlException1) {
                sqlException1.printStackTrace();
            }
            return false;
        }
    }

    public List<Invoice> getPayableInvoices(String customerNumber) throws SQLException {
        PreparedStatement prepStatement = connection.prepareStatement(
                "SELECT InvoiceNumber FROM Invoices WHERE Balance != 0 AND AccountingPeriod != 0 " +
                        "AND CustomerNumber = ? ORDER BY ReleaseTime;"
        );
        prepStatement.setString(1, customerNumber);
        ResultSet resultSet = prepStatement.executeQuery();
        List<Invoice> invoices = new ArrayList<>();
        while (resultSet.next())
            invoices.add(getInvoiceByNumber(resultSet.getInt("InvoiceNumber"), false));
        return invoices;
    }

    public boolean voidInvoice(int invoiceNumber) {
        try{
            connection.setAutoCommit(false);
            PreparedStatement prepInvoiceLineDelete = connection.prepareStatement(
                    "DELETE FROM InvoiceLines WHERE InvoiceNumber = ?; "
            );
            PreparedStatement prepInvoiceDelete = connection.prepareStatement(
                    "DELETE FROM Invoices WHERE InvoiceNumber = ?;"
            );
            prepInvoiceLineDelete.setInt(1, invoiceNumber);
            prepInvoiceDelete.setInt(1, invoiceNumber);
            prepInvoiceLineDelete.execute();
            prepInvoiceDelete.execute();
            connection.commit();
            connection.setAutoCommit(true);
            return true;
        }catch(SQLException e){
            try{
                connection.rollback();
                connection.setAutoCommit(true);
            }catch(SQLException e2){
                e2.printStackTrace();
            }
        }
        return false;
    }

}