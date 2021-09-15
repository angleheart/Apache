package Apache.server.database;

import Apache.objects.Vehicle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VehicleDatabase extends Database {

    public boolean addVehicle(Vehicle vehicle) {
        try {
            PreparedStatement prepStatement = connection.prepareStatement(
                    "INSERT INTO Vehicles(Year, Make, Model, Engine) " +
                            "VALUES(?, ?, ?, ?);"
            );
            prepStatement.setString(1, vehicle.year());
            prepStatement.setString(2, vehicle.make());
            prepStatement.setString(3, vehicle.model());
            prepStatement.setString(4, vehicle.engine());
            prepStatement.execute();

            System.out.println("Added vehicle: |"
                    + vehicle.year() + "|"
                    + vehicle.make() + "|"
                    + vehicle.model() + "|"
                    + vehicle.engine() + "|"
            );
            return true;
        } catch (SQLException sqlException) {

            System.out.println("\n!!![ERROR]!!! FAILED TO ADD VEHICLE: "
                    + vehicle.year() + " "
                    + vehicle.make() + " "
                    + vehicle.model() + " "
                    + vehicle.engine() + "\n");

        }
        return false;
    }

    public List<String> getYears() throws SQLException {
        PreparedStatement prepStatement = connection.prepareStatement(
                "SELECT DISTINCT Year FROM Vehicles ORDER BY Year DESC;"
        );
        ResultSet resultSet = prepStatement.executeQuery();
        List<String> years = new ArrayList<>();
        while (resultSet.next())
            years.add(resultSet.getString("Year"));
        return years;
    }

    public List<String> getMakes(String year) throws SQLException {
        PreparedStatement prepStatement = connection.prepareStatement(
                "SELECT DISTINCT Make From Vehicles WHERE Year = ?;"
        );
        prepStatement.setString(1, year);
        ResultSet resultSet = prepStatement.executeQuery();
        List<String> makes = new ArrayList<>();
        while (resultSet.next())
            makes.add(resultSet.getString("Make").toUpperCase(Locale.ROOT));
        return makes;
    }

    public List<String> getModels(String year, String make) throws SQLException {
        PreparedStatement prepStatement = connection.prepareStatement(
                "SELECT DISTINCT Model FROM Vehicles WHERE Year = ? AND Make = ?;"
        );
        prepStatement.setString(1, year);
        prepStatement.setString(2, make);
        ResultSet resultSet = prepStatement.executeQuery();
        List<String> models = new ArrayList<>();
        while (resultSet.next())
            models.add(resultSet.getString("Model").toUpperCase(Locale.ROOT));
        return models;
    }

    public List<String> getEngines(String year, String make, String model) throws SQLException {
        PreparedStatement prepStatement = connection.prepareStatement(
                "SELECT DISTINCT Engine FROM Vehicles WHERE Year = ? AND Make = ? AND Model = ?;"
        );
        prepStatement.setString(1, year);
        prepStatement.setString(2, make);
        prepStatement.setString(3, model);
        ResultSet resultSet = prepStatement.executeQuery();
        List<String> engines = new ArrayList<>();
        while (resultSet.next())
            engines.add(resultSet.getString("Engine").toUpperCase(Locale.ROOT));
        return engines;
    }

}
