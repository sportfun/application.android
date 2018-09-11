package ovh.shr.sportsfun.sportsfunapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;
import ovh.shr.sportsfun.sportsfunapplication.models.User;
import ovh.shr.sportsfun.sportsfunapplication.network.NetworkManager;
import ovh.shr.sportsfun.sportsfunapplication.network.RequestType;

public class SportsFunApplication {


    //region Declarations

    private static String AuthentificationToken;
    private static User CurrentUser;
    private static Context context;

    //endregion Declarations

    //region Getters & Setters

    public static String getAuthentificationToken() {
        return SportsFunApplication.AuthentificationToken;
    }

    public static void setAuthentificationToken(String authentificationToken) {
        System.out.println("Changing the authentification token :");
        System.out.println(authentificationToken);
        SportsFunApplication.AuthentificationToken = authentificationToken;
    }


    public static void SaveUserLogins(String username, String password) {

        SharedPreferences SaveData = context.getSharedPreferences("userdetails", 0);
        SharedPreferences.Editor edit = SaveData.edit();
        edit.putString("username", username);
        edit.putString("password", password);
        edit.apply();

    }
    public static JsonObject getUserLogins(){
        SharedPreferences SaveData = context.getSharedPreferences("userdetails", 0);

        JsonObject json = new JsonObject();

        json.addProperty("username", SaveData.getString("username", ""));
        json.addProperty("password", SaveData.getString("password", ""));

        return json;
    }

    public static void deleteUserLogins() {
        SharedPreferences preferences = context.getSharedPreferences("userdetails", 0);
        preferences.edit().remove("username").commit();
        preferences.edit().remove("password").commit();
    }


    public static User getCurrentUser() {
        return CurrentUser;
    }

    public static void setCurrentUser(User currentUser) {
        CurrentUser = currentUser;
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        SportsFunApplication.context = context;
    }

    //endregion Getter & Setters

}
