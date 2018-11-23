package ovh.shr.sportsfun.sportsfunapplication.activity.User;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.activity.CommentActivity;
import ovh.shr.sportsfun.sportsfunapplication.activity.LoginActivity;
import ovh.shr.sportsfun.sportsfunapplication.network.NetworkManager;
import ovh.shr.sportsfun.sportsfunapplication.network.RequestType;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class EditPasswordActivity extends AppCompatActivity {

    //region Declarations

    @BindView(R.id.lblErrorMessage) TextView lblErrorMessage;

    @BindView(R.id.txtCurrentPassword) EditText txtCurrentPassword;
    @BindView(R.id.txtPassword) EditText txtNewPassword;
    @BindView(R.id.txtConfirmPassword) EditText txtConfirmPassword;

    @BindView(R.id.layoutToolbar) Toolbar layoutToolbar;
    private ActionBar actionBar;
    private ProgressDialog progressDialog;


    //endregion Declarations

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);
        ButterKnife.bind(this);

        setSupportActionBar(layoutToolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }


    //region Toolbar

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    //endregion Toolbar

    //region Public methods

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, EditPasswordActivity.class);
        context.startActivity(intent);
    }

    @OnClick(R.id.btnSaveChanges)
    protected void SaveChanges() {

        if (CheckPasswords()) {
            CommitChanges();
        }

    }


    //endregion Public methods

    //region Private methods
    private boolean CheckPasswords() {

        if (txtCurrentPassword.getText().toString().length() == 0
                || txtNewPassword.getText().toString().length() == 0
                || txtConfirmPassword.getText().toString().length() == 0)
        {
            lblErrorMessage.setText(getText(R.string.lblErrorMessageMissingFields));
            lblErrorMessage.setVisibility(View.VISIBLE);
            return false;
        }


        if (txtNewPassword.getText().toString().equals(txtConfirmPassword.getText().toString())) {
            lblErrorMessage.setVisibility(View.INVISIBLE);
            return true;
        } else {
            lblErrorMessage.setText(getText(R.string.passwordsDontMatch));
            lblErrorMessage.setVisibility(View.VISIBLE);
            return false;
        }

    }

    private void CommitChanges() {
        progressDialog = new ProgressDialog(EditPasswordActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getApplicationContext().getString(R.string.progressSave));
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("password", txtCurrentPassword.getText().toString());
        jsonObject.addProperty("newPassword", txtNewPassword.getText().toString());

        NetworkManager.PostRequest("api/user/password", jsonObject, RequestType.PUT, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                progressDialog.dismiss();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        lblErrorMessage.setText(getText(R.string.errorOccured));
                        lblErrorMessage.setVisibility(View.VISIBLE);

                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                progressDialog.dismiss();

                Gson gson = new GsonBuilder().create();
                JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);

                if (json.get("success").getAsBoolean()) {

                    finish();

                } else {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            lblErrorMessage.setText(getText(R.string.incorrectPassword));
                            lblErrorMessage.setVisibility(View.VISIBLE);

                        }
                    });

                }

            }

        });

    }

    //endregion Private methods
}

