package Apache.console;

import Apache.objects.CounterPerson;
import Apache.objects.Selectable;
import Apache.server.database.CounterPersonDatabase;
import Apache.util.InputRefiner;
import Apache.util.InputVerifier;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class CounterPersonConsole extends Console {

    private HelpMenu helpMenu;

    CounterPersonConsole(Scanner scanner, String prompt) {
        super(scanner, prompt);
        helpMenu = new HelpMenu();
        helpMenu.addOption("list", "List all counterpersons");
        helpMenu.addOption("create", "Create new counterperson");
        helpMenu.addOption("delete", "Delete existing counterperson");
    }

    @Override
    boolean handle(String input) {
        input = input.toUpperCase(Locale.ROOT);
        switch (input) {
            case "LIST" -> {
                List<CounterPerson> counterPeople = new ArrayList<>();
                try(CounterPersonDatabase counterPersonDatabase = new CounterPersonDatabase()){
                    counterPeople.addAll(counterPersonDatabase.getCounterPeople("*"));
                }catch(SQLException sqlException){
                    sqlException.printStackTrace();
                }
                if (counterPeople.size() == 0) {
                    Console.printError("No results");
                    return true;
                }
                for (Selectable counterPerson : counterPeople) {
                    System.out.println(counterPerson.getSelectableName());
                }
                return true;
            }

            case "CREATE" -> {
                System.out.print("Counter Person Number: ");
                String num = scanner.nextLine().trim();
                if (InputVerifier.verifyCounterPersonNumber(num)) {

                    try(CounterPersonDatabase counterPersonDatabase = new CounterPersonDatabase()){
                        if (counterPersonDatabase.getCounterPeople(num).size() == 0) {
                            System.out.print("Employee Name: ");
                            String name = scanner.nextLine().trim();
                            if (InputVerifier.verifyCounterPersonName(name)) {
                                CounterPerson counterPerson = new CounterPerson(
                                        Integer.parseInt(num),
                                        InputRefiner.capitalizeFirstLetters(name)
                                );

                                System.out.println("----New CounterPerson----");
                                System.out.println(Console.COLOR_PURPLE + counterPerson + Console.COLOR_RESET);
                                System.out.println("-------------------------");
                                System.out.print("Type \"Confirm\" to save this counterperson: ");

                                String confirm = scanner.nextLine().trim();
                                if(confirm.equalsIgnoreCase("CONFIRM")){
                                    if(counterPersonDatabase.addCounterPerson(counterPerson))
                                        Console.printSuccess("Saved counterperson to Apache.database");
                                    else
                                        Console.printError("Failed to update Apache.database");
                                }else{
                                    Console.printError("Counterperson not saved");
                                }
                                return true;
                            } else {
                                Console.printError("Invalid employee name");
                            }
                        } else {
                            Console.printError("Counter person already exists");
                        }

                    }catch (SQLException sqlException){
                        sqlException.printStackTrace();
                        return true;
                    }

                } else {
                    Console.printError("Invalid counter person number");
                }
                return true;
            }

            case "DELETE" -> {
                System.out.print("Enter counterperson number: ");
                String counter = scanner.nextLine();
                if(InputVerifier.verifyCounterPersonNumber(counter)){

                    try(CounterPersonDatabase counterPersonDatabase = new CounterPersonDatabase()){
                        CounterPerson counterPerson = counterPersonDatabase.getCounterPeople(counter).get(0);
                        if(counterPerson == null){
                            Console.printError("This counterperson does not exist");
                            return true;
                        }
                        System.out.println("----DELETE COUNTERPERSON----");
                        System.out.println(Console.COLOR_PURPLE + counterPerson + Console.COLOR_RESET);
                        System.out.println("----------------------------");
                        System.out.print("Type \"Confirm\" to delete this counterperson: ");
                        String confirm = scanner.nextLine();
                        if(confirm.equalsIgnoreCase("CONFIRM")){
                            if(counterPersonDatabase.deleteCounterPerson(Integer.parseInt(counter))){
                                Console.printSuccess("Counterperson was deleted");
                            }else{
                                Console.printError("Failed to delete counterperson");
                            }
                        }else{
                            Console.printError("Counterperson was not deleted");
                        }
                    } catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                    }
                }else{
                    Console.printError("Invalid counter person number");
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
