package Apache.server.database;

import Apache.objects.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDatabase extends Database {

    public CustomerDatabase(Connection connection) {
        super(connection);
    }

    public List<Customer> getCustomers(String query) throws SQLException {
        List<Customer> customers = new ArrayList<>();
        PreparedStatement prepStatement;
        ResultSet resultSet;

        prepStatement = connection.prepareStatement(
                "SELECT * FROM Customers WHERE CustomerNumber = ?;"
        );
        prepStatement.setString(1, query);
        resultSet = prepStatement.executeQuery();

        if (!resultSet.next()) {
            if (query.equals("*"))
                query = "";
            prepStatement = connection.prepareStatement(
                    "SELECT * FROM Customers WHERE Name LIKE ? ORDER BY Name;"
            );
            prepStatement.setString(1, query + "%");
            resultSet = prepStatement.executeQuery();

            while (resultSet.next())
                customers.add(
                        new Customer(
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
                        )
                );
        } else {
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
            customers.add(customer);
        }
        return customers;
    }
}


