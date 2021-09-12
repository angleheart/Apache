package Apache.server.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class Database {

    final Connection connection;

    public Database(Connection connection) {
        this.connection = connection;
    }

    public Statement createStatement() throws SQLException {
        return connection.createStatement();
    }

}