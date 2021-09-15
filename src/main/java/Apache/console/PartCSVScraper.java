package Apache.console;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class PartCSVScraper {

    public static void scrape(){
        Scanner scan;

        try{
            scan = new Scanner(new File("Parts.csv"));
        } catch (FileNotFoundException e) {
            Console.printError("Failed to locate file Parts.csv");
            return;
        }

        String saveMfg = "";

        while(scan.hasNextLine()){
            String mfg;
            String partNumber;
            String description;
            String price;

            try{
                List<String> partLine = getRecordFromLine(scan.nextLine());
                mfg = partLine.get(0).replaceAll("\"", "").trim().toUpperCase(Locale.ROOT);
                partNumber = partLine.get(1).replaceAll("\"", "").trim().substring(3).toUpperCase(Locale.ROOT);
                description = partLine.get(2).replaceAll("\"", "").trim().toUpperCase(Locale.ROOT);
                price = partLine.get(3).replaceAll("\"", "").trim();

                if(!saveMfg.equalsIgnoreCase(mfg)){
                    saveMfg = mfg;
                    System.out.println("[UPDATE]: Now scraping line " + saveMfg);
                }

                double cost = 0;
                try{

                    if(price.startsWith("$"))
                        price = price.substring(1);

                    cost = Double.parseDouble(price);
                }catch(Exception e){
                    System.out.println("[ERROR]: Double parse failed at " + mfg + " " + partNumber +
                            " for string " + price);
                }

                if(partNumber.length() > 50){
                    System.out.println("[ERROR]: Part number too long @ " + mfg + " " + partNumber);
                    continue;
                }

                if(description.length() > 50)
                    System.out.println("[ERROR]: Trimming description @ " + mfg + " " + partNumber);

                Connector.getConnection().createStatement().execute(
                        "INSERT INTO Parts(" +
                                "LineCode, " +
                                "PartNumber, " +
                                "Description, " +
                                "Cost, " +
                                "StockQuantity, " +
                                "AvailableQuantity" +
                                ") VALUES (" +
                                "'" + mfg + "', " +
                                "'" + partNumber + "', " +
                                "'" + description + "', " +
                                "" + cost + ", " +
                                "0, " +
                                "0" +
                                ");"
                );
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }


    private static List<String> getRecordFromLine(String line) {
        List<String> values = new ArrayList<String>();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(",");
            while (rowScanner.hasNext()) {
                values.add(rowScanner.next());
            }
        }
        return values;
    }

}
