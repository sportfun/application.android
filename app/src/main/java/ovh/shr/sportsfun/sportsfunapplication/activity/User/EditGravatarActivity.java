package ovh.shr.sportsfun.sportsfunapplication.activity.User;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.SportsFunApplication;
import ovh.shr.sportsfun.sportsfunapplication.models.User;
import ovh.shr.sportsfun.sportsfunapplication.network.API;
import ovh.shr.sportsfun.sportsfunapplication.network.NetworkManager;
import ovh.shr.sportsfun.sportsfunapplication.network.RequestType;
import ovh.shr.sportsfun.sportsfunapplication.utilities.DateHelper;
import ovh.shr.sportsfun.sportsfunapplication.utilities.SCallback;
import ovh.shr.sportsfun.sportsfunapplication.utilities.Utils;

public class EditGravatarActivity extends AppCompatActivity {


    //region Declarations

    @BindView(R.id.lblErrorMessage) TextView lblErrorMessage;

    @BindView(R.id.txtEmail) EditText txtEmail;
    @BindView(R.id.avatar) CircleImageView avatar;

    @BindView(R.id.layoutToolbar) Toolbar layoutToolbar;
    private ActionBar actionBar;

    private ProgressDialog progressDialog;

    //endregion Declarations

    //region Constructor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_gravatar);
        ButterKnife.bind(this);

        setSupportActionBar(layoutToolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        String gravatar = Utils.Gravatar(SportsFunApplication.getCurrentUser().getProfilePic(), 500);
        Picasso.with(getApplicationContext())
                .load(gravatar)
                .placeholder(R.drawable.baseline_account_circle_black_36)
                .error(R.drawable.baseline_account_circle_black_36)
                .into(avatar);
    }


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, EditGravatarActivity.class);
        context.startActivity(intent);
    }

    //endregion Constructor

    //region Toolbar

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    //endregion Toolbar

    //region Fields

    @OnClick(R.id.btnSaveChanges)
    protected void OnBtnSaveChangesClicked() {
        if (CheckFields())
            CommitChanges();
    }

    //endregion Fields

    //region Private methods


    private boolean CheckFields() {

        if (!Utils.isValidEmail(this.txtEmail.getText())) {
            DisplayError(getText(R.string.invalidEmail));
            return false;
        }
        else {
            return true;
        }
    }

    private void CommitChanges() {

        progressDialog = new ProgressDialog(EditGravatarActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getApplicationContext().getString(R.string.progressSave));
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();
        String url = "https://secure.gravatar.com/avatar/" + Utils.MD5Hex(this.txtEmail.getText().toString());
        jsonObject.addProperty("profilePic", url);

        API.UpdateUser(null, jsonObject, new SCallback() {
            @Override
            public void onTaskCompleted(JsonObject result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();

                    }
                });

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

                } else {

                    finish();
                }
            }
        });
    }

    private void DisplayError(final CharSequence message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lblErrorMessage.setText(message);
                lblErrorMessage.setVisibility(View.VISIBLE);

            }
        });
    }

    //endregion Private methods


}
