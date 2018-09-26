package ovh.shr.sportsfun.sportsfunapplication.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.adapters.MessageListAdapter;
import ovh.shr.sportsfun.sportsfunapplication.models.Message;
import ovh.shr.sportsfun.sportsfunapplication.models.User;
import ovh.shr.sportsfun.sportsfunapplication.network.API;
import ovh.shr.sportsfun.sportsfunapplication.utilities.DateHelper;
import ovh.shr.sportsfun.sportsfunapplication.utilities.SCallback;

public class MessageActivity extends AppCompatActivity {

    //region Declarations

    @BindView(R.id.my_toolbar) Toolbar toolbar;
    @BindView(R.id.lvMessages) ListView lvMessages;
    @BindView(R.id.txtMessage) EditText txtMessage;
    @BindView(R.id.btnSendMessage) ImageButton btnSendMessage;
    private ActionBar actionBar;

    private User Partner;
    private String partnerID;

    List<Message> dataList = new ArrayList<>();
    MessageListAdapter messageListAdapter;

    //endregion Declarations

    //region Android Specific

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        String partnerName = bundle.getString("partnerName");
        partnerID = bundle.getString("partnerID");
        actionBar.setTitle(partnerName);

        this.messageListAdapter = new MessageListAdapter(this.getApplicationContext(), dataList);
        this.lvMessages.setAdapter(this.messageListAdapter);

        GetConversation();

    }

    //endregion Android Specific

    //region Menu

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    //endregion Menu

    //region Private methods

    private void GetConversation()
    {
        API.GetConversation(partnerID, new SCallback() {
            @Override
            public void onTaskCompleted(JsonObject result) {
                JsonArray data = result.get("data").getAsJsonArray();
                dataList.clear();

                for (JsonElement jsonElement : data) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    Message newMessage = new Message();
                    newMessage.setMessage(jsonObject.get("content").getAsString());
                    newMessage.setAuthor(jsonObject.get("author").getAsJsonObject().get("_id").getAsString());
                    newMessage.setCreationDate(DateHelper.fromISO8601UTC(jsonObject.get("createdAt").getAsString()));
                    dataList.add(newMessage);
                }

                dataList.sort(new Comparator<Message>() {
                    @Override
                    public int compare(Message message, Message t1) {
                        return message.getCreationDate().compareTo(t1.getCreationDate());
                    }
                });

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageListAdapter.notifyDataSetChanged();
                        lvMessages.setSelection(messageListAdapter.getCount() - 1);
                        txtMessage.setText("");

                    }
                });

            }
        });
    }

    private void SendMessage()
    {
        API.SendMessage(partnerID, txtMessage.getText().toString().trim(), new SCallback() {
            @Override
            public void onTaskCompleted(JsonObject result) {
                GetConversation();
            }
        });
    }

    //endregion Private methods

    //region Public methods

    @OnClick(R.id.btnSendMessage)
    public void OnSendMessageClick()
    {
        SendMessage();
    }

    //endregion Public mehtods

}
