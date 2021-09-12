package Apache.console;

import Apache.database.Connector;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class SubLineScraper {

    public static void scrape() {
        Scanner scan;

        try {
            scan = new Scanner(new File("Subline.txt"));
        } catch (FileNotFoundException e) {
            Console.printError("Failed to locate file Subline.txt");
            return;
        }

        String saveMfg = "";
        String mfg = "";
        String partNumber = "";
        String subLine = "";

        while (scan.hasNextLine()) {


            try {
                List<String> partLine = getRecordFromLine(scan.nextLine());
                mfg = partLine.get(0).replaceAll("\"", "").trim().toUpperCase(Locale.ROOT);
                partNumber = partLine.get(2).replaceAll("\"", "").trim().substring(3).toUpperCase(Locale.ROOT);
                subLine = partLine.get(3).replaceAll("\"", "").trim().toUpperCase(Locale.ROOT);

                if (!saveMfg.equalsIgnoreCase(mfg)) {
                    saveMfg = mfg;
                    System.out.println("[UPDATE]: Now scraping line " + saveMfg);
                }

                if (partNumber.length() > 50) {
                    System.out.println("[ERROR]: Part number too long @ " + mfg + " " + partNumber);
                    continue;
                }

                if (subLine.length() != 4) {
                    System.out.println("[ERROR]: Subline != 4 @ " + mfg + " " + partNumber + " subline: " + subLine);
                    continue;
                }

                Connector.getConnection().createStatement().execute(
                        "UPDATE Parts SET SubLine = '" + subLine +
                                "' WHERE LineCode = '" + mfg + "' AND PartNumber = '" + partNumber + "';"
                );
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("[ERROR]: EXCEPTION @ " + mfg + " " + partNumber + " subline: " + subLine);

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
