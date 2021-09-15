package Apache.server;

import Apache.server.database.PartDatabase;

import static Apache.util.JSONTransformer.toJson;
import static spark.Spark.get;

class PartsServer {

    static void init(){
        get("/parts/:mfg/:number", (req, res) -> {
            System.out.println("[GET] /parts/" + req.params(":mfg") + "/" + req.params(":number"));
            try (PartDatabase partDatabase = new PartDatabase()) {
                return toJson(partDatabase.getParts(req.params(":mfg"), req.params(":number")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

}
