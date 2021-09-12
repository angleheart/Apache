package Apache.console;

import java.util.Locale;
import java.util.Scanner;

public class ScrapeConsole extends Console{

    private HelpMenu helpMenu;

    ScrapeConsole(Scanner scanner, String prompt) {
        super(scanner, prompt);
        helpMenu = new HelpMenu();
        helpMenu.addOption("vehicles", "Scrape parts pro lookup for vehicles");
        helpMenu.addOption("parts   ", "Scrape parts spreadsheet");
    }

    @Override
    boolean handle(String input) {
        input = input.toUpperCase(Locale.ROOT);

        switch(input){
            case "VEHICLES" -> {
                System.out.print("Parts Pro Username: ");
                String username = scanner.nextLine();
                if(isExitCommand(username))
                    return true;
                System.out.print("Parts Pro Password: ");
                String password = scanner.nextLine();
                if(isExitCommand(password))
                    return true;
                System.out.print("Start Year: ");
                String startYear = scanner.nextLine();
                if(isExitCommand(startYear))
                    return true;
                System.out.print("Start Make: ");
                String startMake = scanner.nextLine();
                if(isExitCommand(startMake))
                    return true;
                System.out.print("Pause in ms: ");
                String pause = scanner.nextLine();
                if(isExitCommand(pause))
                    return true;
                int pauseInt;
                try{
                    pauseInt = Integer.parseInt(pause);
                }catch(Exception e){
                    Console.printError("Invalid pause");
                    return true;
                }

                System.out.println("Starting browser...");
                if(!NextPartScraper.startBrowser())
                    return true;
                System.out.println("Attempting login...");
                if(!NextPartScraper.login(username, password))
                    return true;
                System.out.println("Commencing Scrape...");
                NextPartScraper.commenceScrape(startYear, startMake, pauseInt);
                return true;
            }


            case "PARTS" -> {
                SubLineScraper.scrape();
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
