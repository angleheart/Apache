package Apache.console;

import Apache.console.eom.CustomerStatement;
import Apache.console.eom.StatementLauncher;
import Apache.console.eom.StatementLine;
import Apache.objects.Customer;
import Apache.database.CustomerBase;
import Apache.database.StatementBase;
import Apache.objects.Selectable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static Apache.util.General.cleanDouble;

public class EOMConsole extends Console {

    private final HelpMenu helpMenu;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final List<CustomerStatement> customerStatements = new ArrayList<>();


    EOMConsole(Scanner scanner, String prompt) {
        super(scanner, prompt);
        helpMenu = new HelpMenu();
        helpMenu.addOption("closeout-month-full", "Perform full procedure in one command");
        helpMenu.addOption("prepare            ", "Prepare customer statements");
        helpMenu.addOption("list               ", "List prepared statements");
        helpMenu.addOption("launch             ", "Launch statement imager");
        helpMenu.addOption("print-prepared     ", "Print prepared statements");
        helpMenu.addOption("load-written       ", "Load written statements");
        helpMenu.addOption("purge-prepared     ", "Purge prepared statements");
        helpMenu.addOption("purge-written      ", "Purge written statements");
    }

    @Override
    boolean handle(String input) {
        switch (input.toUpperCase(Locale.ROOT)) {
            case "LAUNCH" -> {
                launch();
                return true;
            }

            case "LIST" -> {
                System.out.println("----[Prepared Statements List]----");
                for (CustomerStatement statement : customerStatements) {
                    System.out.println(
                            statement.getCustomer().getName()
                    );
                }
                System.out.println("----------------------------------");
                return true;
            }

            case "PREPARE" -> {
                System.out.print("Enter Customer Number: ");
                String custNum = scanner.nextLine().trim();
                if (custNum.equalsIgnoreCase("ALL")) {
                    prepareAll();
                } else {
                    Customer customer = CustomerBase.getCustomerByNumber(custNum);
                    if (customer == null) {
                        printError("Customer not found");
                        return true;
                    }
                    if (prepare(customer))
                        printSuccess("Prepared customer statement");
                    else
                        printError("Failed to prepare customer statement");
                }
                return true;
            }

            case "PURGE-PREPARED" -> {
                customerStatements.clear();
                printSuccess("Purged prepared statements");
                return true;
            }

            case "PURGE-WRITTEN" -> {
                System.out.print("Enter Month Number   : ");
                String monthStr = scanner.nextLine().trim();
                System.out.print("Enter Year Number    : ");
                String yearStr = scanner.nextLine().trim();
                int month;
                int year;
                try {
                    month = Integer.parseInt(monthStr);
                    year = Integer.parseInt(yearStr);
                    if (month > 12 || month < 1) {
                        printError("Invalid month");
                        return true;
                    }
                    if (yearStr.length() != 4) {
                        printError("Invalid year");
                        return true;
                    }
                } catch (Exception e) {
                    printError("Failed to parse");
                    return true;
                }
                if (StatementBase.purge(month, year))
                    printSuccess("Statement purge completed successfully");
                else
                    printError("Failed to purge statements");
                return true;
            }

            case "PRINT-PREPARED" -> {
                    for (CustomerStatement statement : customerStatements)
                        printStatementToScreen(statement);
                return true;
            }

            case "WRITE-PREPARED" -> {
                if(customerStatements.size() < 1){
                    printError("There are no statements prepared");
                    return true;
                }
                System.out.print("Enter Month Number   : ");
                String monthStr = scanner.nextLine().trim();
                System.out.print("Enter Year Number    : ");
                String yearStr = scanner.nextLine().trim();
                int month;
                int year;
                try {
                    month = Integer.parseInt(monthStr);
                    year = Integer.parseInt(yearStr);
                    if (month > 12 || month < 1) {
                        printError("Invalid month");
                        return true;
                    }
                    if (yearStr.length() != 4) {
                        printError("Invalid year");
                        return true;
                    }
                } catch (Exception e) {
                    printError("Failed to parse");
                    return true;
                }

                if(!StatementBase.writeStatements(month, year, customerStatements)){
                    printError("Failed to write statements");
                    return true;
                }
                printSuccess("All statements written");
                return true;
            }


            case "LOAD-WRITTEN" -> {
                System.out.print("Enter Month Number   : ");
                String monthStr = scanner.nextLine().trim();
                System.out.print("Enter Year Number    : ");
                String yearStr = scanner.nextLine().trim();
                int month;
                int year;
                try {
                    month = Integer.parseInt(monthStr);
                    year = Integer.parseInt(yearStr);
                    if (month > 12 || month < 1) {
                        printError("Invalid month");
                        return true;
                    }
                    if (yearStr.length() != 4) {
                        printError("Invalid year");
                        return true;
                    }
                } catch (Exception e) {
                    printError("Failed to parse");
                    return true;
                }
                System.out.print("Enter Customer Number: ");
                String custNum = scanner.nextLine().trim();
                if (custNum.equalsIgnoreCase("ALL")) {
                    List<CustomerStatement> loaded = StatementBase.getWrittenStatements(month, year);
                    if(loaded == null){
                        printError("Failed to load statements");
                        return true;
                    }
                    customerStatements.addAll(loaded);
                } else {
                    Customer customer = CustomerBase.getCustomerByNumber(custNum);
                    if (customer == null) {
                        printError("Customer not found");
                        return true;
                    }
                    CustomerStatement statement = StatementBase.getWrittenStatement(month, year, customer.getNumber());
                    if(statement == null){
                        printError("Failed to load customer statement");
                        return true;
                    }
                    customerStatements.add(statement);
                }
                return true;
            }

        }

        return false;
    }

