package Apache.console;

import Apache.objects.Customer;
import Apache.database.CustomerBase;
import Apache.objects.Invoice;
import Apache.util.InputVerifier;

import java.util.Locale;
import java.util.Scanner;

import static Apache.util.General.cleanDouble;

public class InvoiceConsole extends Console{

    private HelpMenu helpMenu;

    InvoiceConsole(Scanner scanner, String prompt) {
        super(scanner, prompt);
        helpMenu = new HelpMenu();
        //helpMenu.addOption("force", "Force insert an invoice into the Apache.database");
    }

    @Override
    boolean handle(String input) {
//        switch(input.toUpperCase(Locale.ROOT)){
//
//            case "FORCE" -> {
//                Console.printWarning(
//                        "This option will force an invoice into the Apache.database.\n" +
//                        "Please proceed with extreme caution.\n" +
//                        "ANY MISTAKES COULD BE CHALLENGING TO CORRECT."
//                );
//                System.out.print("Customer Number: ");
//                String customerNumber = scanner.nextLine().trim();
//
//                if(!InputVerifier.verifyCustomerNumber(customerNumber)){
//                    printError("Invalid customer number entered");
//                    return true;
//                }
//
//                Customer customer = CustomerBase.getCustomerByNumber(customerNumber);
//                if(customer == null){
//                    printError("Customer could not be loaded");
//                    return true;
//                }
//
//                System.out.print("Enter invoice number WITHOUT THE D: ");
//                String invoiceNumberStr = scanner.nextLine().trim();
//                int invoiceNumber;
//                try{
//                    invoiceNumber = Integer.parseInt(invoiceNumberStr);
//                    if(invoiceNumber < 0){
//                        printError("Invoice numbers may not be negative");
//                        return true;
//                    }
//
//                    Invoice invoice = InvoiceBase.getInvoiceByNumberFromAll(invoiceNumber);
//                    if(invoice != null){
//                        printError("This invoice already exists, YOU ARE NOT BEING CAREFUL ENOUGH!");
//                        return true;
//                    }
//
//                }catch(Exception e){
//                    printError("You entered something that isn't a number. Did you include the D?");
//                    return true;
//                }
//
//                System.out.print("Enter " + COLOR_CYAN + "MONTH" + COLOR_RESET + " as a number: ");
//
//                String monthStr = scanner.nextLine().trim();
//                int month;
//
//                try{
//                    month = Integer.parseInt(monthStr);
//                    if(month < 1 || month > 12){
//                        printError("\"" + month + "\" is not a valid month.");
//                        return true;
//                    }
//                }catch(Exception e){
//                    printError("Please enter month AS A NUMBER 1-12");
//                    return true;
//                }
//
//                System.out.print("Enter " + COLOR_CYAN + "DAY" + COLOR_RESET + " as a number: ");
//                String dayStr = scanner.nextLine().trim();
//                int day;
//
//                try {
//                    day = Integer.parseInt(dayStr);
//                    if(day > 31 || day < 1){
//                        printError("This is not a valid day of the month");
//                        return true;
//                    }
//                }catch(Exception e){
//                    printError("Please enter day AS A NUMBER 1-31");
//                    return true;
//                }
//
//                System.out.print("Enter " + COLOR_CYAN + "YEAR" + COLOR_RESET + " as a number: ");
//                String yearStr = scanner.nextLine();
//                int year;
//                if(yearStr.length() != 4){
//                    printError("This is not a valid year");
//                    return true;
//                }
//
//                try{
//                    year = Integer.parseInt(yearStr);
//                }catch(Exception e){
//                    printError("Couldn't parse year, enter as a number please");
//                    return true;
//                }
//
//                System.out.print("Enter invoice balance: ");
//                String balanceStr = scanner.nextLine();
//                double balance;
//                try{
//                    balance = Double.parseDouble(balanceStr);
//                    if(balance == 0){
//                        printError("If an invoice balance is zero, there is no reason to put it in");
//                        return true;
//                    }
//                }catch(Exception e){
//                    printError("Couldn't parse balance, enter number");
//                    return true;
//                }
//                System.out.println("---Invoice To Force---" + Console.COLOR_CYAN);
//                System.out.println(customer.getNumber());
//                System.out.println(customer.getName());
//                System.out.println("D" + invoiceNumber);
//                System.out.println(month + "/" + day + "/" + year);
//                System.out.println(cleanDouble(balance, 2));
//                System.out.println(Console.COLOR_RESET + "----------------------");
//                printConfirmRequest();
//                String confirm = scanner.nextLine();
//                if (!confirm.equalsIgnoreCase("confirm")){
//                    printError("Aborting force insert");
//                    return true;
//                }
//
//                if(InvoiceBase.forceInvoice(
//                        invoiceNumber,
//                        customer.getNumber(),
//                        month,
//                        day,
//                        year,
//                        balance
//                )){
//                    printSuccess("Forced invoice into Apache.database");
//                }else{
//
//                    printError("Failed to force invoice");
//                }
//                return true;
//            }
//        }

        return false;
    }


    @Override
    HelpMenu getHelpMenu() {
        return helpMenu;
    }
}
