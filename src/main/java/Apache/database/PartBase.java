package Apache.database;

import Apache.objects.Part;
import Apache.objects.Selectable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static Apache.database.Connector.*;
import static Apache.util.General.cleanDouble;

public class PartBase {

    public static String inferMfg(String partNumber) {
        if (partNumber.startsWith("AX"))
            return "UNI";
        if (partNumber.startsWith("BR") && partNumber.endsWith("-01"))
            return "MBP";
        if (partNumber.startsWith("89X"))
            return "PSC";
        if (partNumber.startsWith("87X"))
            return "PSC";
        if (partNumber.startsWith("301."))
            return "MBP";
        if (partNumber.startsWith("103."))
            return "MBP";
        if (partNumber.startsWith("104."))
            return "MBP";
        if (partNumber.startsWith("105."))
            return "MBP";
        if (partNumber.startsWith("106."))
            return "MBP";
        if (partNumber.startsWith("102."))
            return "MBP";
        return "MIS";
    }

    public static String inferDescription(String mfg, String partNumber) {

        if (mfg.startsWith("QSC"))
            return "CHASSIS PART";
        if (mfg.equals("WQS"))
            return "PADS";


        // UNI Stuff
        if (mfg.equals("UNI")) {
            if (partNumber.startsWith("AX"))
                return "DRUM/ROTOR";
            if (partNumber.startsWith("ZX"))
                return "PADS";
            if (partNumber.startsWith("ZD"))
                return "PADS";
        }


        // MBP Stuff
        if (mfg.equals("MBP")) {
            if (partNumber.startsWith("XC"))
                return "CABIN FILTER";
            if (partNumber.startsWith("XA"))
                return "AIR FILTER";
            if (partNumber.startsWith("BR"))
                return "ROTOR";
            if (partNumber.startsWith("BD"))
                return "DRUM";
            if (partNumber.startsWith("301."))
                return "PADS";
            if (partNumber.startsWith("103."))
                return "PADS";
            if (partNumber.startsWith("104."))
                return "PADS";
            if (partNumber.startsWith("105."))
                return "PADS";
            if (partNumber.startsWith("106."))
                return "PADS";
            if (partNumber.startsWith("102."))
                return "PADS";
            if (partNumber.startsWith("ZX"))
                return "PADS";
            if (partNumber.startsWith("ZD"))
                return "PADS";
            if (partNumber.startsWith("C") && partNumber.length() > 1 && Character.isDigit(partNumber.charAt(1))) {
                return "CALIPER";
            }

            return "";
        }

        if (partNumber.startsWith("89X"))
            return "CALIPER";
        if (partNumber.startsWith("87X"))
            return "CALIPER";

        return "";
    }

    public static int getAvailableQuantity(String mfg, String partNumber) {
        int avl = 0;
        try {
            Connection conn = getConnection();
            ResultSet partResults = conn.createStatement().executeQuery(
                    "SELECT AvailableQuantity FROM Parts WHERE LineCode = '" + mfg +
                            "' AND PartNumber = '" + partNumber + "';"
            );
            if (partResults.next())
                avl = partResults.getInt("AvailableQuantity");
            return avl;
        } catch (SQLException e) {
            return avl;
        }
    }


    public static String getGp(String mfg, String partNumber, String unit) {
        String gp = "?";
        try {
            Connection conn = getConnection();
            ResultSet part = conn.createStatement().executeQuery(
                    "SELECT Cost FROM Parts WHERE LineCode = '" + mfg + "' AND PartNumber = '" + partNumber + "';"
            );
            if (part.next()) {
                double costDouble = part.getDouble("Cost");
                double unitDouble;
                try {
                    unitDouble = Double.parseDouble(unit);
                } catch (Exception e) {
                    return gp;
                }
                double gpDouble = (unitDouble - costDouble) / unitDouble;
                gpDouble *= 100;
                gp = cleanDouble(gpDouble, 0);
            }
            return gp;
        } catch (SQLException sqlException) {
            return gp;
        }
    }


    public static boolean addNewPart(Part part) {
        try {
            Connection conn = getConnection();
            conn.createStatement().execute(
                    "(LineCode, PartNumber, Description, Cost, StockQuantity, AvailableQuantity) " +
                            "VALUES("
                            + "'" + part.getLineCode() + "', "
                            + "'" + part.getPartNumber() + "', "
                            + "'" + part.getDescription() + "', "
                            + part.getCost() + ", "
                            + part.getStockQuantity() + ", "
                            + part.getAvailableQuantity()
                            + ");"
            );
            return true;
        } catch (SQLException sqlException) {
            return false;
        }
    }

