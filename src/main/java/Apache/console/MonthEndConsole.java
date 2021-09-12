package Apache.console;

import java.util.Scanner;

public class MonthEndConsole extends Console{

    MonthEndConsole(Scanner scanner, String prompt) {
        super(scanner, prompt);
    }

    @Override
    boolean handle(String input) {
        return false;
    }

    @Override
    HelpMenu getHelpMenu() {
        return null;
    }
}
