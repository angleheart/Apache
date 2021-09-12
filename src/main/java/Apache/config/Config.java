package Apache.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class Config {

    public static int SERVER_RUN_PORT;
    public static String DATABASE_URL;
    public static String DATABASE_SERVER;
    public static String DATABASE_PORT;
    public static String DATABASE_NAME;
    public static String DATABASE_USERNAME;
    public static String DATABASE_PASSWORD;
    public static String TMP_PATH = "";
    public static String PRINTER_NAME;
    public static boolean STAND_ALONE;
    public static String TARGET_SERVER;
    public static String TARGET_PORT;
    public static double DEFAULT_COST_MULTIPLIER;
    public static double LIST_PRICE_MULTIPLIER;
    public static double TAX_RATE;
    public static boolean ENABLE_PRINT;

    public static boolean parse(String path){
        try {
            File file = new File(path);
            Properties properties = new Properties();
            properties.load(new FileInputStream(file));

            SERVER_RUN_PORT = Integer.parseInt(properties.getProperty("SERVER_RUN_PORT"));
            TMP_PATH = properties.getProperty("TMP_PATH");
            PRINTER_NAME = properties.getProperty("PRINTER_NAME");

            DATABASE_URL = properties.getProperty("DATABASE_URL");
            DATABASE_SERVER = properties.getProperty("DATABASE_SERVER");
            DATABASE_PORT = properties.getProperty("DATABASE_PORT");
            DATABASE_NAME = properties.getProperty("DATABASE_NAME");
            DATABASE_USERNAME = properties.getProperty("DATABASE_USERNAME");
            DATABASE_PASSWORD = properties.getProperty("DATABASE_PASSWORD");

            STAND_ALONE = Boolean.parseBoolean(properties.getProperty("STAND_ALONE"));
            TARGET_SERVER = properties.getProperty("TARGET_SERVER");
            TARGET_PORT = properties.getProperty("TARGET_PORT");
            DEFAULT_COST_MULTIPLIER = Double.parseDouble(properties.getProperty("DEFAULT_COST_MULTIPLIER"));
            LIST_PRICE_MULTIPLIER = Double.parseDouble(properties.getProperty("LIST_PRICE_MULTIPLIER"));
            TAX_RATE = Double.parseDouble(properties.getProperty("TAX_RATE"));
            ENABLE_PRINT = Boolean.parseBoolean(properties.getProperty("ENABLE_PRINT"));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static DataSource getDataSource() {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(getDatabaseConnectionUrl());
            config.setUsername(DATABASE_USERNAME);
            config.setPassword(DATABASE_PASSWORD);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            HikariDataSource dataSource = new HikariDataSource(config);
            dataSource.getConnection().close();
            return new HikariDataSource(config);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getDatabaseConnectionUrl(){
        return DATABASE_URL + DATABASE_SERVER + ":" + DATABASE_PORT + "/" + DATABASE_NAME;
    }

    public static String getTargetServerURL(){
        return "http://" + TARGET_SERVER + ":" + TARGET_PORT;
    }


}
