package ovh.shr.sportsfun.sportsfunapplication.activity.User;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.SportsFunApplication;
import ovh.shr.sportsfun.sportsfunapplication.models.User;
import ovh.shr.sportsfun.sportsfunapplication.network.NetworkManager;
import ovh.shr.sportsfun.sportsfunapplication.network.RequestType;
import ovh.shr.sportsfun.sportsfunapplication.utilities.DateHelper;
import ovh.shr.sportsfun.sportsfunapplication.utilities.Utils;

public class EditUserDataActivty extends AppCompatActivity implements  DatePickerDialog.OnDateSetListener {


    //region Declarations

    @BindView(R.id.lblErrorMessage) TextView lblErrorMessage;

    @BindView(R.id.txtFirstname) EditText txtFirstname;
    @BindView(R.id.txtLastname) EditText txtLastname;
    @BindView(R.id.txtEmail) EditText txtEmail;
    @BindView(R.id.dtBirthdate) EditText dtBirthdate;

    @BindView(R.id.my_toolbar) Toolbar toolbar;
    private ActionBar actionBar;
    private long birthdate;
    DatePickerDialog datePickerDialog;
    private ProgressDialog progressDialog;

    //endregion Declarations

    //region Constructor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_data_activty);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        User currentUser = SportsFunApplication.getCurrentUser();

        this.txtFirstname.setText(currentUser.getFirstname());
        this.txtLastname.setText(currentUser.getLastname());
        this.txtEmail.setText(currentUser.getEmail());
        Date dt = DateHelper.fromISO8601UTC(currentUser.getBirthdate());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        dtBirthdate.setText(String.format("%d / %d / %d", day, month + 1, year));


        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                dtBirthdate.setText(String.format("%d / %d / %d", day, month + 1, year));

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                birthdate = calendar.getTimeInMillis();

            }
        }, year, month, day);

    }


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, EditUserDataActivty.class);
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

    @OnFocusChange(R.id.dtBirthdate)
    protected void OnBirthDateFocusChange(boolean b) {
        if (b)
            datePickerDialog.show();
    }

    @OnClick(R.id.btnSaveChanges)
    protected void OnBtnSaveChangesClicked() {
        if (CheckFields())
            CommitChanges();
    }

    //endregion Fields

    //region Private methods


    private boolean CheckFields() {

        if (this.txtLastname.getText().toString().length() == 0 || this.txtLastname.getText().toString().length() == 0 ) {
            DisplayError(getText(R.string.lblErrorMessageMissingFields));
            return false;
        }
        else if (!Utils.isValidEmail(this.txtEmail.getText())) {
            DisplayError(getText(R.string.invalidEmail));
            return false;
        }
        else {
            return true;
        }
    }

    private void CommitChanges() {

        progressDialog = new ProgressDialog(EditUserDataActivty.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getApplicationContext().getString(R.string.progressSave));
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("firstName", this.txtFirstname.getText().toString());
        jsonObject.addProperty("lastName", this.txtLastname.getText().toString());

        if (SportsFunApplication.getCurrentUser().getEmail().equals(this.txtEmail.getText().toString()) == false)
            jsonObject.addProperty("email", this.txtEmail.getText().toString());

        jsonObject.addProperty("birthDate", birthdate);

        NetworkManager.PostRequest("api/user", jsonObject, RequestType.PUT, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                progressDialog.dismiss();
                e.printStackTrace();
                DisplayError(getText(R.string.errorOccured));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                progressDialog.dismiss();

                Gson gson = new GsonBuilder().create();
                JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);

                System.out.println(json.toString());

                if (json.get("success").getAsBoolean()) {

                    SportsFunApplication.getCurrentUser().setFirstname(txtFirstname.getText().toString());
                    SportsFunApplication.getCurrentUser().setLastname(txtLastname.getText().toString());
                    SportsFunApplication.getCurrentUser().setEmail(txtEmail.getText().toString());

                    finish();

                } else {

                    String message = json.get("message").getAsJsonObject().toString();
                    if (message.contains("duplicate") && message.contains(txtEmail.getText().toString())) {
                        DisplayError(getText(R.string.duplicateEmail));
                    }
                    else
                    {
                        DisplayError(getText(R.string.errorOccured));
                    }

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

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

    }



}
