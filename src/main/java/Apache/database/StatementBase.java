package Apache.database;

import Apache.console.eom.CustomerStatement;
import Apache.console.eom.StatementLine;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static Apache.database.Connector.getConnection;

public class StatementBase {


    public static boolean purge(int month, int year) {
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
                    "DELETE FROM StatementLines WHERE Month = " + month +
                            " AND Year = " + year + ";"
            );
            statement.addBatch(
                    "DELETE FROM Statements WHERE Month = " + month +
                            " AND Year = " + year + ";"
            );
            statement.executeBatch();
            connection.setAutoCommit(true);
            return true;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            System.out.println("Attempting rollback");
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException sqlException1) {
                sqlException1.printStackTrace();
                System.out.println("Rollback failed");
            }
            return false;
        }
    }


    public static List<CustomerStatement> getWrittenStatements(int month, int year) {
        List<CustomerStatement> toReturn = new ArrayList<>();
        try {
            ResultSet resultSet = getConnection().createStatement().executeQuery(
                    "SELECT DISTINCT CustomerNumber FROM Statements WHERE Month = " + month +
                            " AND Year = " + year + ";"
            );
            while (resultSet.next())
                toReturn.add(getWrittenStatement(month, year, resultSet.getString("CustomerNumber")));
            return toReturn;
        } catch (SQLException sqlException) {
            return null;
        }
    }


    public static CustomerStatement getWrittenStatement(int month, int year, String customerNumber) {
        try {
            Connection connection = getConnection();
            ResultSet resultSet = connection.createStatement().executeQuery(
                    "SELECT * FROM Statements WHERE Month = " + month +
                            " AND Year = " + year +
                            " AND CustomerNumber = '" + customerNumber + "';"
            );

            if (!resultSet.next()){
                return null;
            }

            ResultSet lineSet = connection.createStatement().executeQuery(
                    "SELECT * FROM StatementLines WHERE Month = " + month +
                            " AND Year = " + year +
                            " AND CustomerNumber = '" + customerNumber + "'" +
                            " ORDER BY IndexKey;"
            );

            List<StatementLine> lines = new ArrayList<>();
            while (lineSet.next()) {
                lines.add(
                        new StatementLine(
                                lineSet.getInt("IndexKey"),
                                new String[]{
                                        lineSet.getString("FromDate"),
                                        lineSet.getString("InvoiceNumber"),
                                        lineSet.getString("Detail"),
                                        lineSet.getString("OriginalAmount"),
                                        lineSet.getString("AppliedAmount"),
                                        lineSet.getString("BalanceAmount"),
                                        lineSet.getString("DueAmount")
                                }
                        )
                );
            }

            return new CustomerStatement(
                    CustomerBase.getCustomerByNumber(customerNumber),
                    new java.util.Date(resultSet.getTimestamp("GenerationTime").getTime()),
                    resultSet.getDouble("Current"),
                    resultSet.getDouble("30_Days"),
                    resultSet.getDouble("60_Days"),
                    resultSet.getDouble("90_Days"),
                    resultSet.getDouble("TotalPaid"),
                    lines
            );
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        }
    }

    public static boolean writeStatements(int month, int year, List<CustomerStatement> customerStatements) {
        if (customerStatements == null) {
            System.out.println("Customer statements is null");
            return false;
        }

        Connection connection;

        try {
            connection = Connector.getConnection();
            connection.setAutoCommit(false);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return false;
        }


        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Statement statement = connection.createStatement();

            for (CustomerStatement customerStatement : customerStatements) {

                System.out.println(customerStatement.getCustomer().getName());
                statement.addBatch(
                        "INSERT INTO Statements(" +
                                "Month, " +
                                "Year, " +
                                "CustomerNumber, " +
                                "GenerationTime, " +
                                "Current, " +
                                "30_Days, " +
                                "60_Days, " +
                                "90_Days," +
                                "TotalPaid" +
                                ") VALUES (" +
                                month + ", " +
                                year + ", " +
                                "'" + customerStatement.getCustomer().getNumber() + "', " +
                                "'" + sdf.format(customerStatement.getDate()) + "', " +
                                customerStatement.getBalCurr() + ", " +
                                customerStatement.getBal30() + ", " +
                                customerStatement.getBal60() + ", " +
                                customerStatement.getBal90() + ", " +
                                customerStatement.getTotalPaid() +
                                ");"
                );

                for (StatementLine statementLine : customerStatement.getStatementLines()) {

                    statement.addBatch(
                            "INSERT INTO StatementLines(" +
                                    "Month, " +
                                    "Year, " +
                                    "CustomerNumber, " +
                                    "IndexKey, " +
                                    "FromDate, " +
                                    "InvoiceNumber, " +
                                    "Detail, " +
                                    "OriginalAmount, " +
                                    "AppliedAmount, " +
                                    "BalanceAmount," +
                                    "DueAmount" +
                                    ") VALUES (" +
                                    month + ", " +
                                    year + ", " +
                                    "'" + customerStatement.getCustomer().getNumber() + "', " +
                                    statementLine.indexKey() + ", " +
                                    "'" + statementLine.columns()[0] + "', " +
                                    "'" + statementLine.columns()[1] + "', " +
                                    "'" + statementLine.columns()[2] + "', " +
                                    "'" + statementLine.columns()[3] + "', " +
                                    "'" + statementLine.columns()[4] + "', " +
                                    "'" + statementLine.columns()[5] + "', " +
                                    "'" + statementLine.columns()[6] + "'" +
                                    ");"
                    );
                }

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
