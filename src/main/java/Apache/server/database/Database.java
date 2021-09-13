package Apache.server.database;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class Database implements AutoCloseable {

    private static DataSource DATA_SOURCE;
    Connection connection;

    public static void setDataSource(DataSource dataSource){
        DATA_SOURCE = dataSource;
    }

    public Database(){
        try {
            this.connection = DATA_SOURCE.getConnection();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            System.out.println("[Error] Failed to establish database connection");
        }
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }
}