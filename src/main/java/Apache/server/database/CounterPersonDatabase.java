package Apache.server.database;

import Apache.objects.CounterPerson;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class CounterPersonDatabase extends Database {

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

    public boolean addCounterPerson(CounterPerson counterPerson) {
        try{
            PreparedStatement prepStatement = connection.prepareStatement(
                    "INSERT INTO CounterPeople(CounterPerson, EmployeeName) VALUES(?, ?);"
            );
            prepStatement.setInt(1, counterPerson.getNumber());
            prepStatement.setString(2, counterPerson.getName());
            prepStatement.execute();
            return true;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCounterPerson(int counterPersonNumber){
        try{
            PreparedStatement prepStatement = connection.prepareStatement(
                    "DELETE FROM CounterPeople WHERE CounterPersonNumber = ?;"
            );
            prepStatement.setInt(1, counterPersonNumber);
            prepStatement.execute();
            return true;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

}
