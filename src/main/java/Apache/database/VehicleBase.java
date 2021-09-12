package Apache.database;

import Apache.objects.Vehicle;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static Apache.database.Connector.getConnection;


public class VehicleBase {
    

    public static void addVehicle(Vehicle vehicle) {
        try {
            Connection conn = getConnection();
            conn.createStatement().execute(
                    "INSERT INTO Vehicles(Year, Make, Model, Engine) VALUES" +
                            "(" +
                            "'" + vehicle.year() + "', " +
                            "'" + vehicle.make() + "', " +
                            "'" + vehicle.model() + "', " +
                            "'" + vehicle.engine() + "'" +
                            ");"
            );

            System.out.println("Added vehicle: |"
                    + vehicle.year() + "|"
                    + vehicle.make() + "|"
                    + vehicle.model() + "|"
                    + vehicle.engine() + "|"
            );
        } catch (SQLException sqlException) {

            System.out.println("\n!!![ERROR]!!! FAILED TO ADD VEHICLE: "
                    + vehicle.year() + " "
                    + vehicle.make() + " "
                    + vehicle.model() + " "
                    + vehicle.engine() + "\n");

        }
    }

    public static List<String> getYears(){
        List<String> years = new ArrayList<>();
        try{
            Connection conn = getConnection();
            ResultSet resultSet = conn.createStatement().executeQuery(
                    "SELECT DISTINCT Year FROM Vehicles ORDER BY Year DESC;"
            );
            while(resultSet.next())
                years.add(resultSet.getString("Year"));
            return years;
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            return years;
        }
    }

    public static List<String> getMakes(String year){
        List<String> makes = new ArrayList<>();
        try{
            Connection conn = getConnection();
            ResultSet resultSet = conn.createStatement().executeQuery(
                    "SELECT DISTINCT Make FROM Vehicles WHERE Year = '" + year + "';"
            );
            while(resultSet.next())
                makes.add(resultSet.getString("Make").toUpperCase(Locale.ROOT));
            return makes;
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            return makes;
        }
    }


    public static List<String> getModels(String year, String make){
        List<String> models = new ArrayList<>();
        try{
            Connection conn = getConnection();
            ResultSet resultSet = conn.createStatement().executeQuery(
                    "SELECT DISTINCT Model FROM Vehicles WHERE Year = '" + year + "' " +
                            "AND Make = '" + make +"';"
            );
            while(resultSet.next())
                models.add(resultSet.getString("Model").toUpperCase(Locale.ROOT));
            return models;
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            return models;
        }
    }

    public static List<String> getEngines(String year, String make, String model){
        List<String> engines = new ArrayList<>();
        engines.add("I DON'T KNOW");
        try{
            Connection conn = getConnection();
            ResultSet resultSet = conn.createStatement().executeQuery(
                    "SELECT DISTINCT Engine FROM Vehicles WHERE Year = '" + year + "' " +
                            "AND Make = '" + make +"' AND Model = '" + model + "';"
            );
            while(resultSet.next())
                engines.add(resultSet.getString("Engine").toUpperCase(Locale.ROOT));
            if(engines.size() == 2)
                engines.remove(0);
            return engines;
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            return engines;
        }
    }


}
