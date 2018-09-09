package ovh.shr.sportsfun.sportsfunapplication.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ovh.shr.sportsfun.sportsfunapplication.SportsFunApplication;

public class NetworkManager {

    //region Declarations

    public static final String BASE_API_URL = "http://api.sportsfun.shr.ovh:8080/";
    public static final String BASE_CDN = "http://api.sportsfun.shr.ovh:8080";

    private static final OkHttpClient client = new OkHttpClient();

    //endregion Declarations

    //region Public methods

    public static void PostRequest(String ENDPOINT, JsonObject jsonObject, RequestType requestType, Callback callback)
    {
        PostRequest(ENDPOINT, jsonObject, requestType, SportsFunApplication.getAuthentificationToken(), callback);
    }

    public static void PostRequest(String ENDPOINT, JsonObject jsonObject, RequestType requestType, String AuthentificationToken, Callback callback)
    {
        RequestBody body;
        Request.Builder requestBuilder = new Request.Builder();

        requestBuilder.url(BASE_API_URL + ENDPOINT);
        requestBuilder.addHeader("Content-Type", "application/json");

        if (AuthentificationToken != null) {
            requestBuilder.addHeader("token", AuthentificationToken);
        }

        if (jsonObject == null)
            jsonObject = new JsonObject();

        switch (requestType) {

            case GET:
                requestBuilder.get();
                break;

            case PUT:
                body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                requestBuilder.put(body);
                break;

            case POST:
                body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                requestBuilder.post(body);
                break;

            case DELETE:
                body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                requestBuilder.delete(body);
                break;
        }

        client.newCall(requestBuilder.build()).enqueue(callback);

    }

    //endregion Public methods

}