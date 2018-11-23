package ovh.shr.sportsfun.sportsfunapplication.activity.User;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.SportsFunApplication;
import ovh.shr.sportsfun.sportsfunapplication.models.User;
import ovh.shr.sportsfun.sportsfunapplication.network.NetworkManager;
import ovh.shr.sportsfun.sportsfunapplication.network.RequestType;

public class EditBioActivity extends AppCompatActivity {

    //region Declarations

    @BindView(R.id.lblErrorMessage)
    TextView lblErrorMessage;

    @BindView(R.id.txtBiographie) EditText txtBiographie;
    @BindView(R.id.numObjectif) EditText numObjectif;
    @BindView(R.id.layoutToolbar) Toolbar layoutToolbar;
    private ActionBar actionBar;

    private ProgressDialog progressDialog;

    //endregion Declarations

    //region Constructor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bio);
        ButterKnife.bind(this);

        setSupportActionBar(layoutToolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        User currentUser = SportsFunApplication.getCurrentUser();
        this.txtBiographie.setText(currentUser.getBio());
        this.numObjectif.setText("" + currentUser.getGoal());
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, EditBioActivity.class);
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

    //region Public methods

    @OnClick(R.id.btnSaveChanges)
    protected void SaveChanges() {
        if (CheckFields())
            CommitChanges();
    }

    //endregion Public methods

    //region Private methods

    private boolean CheckFields() {
        return true;
    }

    public void CommitChanges()
    {
        progressDialog = new ProgressDialog(EditBioActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getApplicationContext().getString(R.string.progressSave));
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("bio", txtBiographie.getText().toString());
        jsonObject.addProperty("goal", numObjectif.getText().toString());

        NetworkManager.PostRequest("api/user", jsonObject, RequestType.PUT, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                progressDialog.dismiss();
                DisplayError(getText(R.string.errorOccured));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                progressDialog.dismiss();

                Gson gson = new GsonBuilder().create();
                JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);

                if (json.get("success").getAsBoolean()) {
                    SportsFunApplication.getCurrentUser().setGoal(Integer.parseInt(numObjectif.getText().toString()));
                    SportsFunApplication.getCurrentUser().setBio(txtBiographie.getText().toString());
                    finish();
                } else {
                    DisplayError(getText(R.string.incorrectPassword));
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

    //endregion private methods
}
