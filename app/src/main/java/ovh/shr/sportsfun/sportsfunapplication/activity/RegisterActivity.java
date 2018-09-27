package ovh.shr.sportsfun.sportsfunapplication.activity;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.Format;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.network.NetworkManager;
import ovh.shr.sportsfun.sportsfunapplication.network.RequestType;

public class RegisterActivity extends AppCompatActivity implements  DatePickerDialog.OnDateSetListener {

    //region Declarations

    @BindView(R.id.lblLinkSignIn) TextView lblLinkSignIn;
    @BindView(R.id.btnSubmit) Button btnSubmit;

    @BindView(R.id.txtUsername) EditText txtUsername;
    @BindView(R.id.txtPassword) EditText txtPassword;
    @BindView(R.id.txtConfirmPassword) EditText txtConfirmPassword;
    @BindView(R.id.txtEmail) EditText txtEmail;
    @BindView(R.id.txtFirstname) EditText txtFirstname;
    @BindView(R.id.txtLastname) EditText txtLastname;

    private Long birthdate;
    @BindView(R.id.dtBirthdate) EditText dtBirthdate;

    @BindView(R.id.lblErrorMessage) TextView lblErrorMessage;

    DatePickerDialog datePickerDialog;

    private ProgressDialog progressDialog;

    //endregion Declarations

    //region Constructor
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        //region Birthdate

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                dtBirthdate.setText(String.format("%d / %d / %d", day, month + 1, year));

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                birthdate = calendar.getTimeInMillis();

            }
        }, year, month, day);

        dtBirthdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    datePickerDialog.show();
                }
            }
        });

        dtBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();

            }
        });

        //endregion Birthdate
    }

    //endregion Constructor


    //region DatePickerDialog Implementation


    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

    }

    //endregion DatePickerDialog Implementation

    @OnClick(R.id.btnSubmit)
    protected void OnBtnSubmitClicked() {
        if (CheckFields())
            Register();
    }

    @OnClick(R.id.lblLinkSignIn)
    protected void OnBtnLinkSignIn() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivityForResult(intent, 1);
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    private boolean CheckFields() {

        if (TextUtils.isEmpty(this.txtFirstname.getText())
                || TextUtils.isEmpty(this.txtLastname.getText())
                || TextUtils.isEmpty(this.txtEmail.getText())
                || TextUtils.isEmpty(this.txtPassword.getText())
                || TextUtils.isEmpty(this.txtConfirmPassword.getText())
                || TextUtils.isEmpty(this.dtBirthdate.getText())) {
            DisplayErrorMessage(getApplicationContext().getText(R.string.lblErrorMessageMissingFields));
            return false;
        }

        if (!this.txtPassword.getText().toString().equals(this.txtConfirmPassword.getText().toString())){
            DisplayErrorMessage(getApplicationContext().getText(R.string.passwordsDontMatch2));
            return false;
        }

       if (!android.util.Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText()).matches()) {
           DisplayErrorMessage(getApplicationContext().getText(R.string.invalidEmail));
            return false;
       }

       long test = new Date().getTime() - 31556926;

       if (birthdate >= new Date().getTime() - 31556926)
       {
           DisplayErrorMessage(getApplicationContext().getText(R.string.birhtdateIncorrect));
           return false;
       }

        return true;
    }


    private void DisplayErrorMessage(final CharSequence message) {
        if (progressDialog != null)
            progressDialog.dismiss();
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

    private void Register()
    {

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("username", txtUsername.getText().toString());
        jsonObject.addProperty("password", txtPassword.getText().toString());
        jsonObject.addProperty("email", txtEmail.getText().toString());
        jsonObject.addProperty("firstName", txtFirstname.getText().toString());
        jsonObject.addProperty("lastName", txtLastname.getText().toString());
        jsonObject.addProperty("birthDate", birthdate);


        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getApplicationContext().getString(R.string.progressSignUp));
        progressDialog.show();
        progressDialog.setCancelable(false);

        NetworkManager.PostRequest("api/user", jsonObject, RequestType.POST, new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivityForResult(intent, 1);
                    finish();
                    overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                } else {
                    try {

                        Gson gson = new GsonBuilder().create();
                        JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);
                        String message = json.get("message").getAsString();

                        if (message.startsWith("Missing key")) {
                            DisplayErrorMessage(getApplicationContext().getText(R.string.lblErrorMessageMissingFields));
                        }
                    } catch (Exception err) {
                        DisplayErrorMessage(getApplicationContext().getText(R.string.lblErrorMessageAlreadyExists));
                    }
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                DisplayErrorMessage(getApplicationContext().getText(R.string.connectionerror));
            }

        });

    }

}
