package ovh.shr.sportsfun.sportsfunapplication.network;

import android.os.Handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;
import ovh.shr.sportsfun.sportsfunapplication.SportsFunApplication;
import ovh.shr.sportsfun.sportsfunapplication.models.User;
import ovh.shr.sportsfun.sportsfunapplication.utilities.SCallback;

public class API {

    public static void ToogleFollow(String userId, final SCallback callback)
    {

        NetworkManager.PostRequest("api/user/link/" + userId, null, RequestType.PUT, new okhttp3.Callback(){

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JsonObject tmp = new JsonObject();
                tmp.addProperty("success", true);
                callback.onTaskCompleted(tmp);
            }

        });
    }

    public static void RefreshLocalUserData(final SCallback callback)
    {
        NetworkManager.PostRequest("api/user", null, RequestType.GET, new  okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Gson gson = new GsonBuilder().create();
                    JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);
                    SportsFunApplication.setCurrentUser(gson.fromJson(json.get("data").getAsJsonObject(), User.class));

                    if (callback != null)
                    {
                        JsonObject tmp = new JsonObject();
                        tmp.addProperty("success", true);
                        callback.onTaskCompleted(tmp);
                    }
                } else {
                    if (callback != null)
                    {
                        JsonObject tmp = new JsonObject();
                        tmp.addProperty("success", false);
                        callback.onTaskCompleted(tmp);
                    }
                }
            }

        });

    }



    public static void GetConversation(String parnterID, final SCallback callback)
    {
        NetworkManager.PostRequest("api/message/" + parnterID, null, RequestType.GET, new  okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("lol2");

                if (response.isSuccessful()) {
                    Gson gson = new GsonBuilder().create();
                    JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);
                    System.out.println("lol3");


                    if (callback != null)
                    {
                        callback.onTaskCompleted(json);
                    }
                } else {
                    if (callback != null)
                    {
                        System.out.println("lol4");

                        JsonObject tmp = new JsonObject();
                        tmp.addProperty("success", false);
                        callback.onTaskCompleted(tmp);
                    }
                }
            }

        });

    }

    public static void SendMessage(String to, String message, final SCallback callback)
    {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("content", message);
        jsonObject.addProperty("to", to);

        NetworkManager.PostRequest("api/message", jsonObject, RequestType.POST, new  okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    Gson gson = new GsonBuilder().create();
                    JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);

                    if (callback != null)
                    {
                        callback.onTaskCompleted(json);
                    }
                } else {
                    if (callback != null)
                    {
                        JsonObject tmp = new JsonObject();
                        tmp.addProperty("success", false);
                        callback.onTaskCompleted(tmp);
                    }
                }
            }

        });

    }


}
