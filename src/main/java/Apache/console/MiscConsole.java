package Apache.console;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.util.Locale;
import java.util.Scanner;

public class MiscConsole extends Console{

    private HelpMenu helpMenu;


    MiscConsole(Scanner scanner, String prompt) {
        super(scanner, prompt);
        helpMenu = new HelpMenu();
        helpMenu.addOption("printers", "List the names of available printers");
    }

    @Override
    boolean handle(String input) {
       switch (input.toUpperCase(Locale.ROOT)){
           case "PRINTERS" -> {
               PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
               for (PrintService printService : printServices) {
                   System.out.println(printService.getName());
               }
               return true;
           }
           case "TEST" -> {


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
