package Apache.console;

import Apache.util.InputVerifier;

import java.sql.SQLException;
import java.util.Locale;
import java.util.Scanner;

import static Apache.database.Connector.getConnection;

public class PartConsole extends Console{

    private HelpMenu helpMenu;

    PartConsole(Scanner scanner, String prompt) {
        super(scanner, prompt);
        helpMenu = new HelpMenu();
        helpMenu.addOption("purge", "Purge line code from Apache.database");
    }



    @Override
    boolean handle(String input) {
        switch(input.toUpperCase(Locale.ROOT)){

            case "PURGE" -> {
                System.out.print("Enter line code to purge: ");
                String lineCode = scanner.nextLine().toUpperCase(Locale.ROOT).trim();
                if(!InputVerifier.verifyMfg(lineCode)){
                    Console.printError("Invalid line code");
                    return true;
                }
                Console.printWarning("YOU ARE ABOUT TO PURGE ALL OF THE " + lineCode + " PRODUCT LINE");
                System.out.print("Enter \"CONFIRM\" to complete the purge: ");
                String confirm = scanner.nextLine().trim();
                if(!confirm.equalsIgnoreCase("Confirm")){
                    Console.printError("Purge aborted");
                    return true;
                }
                System.out.println("Commencing purge... please wait");

                try{
                    getConnection().createStatement().execute(
                            "DELETE FROM Parts WHERE LineCode = '" + lineCode + "';"
                    );
                    Console.printSuccess("Purge was successful");
                }catch(SQLException sqlException){
                    Console.printError("Sorry, purge failed to complete");
                }
                return true;
            }

        }

        return false;
    }


    @Override
    HelpMenu getHelpMenu() {
        return helpMenu;
    }
}
