package ovh.shr.sportsfun.sportsfunapplication.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.SportsFunApplication;
import ovh.shr.sportsfun.sportsfunapplication.models.User;
import ovh.shr.sportsfun.sportsfunapplication.network.API;
import ovh.shr.sportsfun.sportsfunapplication.network.NetworkManager;
import ovh.shr.sportsfun.sportsfunapplication.network.RequestType;
import ovh.shr.sportsfun.sportsfunapplication.utilities.JsonHelper;
import ovh.shr.sportsfun.sportsfunapplication.utilities.SCallback;
import ovh.shr.sportsfun.sportsfunapplication.utilities.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity
{

    //region Declarations

    @BindView(R.id.lblErrorMessage) TextView lblErrorMessage;
    @BindView(R.id.lblUsername) TextInputLayout lblUsername;
    @BindView(R.id.lblPassword) TextInputLayout lblPassword;
    @BindView(R.id.txtUsername) EditText txtUsername;
    @BindView(R.id.txtPassword) EditText txtPassword;
    @BindView(R.id.btnSubmit) Button btnSubmit;
    @BindView(R.id.lblLinkSignUp) TextView lblLinkSignUp;
    @BindView(R.id.lblLinkResetPassword) TextView lblLinkResetPassword;
    @BindView(R.id.sportsfun) TextView sportsfun;

    private ProgressDialog progressDialog;

    //endregion Declarations


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SportsFunApplication.setContext(getApplicationContext());

        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);
        setVisibility(View.VISIBLE);

        String txtSports = Utils.getColoredSpanned("Sports", "#2c3e50");
        String txtFun = Utils.getColoredSpanned("Fun","#EA973E");
        sportsfun.setText(Html.fromHtml(txtSports + txtFun, 0));

        try {
            JsonObject json = SportsFunApplication.getUserLogins();
            if (json.has("username") && json.has("password") && json.get("username").getAsString() != "") {

                setVisibility(View.INVISIBLE);
                Login(json.get("username").getAsString(), json.get("password").getAsString());
            }

        } catch (Exception error) {

        }
    }


    //region Buttons

    @OnClick(R.id.btnSubmit)
    public void btnSubmit_OnClick()
    {
        lblErrorMessage.setVisibility(View.INVISIBLE);
        Login(txtUsername.getText().toString(), txtPassword.getText().toString());
    }

    @OnClick(R.id.lblLinkSignUp)
    public void lblLinkSignUp_OnClick()
    {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivityForResult(intent, 0);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @OnClick(R.id.lblLinkResetPassword)
    public void lblLinkResetPassword_OnClick() {
        Intent intent = new Intent(getApplicationContext(), ResetPassword.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivityForResult(intent, 1);
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    //endregion Buttons

    //region Private methods

    private void DisplayError(final CharSequence message) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                toast.show();

                lblErrorMessage.setVisibility(View.VISIBLE);
                lblErrorMessage.setText(message);
            }

        });
    }

    private void StartProgressDialog() {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getApplicationContext().getString(R.string.progressSignIn));
        progressDialog.show();
        progressDialog.setCancelable(false);
    }

    private void StopProgressDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        });
    }

    private void setVisibility(int visibility) {

        btnSubmit.setVisibility(visibility);
        lblLinkSignUp.setVisibility(visibility);
        lblUsername.setVisibility(visibility);
        txtUsername.setVisibility(visibility);
        lblPassword.setVisibility(visibility);
        txtPassword.setVisibility(visibility);
        lblLinkResetPassword.setVisibility(visibility);

    }

    private void StartMainActivity() {

        Intent i = new Intent(getBaseContext(), MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();

    }

    private void Login(String username, String password) {
        StartProgressDialog();
        API.Login(username, password, new SCallback() {

            @Override
            public void onTaskCompleted(JsonObject result) {

                StopProgressDialog();

                if (result.has("success") && result.get("success").getAsBoolean() == false) {
                    switch (result.get("message").getAsString()) {

                        case "failed_to_connect":
                            DisplayError(getText(R.string.connectionerror));
                            break;

                        case "Access forbidden":
                        case "Missing key 'username' in body":
                        case "Missing key 'password' in body":
                            DisplayError(getText(R.string.lblErrorAuthMessage));
                            break;
                    }
                    setVisibility(View.VISIBLE);
                } else {

                    API.RefreshLocalUserData(new SCallback() {
                        @Override
                        public void onTaskCompleted(JsonObject result) {
                            StartMainActivity();
                        }
                    });

                }
            }
        });
    }

    //endregion Private methods

}