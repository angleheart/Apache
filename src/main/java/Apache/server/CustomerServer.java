package Apache.server;

import Apache.server.database.CustomerDatabase;

import static Apache.util.JSONTransformer.toJson;
import static spark.Spark.get;

class CustomerServer {

    static void init(){
        get("/customers/:query", (req, res) -> {
            System.out.println("[GET] /customers/" + req.params(":query"));
            try (CustomerDatabase customerDatabase = new CustomerDatabase()) {
                return toJson(customerDatabase.getCustomers(req.params(":query")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

}
