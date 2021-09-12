package Apache.console;

import java.util.Locale;
import java.util.Scanner;

class MainMenuConsole extends Console {

    private HelpMenu helpMenu;

    MainMenuConsole(Scanner scanner, String prompt) {
        super(scanner, prompt);
        helpMenu = new HelpMenu();
        helpMenu.addOption("customers  ", "Start customers Apache.console");
        helpMenu.addOption("cntrpsns   ", "Start counterperson Apache.console");
        helpMenu.addOption("eod        ", "End of day procedures");
        helpMenu.addOption("parts      ", "Start parts Apache.console");
        helpMenu.addOption("invoices   ", "Start invoices Apache.console");
        helpMenu.addOption("scrape     ", "Access data scraping utilities");
        helpMenu.addOption("sanity     ", "Perform sanity checks on various components");
        helpMenu.addOption("misc       ", "Miscellaneous utilities");
    }

    @Override
    boolean handle(String input) {
        input = input.toUpperCase(Locale.ROOT);

        switch (input) {
            case "CUSTOMERS" -> {
                CustomersConsole customersConsole = new CustomersConsole(
                        scanner, "Apache > Customers > ");
                customersConsole.startConsole();
                return true;
            }
            case "CNTRPSNS" -> {
                CounterPersonConsole counterPersonConsole = new CounterPersonConsole(
                        scanner, "Apache > Cntrpsns > ");
                counterPersonConsole.startConsole();
                return true;
            }
            case "EOD" -> {
                EODConsole eodConsole = new EODConsole(scanner, "Apache > EOD > ");
                eodConsole.startConsole();
                return true;
            }
            case "EOM" -> {
                EOMConsole eomConsole = new EOMConsole(scanner, "Apache > EOM > ");
                eomConsole.startConsole();
                return true;
            }
            case "SCRAPE" -> {
                ScrapeConsole scrapeConsole = new ScrapeConsole(scanner, "Apache > Scrape > ");
                scrapeConsole.startConsole();
                return true;
            }
            case "SANITY" -> {
                SanityConsole sanityConsole = new SanityConsole(scanner, "Apache > Sanity > ");
                sanityConsole.startConsole();
                return true;
            }
            case "MISC" -> {
                MiscConsole miscConsole = new MiscConsole(scanner, "Apache > Misc > ");
                miscConsole.startConsole();
                return true;
            }
            case "PARTS" -> {
                PartConsole partConsole = new PartConsole(scanner, "Apache > Parts > ");
                partConsole.startConsole();
                return true;
            }
            case "INVOICES" -> {
                InvoiceConsole invoiceConsole = new InvoiceConsole(scanner, "Apache > Invoices > ");
                invoiceConsole.startConsole();
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