    private void launch() {
        StatementLauncher.run(customerStatements);
    }

    private boolean prepare(Customer customer) {
        CustomerStatement customerStatement = new CustomerStatement(customer);
        if (!customerStatement.generate())
            return false;
        if (customer.paysByInvoice()) {
            if (customerStatement.getStatementLines().size() == 0) {
                System.out.println("Skipping statement prepare for " + customer.getName());
                return true;
            }
        } else {
            if (
                    customerStatement.getTotalBalance() == 0 &&
                            customerStatement.getStatementLines().size() == 0
            ) {
                System.out.println("Skipping statement prepare for " + customer.getName());
                return true;
            }
        }
        System.out.println("Prepared statement for " + customer.getName());
        customerStatements.add(customerStatement);
        return true;
    }

    public void prepareAll() {
        List<Selectable> customers = CustomerBase.getCustomersByName("");
        if (customers == null) {
            printError("Failed to prepare all customer statements");
            return;
        }
        for (Selectable customer : customers) {
            if (!prepare((Customer) customer)) {
                printError("Failed to prepare all customer statements");
                return;
            }
        }
        printSuccess("Prepared all customer statements");
    }

    public void printStatementToScreen(CustomerStatement statement) {
        Customer customer = statement.getCustomer();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        System.out.println("Statement for : " + customer.getNumber() + "-" + customer.getName());
        System.out.println("Generated on  : " + sdf.format(statement.getDate()));
        System.out.println("90            : " + cleanDouble(statement.getBal90(), 2));
        System.out.println("60            : " + cleanDouble(statement.getBal60(), 2));
        System.out.println("30            : " + cleanDouble(statement.getBal30(), 2));
        System.out.println("Curr          : " + cleanDouble(statement.getBalCurr(), 2));
        System.out.println("Total         : " + cleanDouble(statement.getTotalBalance(), 2));
        System.out.println("Total Payments: " + cleanDouble(statement.getTotalPaid(), 2));
        System.out.println(
                "____Date_________INV#________DETAIL______ORIGINAL_____APPLIED______BALANCE_____AMOUNT DUE_"
        );
        for (StatementLine line : statement.getStatementLines()) {
            for (String col : line.columns()) {
                while (col.length() < 12)
                    col = col.concat(" ");
                col = col.substring(0, 12);
                System.out.print(col + "|");
            }
            System.out.println();
        }
        System.out.println(
                "___________________________________________________________________________________________\n\n"
        );
    }

    @Override
    HelpMenu getHelpMenu() {
        return helpMenu;
    }
}
