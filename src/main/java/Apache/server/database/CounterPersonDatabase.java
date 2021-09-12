package Apache.server.database;

import Apache.objects.CounterPerson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CounterPersonDatabase extends Database {


    public CounterPersonDatabase(Connection connection) {
        super(connection);
    }

    public List<CounterPerson> getCounterPeople(String query) throws SQLException {
        PreparedStatement prepStatement = connection.prepareStatement(
                "SELECT * FROM CounterPeople WHERE CounterPersonNumber = ?;"
        );
        prepStatement.setString(1, query);
        ResultSet resultSet = prepStatement.executeQuery();
        List<CounterPerson> toReturn = new ArrayList<>();
        while (resultSet.next())
            toReturn.add(new CounterPerson(
                    Integer.parseInt(resultSet.getString("CounterPersonNumber")),
                    resultSet.getString("EmployeeName")
            ));
        return toReturn;
    }

}
