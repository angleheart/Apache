package Apache.database;

import Apache.config.Config;

import java.sql.*;

public class Connector {

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed())
            connection = DriverManager.getConnection(
                    Config.getDatabaseConnectionUrl(),
                    Config.DATABASE_USERNAME,
                    Config.DATABASE_PASSWORD
            );
        return connection;
    }

    public static boolean canConnect() {
        try {
            getConnection();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

}
