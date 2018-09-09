package ovh.shr.sportsfun.sportsfunapplication.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.SportsFunApplication;
import ovh.shr.sportsfun.sportsfunapplication.models.User;
import ovh.shr.sportsfun.sportsfunapplication.network.NetworkManager;
import ovh.shr.sportsfun.sportsfunapplication.network.RequestType;
import ovh.shr.sportsfun.sportsfunapplication.utilities.JsonHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity
{

    //region Declarations

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;


    private ProgressDialog progressDialog;

    @BindView(R.id.lblErrorMessage) TextView lblErrorMessage;
    @BindView(R.id.txtUsername) EditText txtUsername;
    @BindView(R.id.txtPassword) EditText txtPassword;

    @BindView(R.id.btnSubmit) Button btnSubmit;
    @BindView(R.id.lblLinkSignUp) TextView lblLinkSignUp;

    //endregion Declarations


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SportsFunApplication.setContext(getApplicationContext());

        try {
            JsonObject json = SportsFunApplication.getUserLogins();
            if (json.get("username").getAsString() != "") {
                SignIn(json.get("username").getAsString(), json.get("password").getAsString());
                return;
            }

        } catch (Exception err) {

        }

        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);


        //region Click on Submit button

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                lblErrorMessage.setVisibility(View.INVISIBLE);
                SignIn(txtUsername.getText().toString(), txtPassword.getText().toString());
            }
        });
        //endregion Click on Submit button

        //region Create Account

        lblLinkSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        //endregion Create Account

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public void SignIan(final String username, final String password)
    {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getApplicationContext().getString(R.string.progressSignIn));
        progressDialog.show();
        progressDialog.setCancelable(false);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", password);


        NetworkManager.PostRequest("api/user/login", jsonObject, RequestType.POST, new okhttp3.Callback() {

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                if (response.isSuccessful()) {
                    try {

                        Gson gson = new GsonBuilder().create();
                        JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);
                        SportsFunApplication.setAuthentificationToken(json.getAsJsonObject("data").get("token").getAsString());
                        NetworkManager.PostRequest("api/user", null, RequestType.GET, new okhttp3.Callback() {

                            @Override
                            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                                progressDialog.dismiss();

                                Gson gson = new GsonBuilder().create();
                                JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);

                                User user = gson.fromJson(json.get("data").getAsJsonObject(), User.class);

                                SportsFunApplication.setCurrentUser(user);

                                SportsFunApplication.SaveUserLogins(username, password);
                                Intent i = new Intent(getBaseContext(), MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();

                            }

                            @Override
                            public void onFailure(okhttp3.Call call, IOException e) {
                                e.printStackTrace();

                                progressDialog.dismiss();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        lblErrorMessage.setVisibility(View.VISIBLE);
                                        lblErrorMessage.setText(getBaseContext().getString(R.string.lblErrorAuthMessage));
                                    }
                                });

                            }

                        });

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                lblErrorMessage.setVisibility(View.VISIBLE);
                                lblErrorMessage.setText(getBaseContext().getString(R.string.connectionerror));
                            }
                        });
                        e.printStackTrace();
                    }
                } else
                {
                    Gson gson = new GsonBuilder().create();
                    JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);

                    String errorMessage = json.get("message").getAsString();

                    progressDialog.dismiss();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lblErrorMessage.setVisibility(View.VISIBLE);
                            lblErrorMessage.setText(getBaseContext().getString(R.string.lblErrorAuthMessage));
                        }
                    });

                }
            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lblErrorMessage.setVisibility(View.VISIBLE);
                        lblErrorMessage.setText(getBaseContext().getString(R.string.connectionerror));
                    }
                });
            }

        });
    }







    //region Private methods

    private void SignIn(final String username, final String password) {
        System.out.println("TEST1");
        DisplayAuthentificationMessage();

        JsonObject newJsonObject = new JsonObject();
        newJsonObject.addProperty("username", username);
        newJsonObject.addProperty("password", password);

        NetworkManager.PostRequest("api/user/login", newJsonObject, RequestType.POST, new okhttp3.Callback() {

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                if (response.isSuccessful()) {

                    JsonObject json = JsonHelper.GetJsonObject(response.body().string());
                    SportsFunApplication.setAuthentificationToken(json.getAsJsonObject("data").get("token").getAsString());
                    SportsFunApplication.SaveUserLogins(username, password);
                    GetUserInformations();
                }
                else
                {
                    progressDialog.dismiss();
                    JsonObject json = JsonHelper.GetJsonObject(response.body().string());
                    if (json.get("message").getAsString().contains("Missing key")) {
                        DisplayErrorMessage(getText(R.string.lblErrorMessageMissingFields));
                    }

                    if (json.get("message").getAsString().contains("Incorrect password") || json.get("message").getAsString().contains("does not exist")) {
                        DisplayErrorMessage(getText(R.string.incorrectPassword));
                    }
                }
            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                progressDialog.dismiss();
                DisplayErrorMessage(getText(R.string.connectionerror));
            }

        });

    }

    public void GetUserInformations() {
        System.out.println("TEST2");

        NetworkManager.PostRequest("api/user", null, RequestType.GET, new okhttp3.Callback() {

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                progressDialog.dismiss();

                Gson gson = new GsonBuilder().create();
                JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);
                User user = gson.fromJson(json.get("data").getAsJsonObject(), User.class);
                SportsFunApplication.setCurrentUser(user);

                Intent i = new Intent(getBaseContext(), MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();

            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                progressDialog.dismiss();
                DisplayErrorMessage(getText(R.string.lblErrorAuthMessage));

            }

        });

    }

    private void DisplayAuthentificationMessage() {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getApplicationContext().getString(R.string.progressSignIn));
        progressDialog.show();
        progressDialog.setCancelable(false);
    }

    private void DisplayErrorMessage(final CharSequence message) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                lblErrorMessage.setVisibility(View.VISIBLE);
                lblErrorMessage.setText(message);
            }

        });

    }

    //endregion Private methods

}