    public static boolean updateAvailableQuantity(String lineCode, String partNumber, int quantity) {
        try {
            Connection conn = getConnection();
            conn.createStatement().execute(
                    "UPDATE Parts SET AvailableQuantity = " + quantity +
                            " WHERE LineCode = '" + lineCode + "' AND PartNumber = '" + partNumber + "';"
            );
            return true;
        } catch (SQLException sqlException) {
            return false;
        }
    }

    public static boolean updateStockQuantity(String lineCode, String partNumber, int quantity) {
        try {
            Connection conn = getConnection();
            conn.createStatement().execute(
                    "UPDATE Parts SET StockQuantity = " + quantity +
                            " WHERE LineCode = '" + lineCode + "' AND PartNumber = '" + partNumber + "';"
            );
            return true;
        } catch (SQLException sqlException) {
            return false;
        }
    }

    public static boolean updateCost(String lineCode, String partNumber, double cost) {
        try {
            Connection conn = getConnection();
            conn.createStatement().execute(
                    "UPDATE Parts SET Cost = " + cost +
                            " WHERE LineCode = '" + lineCode + "' AND PartNumber = '" + partNumber + "';"
            );
            return true;
        } catch (SQLException sqlException) {
            return false;
        }
    }

    public static boolean updateDescription(String lineCode, String partNumber, String description) {
        try {
            Connection conn = getConnection();
            conn.createStatement().execute(
                    "UPDATE Parts SET Description = '" + description +
                            "' WHERE LineCode = '" + lineCode + "' AND PartNumber = '" + partNumber + "';"
            );
            return true;
        } catch (SQLException sqlException) {
            return false;
        }
    }


    public static boolean deletePart(String lineCode, String partNumber) {
        try {
            Connection conn = getConnection();
            conn.createStatement().execute(
                    "DELETE FROM Parts WHERE LineCode = '" + lineCode + "' AND PartNumber = '" + partNumber + "';"
            );
            return true;
        } catch (SQLException sqlException) {
            return false;
        }
    }

    public static Part getPart(String lineCode, String partNumber) {
        try {
            Connection conn = getConnection();
            ResultSet partResults = conn.createStatement().executeQuery(
                    "SELECT * FROM Parts WHERE LineCode = '" + lineCode + "' AND PartNumber = '" + partNumber + "';"
            );
            partResults.next();
            return new Part(
                    partResults.getString("LineCode"),
                    partResults.getString("PartNumber"),
                    partResults.getString("Description"),
                    partResults.getDouble("Cost"),
                    partResults.getInt("StockQuantity"),
                    partResults.getInt("AvailableQuantity")
            );
        } catch (SQLException sqlException) {
            return null;
        }
    }


    public static Part tryToCorrectPartNumber(String mfg, String partNumber) {
        try {
            partNumber = partNumber.replaceAll("/", "");
            partNumber = partNumber.replaceAll("-", "");
            partNumber = partNumber.replaceAll(" ", "");

            Connection conn = getConnection();

            ResultSet partResults = conn.createStatement().executeQuery(
                    "SELECT * FROM Parts WHERE LineCode = '" +
                            mfg + "' AND " +
                            "REPLACE(REPLACE(PartNumber, '-', ''), '/', '') LIKE '" + partNumber + "';"
            );
            if (!partResults.next()) {
                return null;
            }
            Part part = new Part(
                    partResults.getString("LineCode"),
                    partResults.getString("PartNumber"),
                    partResults.getString("Description"),
                    partResults.getDouble("Cost"),
                    partResults.getInt("StockQuantity"),
                    partResults.getInt("AvailableQuantity")
            );
            return part;

        } catch (Exception e) {
            return null;
        }
    }

    public static List<Selectable> getPartsByNumber(String partNumber) {
        List<Selectable> parts = new ArrayList<>();
        try {
            partNumber = partNumber.replaceAll("/", "");
            partNumber = partNumber.replaceAll("-", "");
            partNumber = partNumber.replaceAll(" ", "");

            Connection connection = getConnection();

            ResultSet partResults = connection.createStatement().executeQuery(
                    "SELECT * FROM Parts WHERE " +
                            "REPLACE(REPLACE(PartNumber, '-', ''), '/', '') LIKE '" + partNumber + "';");
            while (partResults.next()) {
                parts.add(new Part(
                        partResults.getString("LineCode"),
                        partResults.getString("PartNumber"),
                        partResults.getString("Description"),
                        partResults.getDouble("Cost"),
                        partResults.getInt("StockQuantity"),
                        partResults.getInt("AvailableQuantity")
                ));
            }
            return parts;
        } catch (SQLException sqlException) {
            return parts;
        }
    }


}
