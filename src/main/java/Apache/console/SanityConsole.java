package Apache.console;

import java.util.Scanner;

public class SanityConsole extends Console{



    SanityConsole(Scanner scanner, String prompt) {
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
