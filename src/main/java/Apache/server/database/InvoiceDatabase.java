package Apache.server.database;

import Apache.config.Config;
import Apache.database.CustomerBase;
import Apache.objects.*;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static Apache.database.Connector.getConnection;

public class InvoiceDatabase extends Database {

    private final CustomerDatabase customerDatabase;

    public InvoiceDatabase(){
        this.customerDatabase = new CustomerDatabase();
    }

    public List<Transferable> getOpenInvoices() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT InvoiceNumber FROM Invoices WHERE AccountingPeriod = 0;"
        );
        ResultSet openNumbers = preparedStatement.executeQuery();
        List<Transferable> toReturn = new ArrayList<>();
        while(openNumbers.next())
            toReturn.add(getInvoiceByNumber(openNumbers.getInt("InvoiceNumber")));
        return toReturn;
    }

    public Transferable getInvoiceByNumber(int invoiceNumber) throws SQLException {
        return getInvoiceByNumber(invoiceNumber, null);
    }

    public Transferable getInvoiceByNumber(int invoiceNumber, Customer customer) throws SQLException {

        PreparedStatement prepStatement = connection.prepareStatement(
                "SELECT * FROM Invoices WHERE InvoiceNumber = ?;"
        );
        prepStatement.setInt(1, invoiceNumber);
        ResultSet invoiceResults = prepStatement.executeQuery();
        if(!invoiceResults.next())
            return null;

        prepStatement = connection.prepareStatement(
                "SELECT * FROM InvoiceLines WHERE InvoiceNumber = ? ORDER BY IndexKey;"
        );
        prepStatement.setInt(1, invoiceNumber);
        ResultSet lineResults = prepStatement.executeQuery();

        List<TransferableInvoiceLine> transferableInvoiceLines = new ArrayList<>();

        while (lineResults.next())
            transferableInvoiceLines.add(
                    new TransferableInvoiceLine(
                            lineResults.getInt("IndexKey"),
                            lineResults.getInt("InvoiceNumber"),
                            lineResults.getTimestamp("ReleaseTime").getTime(),
                            lineResults.getString("TransCode"),
                            lineResults.getString("LineCode"),
                            lineResults.getInt("Quantity"),
                            lineResults.getString("PartNumber"),
                            lineResults.getString("Description"),
                            lineResults.getDouble("ListPrice"),
                            lineResults.getDouble("Price"),
                            lineResults.getDouble("CorePrice"),
                            lineResults.getString("TaxCode")
                    )
            );

        if(customer == null){
            List<Customer> customers = customerDatabase.getCustomers(
                    invoiceResults.getString("CustomerNumber")
            );
            customer = customers.get(0);
        }

        return new TransferableInvoice(
                transferableInvoiceLines,
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
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException sqlException1) {
                sqlException1.printStackTrace();
            }
            return false;
        }
    }
}
