package Apache.server.database;

import Apache.config.Config;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class Database implements AutoCloseable {

    private static DataSource DATA_SOURCE;
    Connection connection;

    public static boolean init(){
        try{
            DATA_SOURCE = Config.getDataSource();;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        if(DATA_SOURCE == null)
            return false;
        try{
            DATA_SOURCE.getConnection().close();
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public Database(){
        try {
            this.connection = DATA_SOURCE.getConnection();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }
}