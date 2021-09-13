package Apache.console;

import Apache.objects.Customer;
import Apache.objects.ReceivableReport;
import Apache.database.CustomerBase;
import Apache.objects.Transferable;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import static Apache.util.General.cleanDouble;

public class CustomersConsole extends Console {

    private HelpMenu helpMenu;

    CustomersConsole(Scanner scanner, String prompt) {
        super(scanner, prompt);
        helpMenu = new HelpMenu();
        helpMenu.addOption("create             ", "Add a new customer");
        helpMenu.addOption("lookup  <name>  ", "Lookup customers from name");
        helpMenu.addOption("modify  <number>", "Modify an existing customer");
        helpMenu.addOption("detail  <number>", "Display customer detail from number");
        helpMenu.addOption("rec     <number>", "Run receivable report");
    }

    @Override
    boolean handle(String input) {
        String[] args = input.split(" ");
        String command = args[0].toUpperCase(Locale.ROOT);

        switch (command) {
            case "CREATE" -> {
                CustomerCreator customerCreator = new CustomerCreator(scanner);
                customerCreator.performRequest();
                return true;
            }

            case "LOOKUP" -> {
                String name = "";
                if (args.length > 1)
                    name = args[1];
                List<Transferable> customers = CustomerBase.getCustomersByName(name);
                if (customers.size() == 0)
                    Console.printError("No results");
                for (Transferable customer : customers)
                    System.out.println(customer.getSelectableName());
                return true;
            }

            case "DETAIL" -> {
                if (args.length < 2) {
                    Console.printError("You must provide a customer number");
                    return true;
                }

                Customer customer = CustomerBase.getCustomerByNumber(args[1]);
                if (customer == null) {
                    Console.printError("No customers found by this number");
                    return true;
                }

                System.out.println("----Customer Detail----");
                System.out.println(Console.COLOR_PURPLE + customer + Console.COLOR_RESET);
                System.out.println("-----------------------");
                return true;
            }

            case "MODIFY" -> {
                if (args.length < 2) {
                    Console.printError("You must provide a customer number");
                    return true;
                }
                Customer customer = CustomerBase.getCustomerByNumber(args[1].toUpperCase(Locale.ROOT));
                if (customer == null) {
                    Console.printError("No customers found by this number");
                    return true;
                }

                CustomerModificationConsole cmc = new CustomerModificationConsole(
                        scanner,
                        "Apache > Customers > Modify " + customer.getNumber() + " > ",
                        customer
                );
                cmc.startConsole();
                return true;
            }

            case "REC" -> {
                if (args.length < 2) {
                    Console.printError("You must provide a customer number");
                    return true;
                }
                Customer customer = CustomerBase.getCustomerByNumber(args[1].toUpperCase(Locale.ROOT));
                if (customer == null) {
                    Console.printError("No customers found by this number");
                    return true;
                }
                ReceivableReport receivableReport = new ReceivableReport(customer.getNumber());
                if (!receivableReport.runReceivables()) {
                    printError("Receivable report failed to run");
                    return true;
                }

                System.out.println("---Receivables For " + customer.getName() + "---" + COLOR_GREEN);
                System.out.println("90 Days:  " + cleanDouble(receivableReport.getDay90Balance(), 2));
                System.out.println("60 Days:  " + cleanDouble(receivableReport.getDay60Balance(), 2));
                System.out.println("30 Days:  " + cleanDouble(receivableReport.getDay30Balance(), 2));
                System.out.println("Current:  " + cleanDouble(receivableReport.getCurrentBalance(), 2));
                System.out.println();
                System.out.println("Total  :  " +
                        cleanDouble(
                                receivableReport.getDay90Balance() +
                                        receivableReport.getDay60Balance() +
                                        receivableReport.getDay30Balance() +
                                        receivableReport.getCurrentBalance(),
                                2
                        )
                );
                System.out.println(COLOR_RESET + "-----------------------");
                if(
                        receivableReport.getDay60Balance() +
                        receivableReport.getDay90Balance() > 0
                )printAlert("This account is past due");
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
