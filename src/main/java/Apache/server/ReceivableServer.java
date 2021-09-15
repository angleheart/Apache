package Apache.server;

import Apache.server.database.ReceivableDatabase;

import static Apache.util.JSONTransformer.toJson;
import static spark.Spark.get;

class ReceivableServer {

    static void init(){
        get("/receivable/:customer", (req, res) -> {
            System.out.println("[GET] /receivable/" + req.params(":customer"));
            try (ReceivableDatabase receivableDatabase = new ReceivableDatabase()) {
                return toJson(receivableDatabase.getReceivable(req.params(":customer")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

}
