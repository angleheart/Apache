package Apache.console;

import Apache.config.Config;
import Apache.objects.Customer;
import Apache.database.CustomerBase;
import Apache.util.InputRefiner;
import Apache.util.InputVerifier;

import java.util.Locale;
import java.util.Scanner;

class CustomerCreator {

    private final Scanner scanner;
    private String number;
    private String name;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String phone;
    private double priceMultiplier;


    private boolean taxable;
    private boolean payByInvoice;


    CustomerCreator(Scanner scanner) {
        this.scanner = scanner;
    }

    void performRequest() {

        do {
            System.out.print("Customer Number: ");
            number = scanner.nextLine().trim();
            if (Console.isExitCommand(number))
                return;
            if (InputVerifier.verifyCustomerNumber(number)) {
                if (CustomerBase.getCustomerByNumber(number) == null)
                    break;
                Console.printError("Customer already exists");
            } else {
                Console.printError("Invalid customer number");
            }
        } while (true);

        do {
            System.out.print("Customer Name: ");
            name = scanner.nextLine().trim();
            if (Console.isExitCommand(name))
                return;
            if (InputVerifier.verifyCustomerName(name))
                break;
            Console.printError("Invalid customer name");
        } while (true);

        do {
            System.out.print("Street Address: ");
            address = scanner.nextLine().trim();
            if (address.equalsIgnoreCase(""))
                break;

            if (Console.isExitCommand(address))
                return;
            if (InputVerifier.verifyAddress(address))
                break;
            Console.printError("Invalid address - provide number and street name");
        } while (true);

        do {
            System.out.print("City: ");
            city = scanner.nextLine().trim();
            if (city.equalsIgnoreCase(""))
                break;
            if (Console.isExitCommand(city))
                return;
            if (InputVerifier.verifyCity(city))
                break;
            Console.printError("Invalid city");
        } while (true);

        do {
            System.out.print("State: ");
            state = scanner.nextLine().trim();
            if (state.equalsIgnoreCase(""))
                break;
            if (Console.isExitCommand(state))
                return;
            if (InputVerifier.verifyState(state))
                break;
            Console.printError("Invalid state- provide 2 characters only");
        } while (true);

        do {
            System.out.print("Zip: ");
            zip = scanner.nextLine().trim();
            if (zip.equalsIgnoreCase(""))
                break;
            if (Console.isExitCommand(zip))
                return;
            if (InputVerifier.verifyZip(zip))
                break;
            Console.printError("Invalid zip code");
        } while (true);


        do {
            System.out.print("Customer Phone: ");
            phone = scanner.nextLine();
            if (phone.equalsIgnoreCase(""))
                break;
            if (Console.isExitCommand(phone))
                return;
            if (InputVerifier.verifyPhoneNumber(phone))
                break;
            Console.printError("Invalid phone number");
        } while (true);

        do {
            System.out.print("Price Multiplier: ");
            String priceMultiplierStr = scanner.nextLine().trim();
            if (priceMultiplierStr.equalsIgnoreCase(""))
                priceMultiplier = Config.DEFAULT_COST_MULTIPLIER;
            if (Console.isExitCommand(priceMultiplierStr))
                return;
            if (InputVerifier.verifyPriceMultiplier(priceMultiplierStr)) {
                priceMultiplier = Double.parseDouble(priceMultiplierStr);
                break;
            }
            Console.printError("Invalid price multiplier");
        } while (true);

        do {
            System.out.print("Taxable? (Y/N): ");
            String input = scanner.nextLine().trim().toUpperCase(Locale.ROOT);
            if (input.startsWith("Y")) {
                taxable = true;
                break;
            } else if (input.startsWith("N")) {
                taxable = false;
                break;
            }
            Console.printError("Invalid option- enter \'Y\' or \'N\'");

        } while (true);

        do {
            System.out.print("Pay by invoice? (Y/N): ");
            String input = scanner.nextLine().trim().toUpperCase(Locale.ROOT);
            if (input.startsWith("Y")) {
                payByInvoice = true;
                break;
            } else if (input.startsWith("N")) {
                payByInvoice = false;
                break;
            }
            Console.printError("Invalid option- enter \'Y\' or \'N\'");
        } while (true);

        refineInput();
        requestConfirmation(new Customer(
                number,
                name,
                address,
                city,
                state,
                zip,
                phone,
                priceMultiplier,
                taxable,
                payByInvoice
        ));
    }

    private void refineInput() {
        name = name.toUpperCase(Locale.ROOT);
        number = number.toUpperCase(Locale.ROOT);
        address = address.toUpperCase(Locale.ROOT);
        city = city.toUpperCase(Locale.ROOT);
        state = state.toUpperCase(Locale.ROOT);
        if (!phone.equalsIgnoreCase(""))
            phone = InputRefiner.refinePhoneNumber(phone);
    }

    private void requestConfirmation(Customer customer) {
        System.out.println("----NEW CUSTOMER----");
        System.out.println(Console.COLOR_PURPLE + customer + Console.COLOR_RESET);
        System.out.println("--------------------");
        System.out.print("Type \"Confirm\" to save this customer: ");
        String confirm = scanner.nextLine();
        if (confirm.equalsIgnoreCase("CONFIRM")) {
            if (CustomerBase.addNewCustomer(customer))
                Console.printSuccess("Successfully saved " + customer.getName() + " in Apache.database");
            else
                Console.printError("Failed to save " + customer.getName() + " to the Apache.database");
        } else {
            Console.printError("Customer not saved");
        }
    }

}
