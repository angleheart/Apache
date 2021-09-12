package Apache.console;

import java.util.LinkedHashMap;
import java.util.Map;

class HelpMenu {

    private Map<String, String> options = new LinkedHashMap<>();

    void addOption(String command, String description) {
        options.put(command, description);
    }

    void print() {
        StringBuilder str = new StringBuilder();
        str.append("-------AVAILABLE COMMANDS-------\n");
        for (Map.Entry<String, String> entry : options.entrySet()){
            str.append(Console.COLOR_CYAN);
            str.append(entry.getKey());
            str.append(Console.COLOR_RESET);
            str.append(" - ");
            str.append(entry.getValue()).append("\n");
        }
        str.append("------------------------------\n");
        System.out.print(str);
    }
}
