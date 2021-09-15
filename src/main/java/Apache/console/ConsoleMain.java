package Apache.console;

import Apache.server.database.Database;

import java.util.Scanner;

public class ConsoleMain {

    public static void main(String[] args) {
        MainMenuConsole mainMenuConsole = new MainMenuConsole(new Scanner(System.in), "Apache > ");
        mainMenuConsole.startConsole();
    }


}
