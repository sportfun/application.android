package ovh.shr.sportsfun.sportsfunapplication.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.network.API;
import ovh.shr.sportsfun.sportsfunapplication.utilities.SCallback;
import ovh.shr.sportsfun.sportsfunapplication.utilities.Utils;

public class ResetPassword extends AppCompatActivity {


    //region Declarations

    @BindView(R.id.sportsfun) TextView sportsfun;

    @BindView(R.id.txtEmail) TextView txtEmail;
    @BindView(R.id.txtUsername) TextView txtUsername;

    @BindView(R.id.lblErrorMessage) TextView lblErrorMessage;
    private ProgressDialog progressDialog;

    //endregion Declarations

    //region Android Specific

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);

        String txtSports = Utils.getColoredSpanned("Sports", "#2c3e50");
        String txtFun = Utils.getColoredSpanned("Fun","#EA973E");
        sportsfun.setText(Html.fromHtml(txtSports + txtFun, 0));
    }

    //endregion Android Specific

    //region Events

    @OnClick(R.id.lblLinkReturn)
    public void lblLinkReturn_OnClick() {
        StartLoginActivity();
    }

    @OnClick(R.id.btnResetPassword)
    public void btnResetPassword_OnClick() {
        StartProgressDialog();

        API.ResetPassword(this.txtUsername.getText().toString(), this.txtEmail.getText().toString(), new SCallback() {
            @Override
            public void onTaskCompleted(JsonObject result) {
                StopProgressDialog();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog alertDialog = new AlertDialog.Builder(ResetPassword.this).create();
                        alertDialog.setTitle("");
                        alertDialog.setMessage("Si les informations indiquées sont correctes, votre mot de passe a été réinitialisé, veuillez vérifier vos emails.");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Fermer",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        StartLoginActivity();
                                    }
                                });
                        alertDialog.show();

                    }

                });
            }
        });
    }

    //endregion Events

    //region Private methods

    private void StartProgressDialog() {
        progressDialog = new ProgressDialog(ResetPassword.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getApplicationContext().getString(R.string.progressWaitPlease));
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

    private void StartLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivityForResult(intent, 1);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    //endregion Private methods

}
