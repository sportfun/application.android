package ovh.shr.sportsfun.sportsfunapplication.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.models.User;

public class MessageActivity extends AppCompatActivity {

    //region Declarations

    @BindView(R.id.my_toolbar) Toolbar toolbar;
    @BindView(R.id.lvMessages) ListView lvMessages;
    @BindView(R.id.txtMessage) EditText txtMessage;
    @BindView(R.id.btnSendMessage) ImageButton btnSendMessage;

    private User Partner;

    //endregion Declarations

    //region Android Specific

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        ButterKnife.bind(this);

    }

    //endregion Android Specific

    //region Private methods

    private void LoadDatas()
    {

    }

    //endregion Private methods

}
