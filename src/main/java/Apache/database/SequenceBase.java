package Apache.database;

import Apache.objects.SequenceLine;
import Apache.objects.Sequence;
import Apache.objects.CounterPerson;
import Apache.objects.Customer;
import Apache.objects.Selectable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static Apache.database.Connector.*;

public class SequenceBase {

    public static List<Selectable> getSequences() {
        List<Selectable> sequences = new ArrayList<>();
        try {
            Connection connection = getConnection();
            ResultSet sequenceResults = connection.createStatement().executeQuery(
                    "SELECT * FROM Sequences");
            if (!sequenceResults.next()){
                return sequences;
            }
            do {
                String saveName = sequenceResults.getString("SaveName");
                List<SequenceLine> sequenceLines = new ArrayList<>();
                ResultSet lineResults = connection.createStatement().executeQuery(
                        "SELECT * FROM SequenceLines WHERE SaveName = '" + saveName + "' ORDER BY IndexKey;"
                );
                while (lineResults.next()) {
                    SequenceLine newSequenceLine = new SequenceLine(
                            lineResults.getInt("IndexKey"),
                            lineResults.getString("TransCode"),
                            lineResults.getInt("Quantity"),
                            lineResults.getString("LineCode"),
                            lineResults.getString("PartNumber"),
                            lineResults.getString("Description"),
                            lineResults.getDouble("ListPrice"),
                            lineResults.getDouble("Price"),
                            lineResults.getString("TaxCode")
                    );

                    if (lineResults.getBoolean("Voided"))
                        newSequenceLine.voidSale();

                    sequenceLines.add(newSequenceLine);
                }

                Customer customer = CustomerBase.getCustomerByNumber(
                        sequenceResults.getString("CustomerNumber"));
                CounterPerson counterPerson = CounterPersonBase.getCounterPersonByNumber(
                        sequenceResults.getString("CounterPersonNumber")
                );

                sequences.add(new Sequence(
                        sequenceResults.getString("SaveName"),
                        customer,
                        counterPerson,
                        sequenceResults.getString("PurchaseOrder"),
                        sequenceResults.getString("TransCode"),
                        sequenceResults.getString("VehicleDescription"),
                        sequenceResults.getString("ShipTo"),
                        sequenceResults.getDouble("FreightTotal"),
                        sequenceLines
                ));

            } while (sequenceResults.next());

            return sequences;
        } catch (SQLException sqlException) {
            return sequences;
        }
    }

    public static boolean holdSequence(Sequence sequence) {
        Connection conn;
        try{
            conn = getConnection();
        }catch(SQLException sqlException){
            return false;
        }


        try {
            conn.setAutoCommit(false);
            Statement statement = conn.createStatement();
            String saveName = sequence.getSaveName();
            statement.addBatch(
                    "DELETE FROM SequenceLines WHERE SaveName = '" + saveName + "';"
            );
            statement.addBatch(
                    "DELETE FROM Sequences WHERE SaveName = '" + saveName + "';"
            );

            statement.addBatch(
                    "INSERT INTO Sequences(" +
                            "SaveName, " +
                            "CustomerNumber, " +
                            "CounterPersonNumber, " +
                            "PurchaseOrder, " +
                            "TransCode, " +
                            "VehicleDescription, " +
                            "ShipTo, " +
                            "FreightTotal" +
                            ")" +
                            "VALUES(" +
                            "'" + sequence.getSaveName() + "', " +
                            "'" + sequence.getCustomer().getNumber() + "', " +
                            sequence.getCounterPerson().getNumber() + ", " +
                            "'" + sequence.getPo() + "', " +
                            "'" + sequence.getTransCode() + "', " +
                            "'" + sequence.getVehicleDescription() + "'," +
                            "'" + sequence.getShipTo() + "', " +
                            sequence.getFreightTotal() +
                            ");"
            );

            for (SequenceLine sequenceLine : sequence.getSequenceLines()) {
                boolean voided = sequenceLine.isVoided();
                statement.addBatch(
                        "INSERT INTO SequenceLines(" +
                                "IndexKey, " +
                                "SaveName, " +
                                "TransCode, " +
                                "Quantity, " +
                                "LineCode, " +
                                "PartNumber, " +
                                "Description, " +
                                "ListPrice, " +
                                "Price, " +
                                "TaxCode, " +
                                "Voided)" +
                                "VALUES(" +
                                sequenceLine.getIndexKey() + ", " +
                                "'" + sequence.getSaveName() + "', " +
                                "'" + sequenceLine.getTransCode() + "', " +
                                sequenceLine.getQty() + ", " +
                                "'" + sequenceLine.getMfg() + "', " +
                                "'" + sequenceLine.getPartNumber() + "', " +
                                "'" + sequenceLine.getDescription() + "', " +
                                sequenceLine.getListPrice() + ", " +
                                sequenceLine.getUnitPrice() + ", " +
                                "'" + sequenceLine.getTx() + "', " +
                                voided +
                                ");"
                );
            }

            statement.executeBatch();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            System.out.println("Attempting rollback..");
            try{
                conn.rollback();
                conn.setAutoCommit(true);
                System.out.println("Rollback success");
            }catch(SQLException sqlException1){
                sqlException1.printStackTrace();
                System.out.println("Rollback failed");
            }
            return false;
        }
    }

    public static boolean killSequence(String saveName) {
        Connection conn;
        try{
            conn = getConnection();
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            return false;
        }


        try {
            conn.setAutoCommit(false);
            Statement statement = conn.createStatement();

            statement.addBatch(
                    "DELETE FROM SequenceLines WHERE SaveName = '" + saveName + "';"
            );

            statement.addBatch(
                    "DELETE FROM Sequences WHERE SaveName = '" + saveName + "';"
            );

            statement.executeBatch();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException sqlException) {
            try{
                conn.rollback();
                conn.setAutoCommit(true);
            }catch(SQLException sqlException1){
                sqlException1.printStackTrace();
            }
            return false;
        }
    }

}