package Apache.server;

import Apache.server.database.VehicleDatabase;

import static Apache.util.JSONTransformer.toJson;
import static spark.Spark.get;

class VehicleServer {

    static void init(){

        get("/vehicles/engines/:year/:make/:model", (req, res) -> {
            System.out.println("[GET] /vehicles/engines/" +
                    req.params(":year") + "/" +
                    req.params(":make") + "/" +
                    req.params(":model")
            );
            try (VehicleDatabase vehicleDatabase = new VehicleDatabase()) {
                return toJson(
                        vehicleDatabase.getEngines(
                                req.params(":year"),
                                req.params(":make"),
                                req.params(":model")
                        )
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        get("/vehicles/models/:year/:make", (req, res) -> {
            System.out.println("[GET] /vehicles/models/" + req.params(":year") + "/" + req.params(":make"));
            try (VehicleDatabase vehicleDatabase = new VehicleDatabase()) {
                return toJson(vehicleDatabase.getModels(req.params(":year"), req.params(":make")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        get("/vehicles/makes/:year", (req, res) -> {
            System.out.println("[GET] /vehicles/makes/" + req.params(":year"));
            try (VehicleDatabase vehicleDatabase = new VehicleDatabase()) {
                return toJson(vehicleDatabase.getMakes(req.params(":year")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        get("/vehicles/years", (req, res) -> {
            System.out.println("[GET] /vehicles/years");
            try (VehicleDatabase vehicleDatabase = new VehicleDatabase()) {
                return toJson(vehicleDatabase.getYears());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

    }

}
