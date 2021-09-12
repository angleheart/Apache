package Apache.console;

import Apache.objects.Customer;
import Apache.database.CustomerBase;
import Apache.util.InputRefiner;
import Apache.util.InputVerifier;

import java.util.Locale;
import java.util.Scanner;

public class CustomerModificationConsole extends Console {

    private HelpMenu helpMenu;
    private Customer customer;

    CustomerModificationConsole(Scanner scanner, String prompt, Customer customer) {
        super(scanner, prompt);
        this.customer = customer;
        helpMenu = new HelpMenu();
        helpMenu.addOption("name   ", "Change name");
        helpMenu.addOption("address", "Change address");
        helpMenu.addOption("phone  ", "Change phone");
        helpMenu.addOption("pricing", "Change default pricing");
        helpMenu.addOption("taxable", "Change default tax setting");
    }

    @Override
    boolean handle(String input) {
        input = input.toUpperCase(Locale.ROOT);
        switch (input) {
            case "NAME" -> {
                System.out.print("Enter new name: ");
                String in = scanner.nextLine().trim();
                if (Console.isExitCommand(in))
                    return true;

                if (InputVerifier.verifyCustomerName(in)) {
                    in = in.toUpperCase(Locale.ROOT);
                    if (customer == null) {
                        Console.printError("Sorry, a Apache.database error occurred");
                        return true;
                    }

                    Customer newCustomer = new Customer(
                            customer.getNumber(),
                            in,
                            customer.getAddress(),
                            customer.getCity(),
                            customer.getState(),
                            customer.getZip(),
                            customer.getPhone(),
                            customer.getPriceMultiplier(),
                            customer.isTaxable(),
                            customer.paysByInvoice()
                    );
                    System.out.println("----UPDATED CUSTOMER----");
                    System.out.println(Console.COLOR_PURPLE + newCustomer + Console.COLOR_RESET);
                    System.out.println("--------------------");
                    System.out.print("Type \"confirm\" to save this customer: ");

                    String confirm = scanner.nextLine();
                    if (confirm.equalsIgnoreCase("CONFIRM")) {
                        if (CustomerBase.updateName(customer.getNumber(), in)) {
                            Console.printSuccess("Customer updated successfully");
                        } else {
                            Console.printError("Failed to update customer");
                        }
                    } else {
                        Console.printError("Customer update cancelled");
                    }
                } else {
                    Console.printError("Customer name invalid");
                }
                return true;
            }

            case "ADDRESS" -> {
                System.out.print("Enter new address: ");
                String address = scanner.nextLine().trim();
                if (Console.isExitCommand(address))
                    return true;
                if (!InputVerifier.verifyAddress(address)) {
                    Console.printError("Invalid address");
                    return true;
                }

                System.out.print("Enter new city: ");
                String city = scanner.nextLine().trim();
                if (Console.isExitCommand(city))
                    return true;
                if (!InputVerifier.verifyCity(city)) {
                    Console.printError("Invalid city");
                    return true;
                }

                System.out.print("Enter new state: ");
                String state = scanner.nextLine().trim();
                if (Console.isExitCommand(state))
                    return true;
                if (!InputVerifier.verifyState(state)) {
                    Console.printError("Invalid state- provide 2 characters only");
                    return true;
                }

                System.out.print("Enter new zip code: ");
                String zip = scanner.nextLine().trim();
                if (Console.isExitCommand(zip))
                    return true;
                if (!InputVerifier.verifyZip(zip)) {
                    Console.printError("Invalid zip code");
                    return true;
                }

                address = address.toUpperCase(Locale.ROOT);
                city = city.toUpperCase(Locale.ROOT);
                state = state.toUpperCase(Locale.ROOT);

                if (customer == null) {
                    Console.printError("Sorry, a Apache.database error occurred");
                    return true;
                }

                Customer newCustomer = new Customer(
                        customer.getNumber(),
                        customer.getName(),
                        address,
                        city,
                        state,
                        zip,
                        customer.getPhone(),
                        customer.getPriceMultiplier(),
                        customer.isTaxable(),
                        customer.paysByInvoice()
                );
                System.out.println("----UPDATED CUSTOMER----");
                System.out.println(Console.COLOR_PURPLE + newCustomer + Console.COLOR_RESET);
                System.out.println("--------------------");
                System.out.print("Type \"Confirm\" to save this customer: ");
                String confirm = scanner.nextLine();
                if (confirm.equalsIgnoreCase("CONFIRM")) {
                    if (CustomerBase.updateAddress(customer.getNumber(), address, city, state, zip)) {
                        Console.printSuccess("Customer updated successfully");
                    } else {
                        Console.printError("Sorry, customer update failed");
                    }
                } else {
                    Console.printError("Customer update cancelled");
                }
                return true;
            }

            case "PHONE" -> {
                System.out.print("Enter new phone number: ");
                String in = scanner.nextLine().trim();
                if (Console.isExitCommand(in))
                    return true;
                if (!InputVerifier.verifyPhoneNumber(in)) {
                    if (!in.equalsIgnoreCase("")) {
                        Console.printError("Invalid phone number");
                        return true;
                    }
                }

                if (!in.equalsIgnoreCase(""))
                    in = InputRefiner.refinePhoneNumber(in);
                if (customer == null) {
                    Console.printError("Sorry, a Apache.database error occurred");
                    return true;
                }

                Customer newCustomer = new Customer(
                        customer.getNumber(),
                        customer.getName(),
                        customer.getAddress(),
                        customer.getCity(),
                        customer.getState(),
                        customer.getZip(),
                        in,
                        customer.getPriceMultiplier(),
                        customer.isTaxable(),
                        customer.paysByInvoice()
                );
                System.out.println("----UPDATED CUSTOMER----");
                System.out.println(Console.COLOR_PURPLE + newCustomer + Console.COLOR_RESET);
                System.out.println("--------------------");
                System.out.print("Type \"Confirm\" to save this customer: ");
                String confirm = scanner.nextLine();
                if (confirm.equalsIgnoreCase("CONFIRM")) {
                    if (CustomerBase.updatePhoneNumber(customer.getNumber(), in)) {
                        Console.printSuccess("Customer updated successfully");
                    } else {
                        Console.printError("Sorry, customer update failed");
                    }
                } else {
                    Console.printError("Customer update cancelled");
                }
                return true;
            }

            case "PRICING" -> {
                System.out.print("Enter new pricing multiplier: ");
                String in = scanner.nextLine().trim();
                if (Console.isExitCommand(in))
                    return true;

                if (!InputVerifier.verifyPriceMultiplier(in)) {
                    Console.printError("Invalid pricing multiplier");
                    return true;
                }

                if (customer == null) {
                    Console.printError("Sorry, a Apache.database error occurred");
                    return true;
                }

                double inDouble = Double.parseDouble(in);

                Customer newCustomer = new Customer(
                        customer.getNumber(),
                        customer.getName(),
                        customer.getAddress(),
                        customer.getCity(),
                        customer.getState(),
                        customer.getZip(),
                        customer.getPhone(),
                        inDouble,
                        customer.isTaxable(),
                        customer.paysByInvoice()
                );
                System.out.println("----UPDATED CUSTOMER----");
                System.out.println(Console.COLOR_PURPLE + newCustomer + Console.COLOR_RESET);
                System.out.println("--------------------");
                System.out.print("Type \"Confirm\" to save this customer: ");
                String confirm = scanner.nextLine();
                if (confirm.equalsIgnoreCase("CONFIRM")) {
                    if (CustomerBase.updatePricing(customer.getNumber(), inDouble)) {
                        Console.printSuccess("Customer updated successfully");
                    } else {
                        Console.printError("Sorry, customer update failed");
                    }
                } else {
                    Console.printError("Customer update cancelled");
                }
                return true;
            }


            case "TAXABLE" -> {
                System.out.print("Tax this customer? (Y/N): ");
                String in = scanner.nextLine().trim();
                if (Console.isExitCommand(in))
                    return true;
                boolean taxable;
                if (in.equalsIgnoreCase("Y")) {
                    taxable = true;
                } else if (in.equalsIgnoreCase("N")) {
                    taxable = false;
                } else {
                    Console.printError("Invalid input, enter \"Y\" or \"N\" only ");
                    return true;
                }

                if (customer == null) {
                    Console.printError("Sorry, a Apache.database error occured");
                    return true;
                }

                Customer newCustomer = new Customer(
                        customer.getNumber(),
                        customer.getName(),
                        customer.getAddress(),
                        customer.getCity(),
                        customer.getState(),
                        customer.getZip(),
                        customer.getPhone(),
                        customer.getPriceMultiplier(),
                        taxable,
                        customer.paysByInvoice()
                );
                System.out.println("----UPDATED CUSTOMER----");
                System.out.println(Console.COLOR_PURPLE + newCustomer + Console.COLOR_RESET);
                System.out.println("--------------------");
                System.out.print("Type \"Confirm\" to save this customer: ");
                String confirm = scanner.nextLine();
                if (confirm.equalsIgnoreCase("CONFIRM")) {
                    if (CustomerBase.updateTaxable(customer.getNumber(), taxable)) {
                        Console.printSuccess("Customer updated successfully");
                    } else {
                        Console.printError("Sorry, customer update failed");
                    }
                } else {
                    Console.printError("Customer update cancelled");
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
