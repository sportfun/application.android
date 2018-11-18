package ovh.shr.sportsfun.sportsfunapplication.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.utilities.Utils;

public class ResetPassword extends AppCompatActivity {


    //region Declarations

    @BindView(R.id.sportsfun) TextView sportsfun;

    //endregion Declarations

    //region Android Specific

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);

        String txtSports = Utils.getColoredSpanned("Sports", "#1A1A1A");
        String txtFun = Utils.getColoredSpanned("Fun","#EA973E");
        sportsfun.setText(Html.fromHtml(txtSports + txtFun, 0));
    }

    //endregion Android Specific

    //region Events

    @OnClick(R.id.lblLinkReturn)
    public void lblLinkReturn_OnClick() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivityForResult(intent, 1);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @OnClick(R.id.btnResetPassword)
    public void btnResetPassword_OnClick() {

    }

    //endregion Events

    //region Private methods



    //endregion Private methods

}
