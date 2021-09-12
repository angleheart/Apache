package Apache;

import Apache.config.Config;
import Apache.console.ConsoleMain;
import Apache.invoicer.InvoicerApplication;
import Apache.server.ServerMain;
import Apache.workstation.WorkstationApplication;

public class Main {

    public static void main(String[] args) {
        if(args.length == 0){
            if(!Config.parse("apache.properties")){
                System.out.println("Failed to parse configuration");
                return;
            }
            WorkstationApplication.launchWorkstation(args);
        }else if(args.length >= 2){
            if(!Config.parse(args[1])){
                System.out.println("Failed to parse configuration");
                return;
            }
            switch(args[0]){
                case "workstation" -> WorkstationApplication.launchWorkstation(args);
                case "server" -> ServerMain.main(args);
                case "console" -> ConsoleMain.main(args);
                case "invoicer" -> InvoicerApplication.main(args);
                default -> System.out.println("Invalid launch syntax");
            }
        }
    }

}
