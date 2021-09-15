package Apache.server;

import Apache.server.database.CounterPersonDatabase;

import static Apache.util.JSONTransformer.toJson;
import static spark.Spark.get;

class CounterPersonServer {

    static void init(){
        get("/counterperson/:number", (req, res) -> {
            System.out.println("[GET] /counterperson/" + req.params(":number"));
            try (CounterPersonDatabase counterPersonDatabase = new CounterPersonDatabase()) {
                return toJson(counterPersonDatabase.getCounterPeople(req.params(":number")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

}
