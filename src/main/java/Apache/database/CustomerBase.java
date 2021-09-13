package Apache.database;

import Apache.objects.Customer;
import Apache.objects.Transferable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static Apache.database.Connector.*;

public class CustomerBase {

    public static Customer getCustomerByNumber(String customerNumber) {
        try {
            Connection conn = getConnection();
            ResultSet resultSet = conn.createStatement().executeQuery(
                    "SELECT * FROM Customers WHERE CustomerNumber = '" + customerNumber + "'");
            if (!resultSet.next()){
                return null;
            }

            Customer customer = new Customer(
                    resultSet.getString("CustomerNumber"),
                    resultSet.getString("Name"),
                    resultSet.getString("Address"),
                    resultSet.getString("City"),
                    resultSet.getString("State"),
                    resultSet.getString("Zip"),
                    resultSet.getString("PhoneNumber"),
                    resultSet.getDouble("PriceMultiplier"),
                    resultSet.getBoolean("Taxable"),
                    resultSet.getBoolean("PayByInvoice")
            );
            return customer;
        } catch (SQLException sqlException) {
            return null;
        }
    }

    public static List<Transferable> getCustomersByName(String customerName) {
        List<Transferable> customers = new ArrayList<>();
        try {
            Connection conn = getConnection();
            ResultSet resultSet = conn.createStatement().executeQuery(
                    "SELECT * FROM Customers WHERE Name LIKE '%" + customerName +
                    "%' ORDER BY Name");
            if (!resultSet.next()){
                conn.close();
                return customers;
            }
            do {
                customers.add(new Customer(
                        resultSet.getString("CustomerNumber"),
                        resultSet.getString("Name"),
                        resultSet.getString("Address"),
                        resultSet.getString("City"),
                        resultSet.getString("State"),
                        resultSet.getString("Zip"),
                        resultSet.getString("PhoneNumber"),
                        resultSet.getDouble("PriceMultiplier"),
                        resultSet.getBoolean("Taxable"),
                        resultSet.getBoolean("PayByInvoice")
                ));
            } while (resultSet.next());
            return customers;
        } catch (SQLException sqlException) {
            return customers;
        }
    }

    public static boolean addNewCustomer(Customer customer) {
        try {
            Connection conn = getConnection();
            conn.createStatement().execute(
                    "INSERT INTO Customers(" +
                            "CustomerNumber, " +
                            "Name, " +
                            "Address, " +
                            "City, " +
                            "State, " +
                            "Zip, " +
                            "PhoneNumber, " +
                            "PriceMultiplier, " +
                            "Taxable, " +
                            "PayByInvoice" +
                            ") VALUES (" +
                            "'" + customer.getNumber() + "', " +
                            "'" + customer.getName() + "', " +
                            "'" + customer.getAddress() + "', " +
                            "'" + customer.getCity() + "', " +
                            "'" + customer.getState() + "', " +
                            "'" + customer.getZip() + "', " +
                            "'" + customer.paysByInvoice() + "', " +
                            customer.getPriceMultiplier() + ", " +
                            customer.isTaxable() + ", " +
                            customer.paysByInvoice() +
                            ");"
            );
            return true;
        } catch (SQLException sqlException) {
            return false;
        }
    }

    public static boolean updateName(String customerNumber, String customerName) {
        try {
            Connection conn = getConnection();
            conn.createStatement().execute(
                    "UPDATE Customers " +
                            "SET Name = '" + customerName + "' WHERE CustomerNumber = '" + customerNumber + "';"
            );
            return true;
        } catch (SQLException sqlException) {
            return false;
        }
    }


    public static boolean updateAddress(
            String customerNumber,
            String address,
            String city,
            String state,
            String zip
    ) {
        try {
            Connection conn = getConnection();
            conn.setAutoCommit(false);
            Statement statement = conn.createStatement();
            
            statement.addBatch(
                    "UPDATE Customers " +
                            "SET Address = '" + address + "' WHERE CustomerNumber = '" + customerNumber + "';"
            );
            statement.addBatch(
                    "UPDATE Customers " +
                            "SET City = '" + city + "' WHERE CustomerNumber = '" + customerNumber + "';"
            );
            statement.addBatch(
                    "UPDATE Customers " +
                            "SET State = '" + state + "' WHERE CustomerNumber = '" + customerNumber + "';"
            );
            statement.addBatch(
                    "UPDATE Customers " +
                            "SET Zip = '" + zip + "' WHERE CustomerNumber = '" + customerNumber + "';"
            );
            statement.executeBatch();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException sqlException) {
            return false;
        }
    }


    public static boolean updatePricing(String customerNumber, double pricing) {
        try {
            Connection conn = getConnection();

            conn.createStatement().execute(
                    "UPDATE Customers " +
                            "SET PriceMultiplier = " + pricing + " WHERE CustomerNumber = '" + customerNumber + "';"
            );
            return true;
        } catch (SQLException sqlException) {
            return false;
        }
    }


    public static boolean updateTaxable(String customerNumber, boolean taxable) {
        try {
            Connection conn = getConnection();

            conn.createStatement().execute(
                    "UPDATE Customers " +
                            "SET Taxable = " + taxable + " WHERE CustomerNumber = '" + customerNumber + "';"
            );
            return true;
        } catch (SQLException sqlException) {
            return false;
        }
    }

    public static boolean updatePhoneNumber(String customerNumber, String phoneNumber) {
        try {
            Connection conn = getConnection();

            conn.createStatement().execute(
                    "UPDATE Customers " +
                            "SET PhoneNumber = '" + phoneNumber + "' WHERE CustomerNumber = '" + customerNumber + "';"
            );
            return true;
        } catch (SQLException sqlException) {
            return false;
        }
    }


}
