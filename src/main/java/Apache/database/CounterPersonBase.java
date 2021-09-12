package Apache.database;

import Apache.objects.CounterPerson;
import Apache.objects.Selectable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static Apache.database.Connector.*;

public class CounterPersonBase {

    public static CounterPerson getCounterPersonByNumber(String cpn) {
        try {
            Connection conn = getConnection();
            ResultSet resultSet = conn.createStatement().executeQuery(
                    "SELECT * FROM CounterPeople WHERE CounterPersonNumber = " + cpn);
            resultSet.next();
            return new CounterPerson(
                    Integer.parseInt(resultSet.getString("CounterPersonNumber")),
                    resultSet.getString("EmployeeName")
            );
        } catch (SQLException sqlException) {
            return null;
        }
    }


    public static List<Selectable> getAllCounterPeople() {
        List<Selectable> counterPeople = new ArrayList<>();
        try {
            Connection conn = getConnection();

            ResultSet resultSet = conn.createStatement().executeQuery("SELECT * FROM CounterPeople");
            while (resultSet.next()) {
                counterPeople.add(
                        new CounterPerson(
                                resultSet.getInt("CounterPersonNumber"),
                                resultSet.getString("EmployeeName")
                        )
                );
            }
            return counterPeople;
        } catch (SQLException sqlException) {
            return counterPeople;
        }
    }

    public static boolean addCounterPerson(CounterPerson counterPerson) {
        try {
            Connection conn = getConnection();
            conn.createStatement().execute(
                    "INSERT INTO CounterPeople(CounterPersonNumber, EmployeeName) VALUES(" +
                            counterPerson.getNumber() + ", '" +
                            counterPerson.getName() + "'" +
                            ");"
            );
            return true;
        } catch (SQLException sqlException) {
            return false;
        }
    }

    public static boolean deleteCounterPerson(int counterPersonNumber){
        try{
            Connection conn = getConnection();
            conn.createStatement().execute(
                    "DELETE FROM CounterPeople WHERE CounterPersonNumber = " + counterPersonNumber + ";"
            );
            return true;
        }catch(Exception e){
            return false;
        }
    }



}
