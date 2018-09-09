package ovh.shr.sportsfun.sportsfunapplication.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import ovh.shr.sportsfun.sportsfunapplication.SportsFunApplication;

public class JsonHelper {

    public static JsonObject GetJsonObject(String json) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, JsonObject.class);
    }

}
