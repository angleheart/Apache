package Apache.console;

import java.util.Locale;
import java.util.Scanner;

abstract class Console {

    public static final String COLOR_RESET = "\u001B[0m";
    public static final String COLOR_BLACK = "\u001B[30m";
    public static final String COLOR_RED = "\u001B[31m";
    public static final String COLOR_GREEN = "\u001B[32m";
    public static final String COLOR_YELLOW = "\u001B[33m";
    public static final String COLOR_BLUE = "\u001B[34m";
    public static final String COLOR_PURPLE = "\u001B[35m";
    public static final String COLOR_CYAN = "\033[0;96m";
    public static final String COLOR_WHITE = "\u001B[37m";

    private final String prompt;
    private boolean end;

    final Scanner scanner;

    abstract boolean handle(String input);

    abstract HelpMenu getHelpMenu();

    Console(Scanner scanner, String prompt) {
        this.scanner = scanner;
        this.prompt = prompt;
    }

    void startConsole() {
        do {
            System.out.print(COLOR_YELLOW + prompt + COLOR_RESET);
            String input = scanner.nextLine();
            String capsInput = input.toUpperCase(Locale.ROOT);
            switch (capsInput) {
                case "END", "EXIT", "CANCEL", "QUIT" -> end = true;
                case "CLEAR", "CLS" -> System.out.print("\033[H\033[2J");
                case "HELP" -> getHelpMenu().print();
                default -> {
                    if (!handle(input.trim()) && !input.trim().equals(""))
                        System.out.println("Unknown command. Enter \"help\" for help");
                }
            }
        } while (!end);
    }


    static void printAlert(String message){
        java.awt.Toolkit.getDefaultToolkit().beep();
        System.out.println(COLOR_RED + message + COLOR_RESET);
    }

    static void printError(String message) {
        java.awt.Toolkit.getDefaultToolkit().beep();
        System.out.println("[" + COLOR_RED + "ERROR" + COLOR_RESET + "]: " + message);
    }

    static void printWarning(String message) {
        java.awt.Toolkit.getDefaultToolkit().beep();
        System.out.println();
        System.out.println("[" + COLOR_RED + "WARNING" + COLOR_RESET + "]: " + message);
        System.out.println();
    }

    static void printConfirmRequest(){
        System.out.print("Enter \"" + COLOR_CYAN + "Confirm" + COLOR_RESET + "\" to validate this action: ");
    }

    static void printSuccess(String message) {
        System.out.println("[" + COLOR_GREEN + "SUCCESS" + COLOR_RESET + "]: " + message);
    }

    static boolean isExitCommand(String s) {
        return s.equalsIgnoreCase("END") ||
                s.equalsIgnoreCase("EXIT") ||
                s.equalsIgnoreCase("QUIT") ||
                s.equalsIgnoreCase("CANCEL");
    }

}
