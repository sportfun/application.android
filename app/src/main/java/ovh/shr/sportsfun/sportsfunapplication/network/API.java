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

    public static void GetUser(String userID, final SCallback callback) {

        NetworkManager.PostRequest("api/user" + ( userID != null ? '/' + userID : "" ), null, RequestType.GET, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("success", false);
                jsonObject.addProperty("message", "failed_to_connect");
                callback.onTaskCompleted(jsonObject);
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

    public static void GetConversation(String parnterID, final SCallback callback)
    {
        NetworkManager.PostRequest("api/message/" + parnterID, null, RequestType.GET, new  okhttp3.Callback() {

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

    public static void Login(final String username, final String password, final SCallback callback)  {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", password);

        NetworkManager.PostRequest("api/user/login", jsonObject, RequestType.POST, new  okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

                JsonObject tmp = new JsonObject();
                tmp.addProperty("success", false);
                tmp.addProperty("message", "failed_to_connect");
                callback.onTaskCompleted(tmp);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new GsonBuilder().create();
                JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);

                if (response.isSuccessful()) {
                    if (callback != null)
                    {
                        SportsFunApplication.SaveUserLogins(username, password);
                        SportsFunApplication.setAuthentificationToken(json.getAsJsonObject("data").get("token").getAsString());
                        callback.onTaskCompleted(json);
                    }
                } else {
                    if (callback != null)
                    {
                        callback.onTaskCompleted(json);
                    }
                }
            }

        });

    }

    public static void Register(JsonObject jsonObject, final SCallback callback) {
        NetworkManager.PostRequest("api/user", jsonObject, RequestType.POST, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                JsonObject result = new JsonObject();
                result.addProperty("success", false);
                result.addProperty("message", "failed_to_connect");
                callback.onTaskCompleted(result);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new GsonBuilder().create();
                JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);

                if (response.isSuccessful()) {
                    if (callback != null)
                    {
                        callback.onTaskCompleted(json);
                    }
                } else {
                    if (callback != null)
                    {
                        callback.onTaskCompleted(json);
                    }
                }
            }

        } );

    }


    public static void SendQRCode(final String code, final SCallback callback) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("qr", code);

        NetworkManager.PostRequest("api/qr", jsonObject, RequestType.PUT, new  okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

                JsonObject tmp = new JsonObject();
                tmp.addProperty("success", false);
                tmp.addProperty("message", "failed_to_connect");
                callback.onTaskCompleted(tmp);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new GsonBuilder().create();
                JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);

                if (response.isSuccessful()) {
                    if (callback != null)
                    {
                        callback.onTaskCompleted(json);
                    }
                } else {
                    if (callback != null)
                    {
                        callback.onTaskCompleted(json);
                    }
                }
            }
        });
    }

    public static void UpdateUser(String userID, final JsonObject jsonObject, final SCallback callback) {

        NetworkManager.PostRequest("api/user" + (userID != null ? "/" + userID : ""), jsonObject, RequestType.PUT, new  okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

                JsonObject tmp = new JsonObject();
                tmp.addProperty("success", false);
                tmp.addProperty("message", "failed_to_connect");
                callback.onTaskCompleted(tmp);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new GsonBuilder().create();
                JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);

                if (response.isSuccessful()) {
                    if (callback != null)
                    {
                        callback.onTaskCompleted(json);
                    }
                } else {
                    if (callback != null)
                    {
                        callback.onTaskCompleted(json);
                    }
                }
            }
        });
    }

    public static void GetActivities(final SCallback callback) {

        NetworkManager.PostRequest("api/activity", null, RequestType.GET, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

                JsonObject tmp = new JsonObject();
                tmp.addProperty("success", false);
                tmp.addProperty("message", "failed_to_connect");
                callback.onTaskCompleted(tmp);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new GsonBuilder().create();
                JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);

                if (response.isSuccessful()) {
                    if (callback != null)
                    {
                        callback.onTaskCompleted(json);
                    }
                } else {
                    if (callback != null)
                    {
                        callback.onTaskCompleted(json);
                    }
                }
            }

        });

    }

    public static void ResetPassword(String username, String email, final SCallback callback) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("email", email);

        NetworkManager.PostRequest("api/user/password", jsonObject, RequestType.LOCK, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                JsonObject tmp = new JsonObject();
                tmp.addProperty("success", false);
                tmp.addProperty("message", "failed_to_connect");
                callback.onTaskCompleted(tmp);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new GsonBuilder().create();
                JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);
                System.out.println(json.toString());
                if (response.isSuccessful()) {
                    if (callback != null)
                    {
                        callback.onTaskCompleted(json);
                    }
                } else {
                    if (callback != null)
                    {
                        callback.onTaskCompleted(json);
                    }
                }
            }

        });

    }


}