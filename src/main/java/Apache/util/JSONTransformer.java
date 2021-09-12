package Apache.util;

import com.google.gson.Gson;

public class JSONTransformer {

    public static String toJson(Object object) {
        return new Gson().toJson(object);
    }


    public static <T> T fromJson(Class<T> clazz, String json) {
        return new Gson().fromJson(json, clazz);
    }

}
