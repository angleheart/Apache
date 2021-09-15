package Apache.server;

import Apache.config.Config;
import Apache.server.database.*;

import static spark.Spark.*;

public class ServerMain {

    public static void main(String[] args) {
        if (!init())
            return;
        System.out.println("[SERVER] initialized and listening on port " + Config.SERVER_RUN_PORT);
    }

    private static boolean init() {
        System.out.println("Setting port...");
        port(Config.SERVER_RUN_PORT);
        System.out.println("[SERVER] Getting data source...");
        if(!Database.init()){
            System.out.println("[SERVER] Failed to initiate database");
            return false;
        }
        VehicleServer.init();
        InvoiceServer.init();
        CustomerServer.init();
        CounterPersonServer.init();
        PartsServer.init();
        ReceivableServer.init();
        SequenceServer.init();
        return true;
    }

}
