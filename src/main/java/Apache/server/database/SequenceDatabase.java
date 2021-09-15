package Apache.server.database;

import Apache.objects.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SequenceDatabase extends Database {

    public List<Sequence> getSequences() throws SQLException {
        List<Sequence> toReturn = new ArrayList<>();
        PreparedStatement prepHead;
        PreparedStatement prepBody;
        ResultSet headResults;
        ResultSet bodyResults;

        prepHead = connection.prepareStatement(
                "SELECT * FROM Sequences;"
        );
        headResults = prepHead.executeQuery();
        if (!headResults.next())
            return toReturn;

        CustomerDatabase customerDatabase = new CustomerDatabase();
        CounterPersonDatabase counterPersonDatabase = new CounterPersonDatabase();

        prepBody = connection.prepareStatement(
                "SELECT * FROM SequenceLines WHERE SaveName = ? ORDER BY IndexKey;"
        );

        do {
            String saveName = headResults.getString("SaveName");
            prepBody.setString(1, saveName);
            bodyResults = prepBody.executeQuery();
            List<SequenceLine> sequenceLines = new ArrayList<>();
            while (bodyResults.next()) {
                SequenceLine newSequenceLine = new SequenceLine(
                        bodyResults.getInt("IndexKey"),
                        bodyResults.getString("TransCode"),
                        bodyResults.getInt("Quantity"),
                        bodyResults.getString("LineCode"),
                        bodyResults.getString("PartNumber"),
                        bodyResults.getString("Description"),
                        bodyResults.getDouble("ListPrice"),
                        bodyResults.getDouble("Price"),
                        bodyResults.getString("TaxCode")
                );
                if (bodyResults.getBoolean("Voided"))
                    newSequenceLine.voidSale();
                sequenceLines.add(newSequenceLine);
            }
            List<Customer> customers =
                    customerDatabase.getCustomers(headResults.getString("CustomerNumber"));
            List<CounterPerson> counterPeople =
                    counterPersonDatabase.getCounterPeople(headResults.getString("CounterPersonNumber"));
            if (customers.size() < 1 || counterPeople.size() < 1)
                continue;
            toReturn.add(new Sequence(
                    headResults.getString("SaveName"),
                    customers.get(0),
                    counterPeople.get(0),
                    headResults.getString("PurchaseOrder"),
                    headResults.getString("TransCode"),
                    headResults.getString("VehicleDescription"),
                    headResults.getString("ShipTo"),
                    headResults.getDouble("FreightTotal"),
                    sequenceLines
            ));
        } while (headResults.next());
        customerDatabase.close();
        counterPersonDatabase.close();
        return toReturn;
    }


    public boolean holdSequence(Sequence sequence) {
        try {
            if(!killSequence(sequence.getSaveName()))
                return false;

            connection.setAutoCommit(false);
            PreparedStatement insertHead = connection.prepareStatement(
                    "INSERT INTO Sequences(SaveName, CustomerNumber, CounterPersonNumber, " +
                            "PurchaseOrder, TransCode, VehicleDescription, ShipTo, FreightTotal)" +
                            "VALUES(?, ?, ?, ?, ?, ?, ?, ?);"
            );

            insertHead.setString(1, sequence.getSaveName());
            insertHead.setString(2, sequence.getCustomer().getNumber());
            insertHead.setInt(3, sequence.getCounterPerson().getNumber());
            insertHead.setString(4, sequence.getPo());
            insertHead.setString(5, sequence.getTransCode());
            insertHead.setString(6, sequence.getVehicleDescription());
            insertHead.setString(7, sequence.getShipTo());
            insertHead.setDouble(8, sequence.getFreightTotal());
            insertHead.execute();
            for(PreparedStatement prep :
                    getLineInsertStatements(
                            sequence.getSaveName(),
                            sequence.getSequenceLines())
            ){
                prep.execute();
            }
            connection.commit();
            connection.setAutoCommit(true);
            return true;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            try{
                connection.rollback();
                connection.setAutoCommit(true);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public List<PreparedStatement> getLineInsertStatements(
            String saveName,
            List<SequenceLine> sequenceLines
    ) throws SQLException {
        List<PreparedStatement> toReturn = new ArrayList<>();
        for (SequenceLine sequenceLine : sequenceLines) {
            boolean voided = sequenceLine.isVoided();
            PreparedStatement prepStatement = connection.prepareStatement(
                    "INSERT INTO SequenceLines(IndexKey, SaveName, TransCode, Quantity, LineCode, " +
                            "PartNumber, Description, ListPrice, Price, TaxCode, Voided)" +
                            "VALUES(? , ? , ? , ?, ?, ?, ?, ?, ?, ?, ?);"
            );
            prepStatement.setInt(1, sequenceLine.getIndexKey());
            prepStatement.setString(2, saveName);
            prepStatement.setString(3, sequenceLine.getTransCode());
            prepStatement.setInt(4, sequenceLine.getQty());
            prepStatement.setString(5, sequenceLine.getMfg());
            prepStatement.setString(6, sequenceLine.getPartNumber());
            prepStatement.setString(7, sequenceLine.getDescription());
            prepStatement.setDouble(8, sequenceLine.getListPrice());
            prepStatement.setDouble(9, sequenceLine.getUnitPrice());
            prepStatement.setString(10, sequenceLine.getTx());
            prepStatement.setBoolean(11, voided);
            toReturn.add(prepStatement);
        }
        return toReturn;
    }


    public boolean killSequence(String saveName) {
        try{
            connection.setAutoCommit(false);
            PreparedStatement prepBodyDelete = connection.prepareStatement(
                    "DELETE FROM SequenceLines WHERE SaveName = ?;"

            );
            PreparedStatement prepHeadDelete = connection.prepareStatement(
                    "DELETE FROM Sequences WHERE SaveName = ?;"
            );
            prepBodyDelete.setString(1, saveName);
            prepHeadDelete.setString(1, saveName);
            prepBodyDelete.execute();
            prepHeadDelete.execute();
            connection.commit();
            connection.setAutoCommit(true);
            return true;
        }catch(SQLException e){
            e.printStackTrace();
            try{
                connection.rollback();
                connection.setAutoCommit(true);
            }catch(SQLException e2){
                e2.printStackTrace();
            }
            return false;
        }
    }

}
