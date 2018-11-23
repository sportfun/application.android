package ovh.shr.sportsfun.sportsfunapplication.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.socket.emitter.Emitter;
import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.adapters.MessageListAdapter;
import ovh.shr.sportsfun.sportsfunapplication.models.Message;
import ovh.shr.sportsfun.sportsfunapplication.models.User;
import ovh.shr.sportsfun.sportsfunapplication.network.API;
import ovh.shr.sportsfun.sportsfunapplication.network.SocketIOHelper;
import ovh.shr.sportsfun.sportsfunapplication.utilities.DateHelper;
import ovh.shr.sportsfun.sportsfunapplication.utilities.NotificationHelper;
import ovh.shr.sportsfun.sportsfunapplication.utilities.SCallback;

public class MessageActivity extends AppCompatActivity {

    //region Declarations

    @BindView(R.id.layoutToolbar) Toolbar layoutToolbar;
    @BindView(R.id.lvMessages) ListView lvMessages;
    @BindView(R.id.txtMessage) EditText txtMessage;
    @BindView(R.id.btnSendMessage) ImageButton btnSendMessage;
    @BindView(R.id.refresher) SwipeRefreshLayout refresher;

    private ActionBar actionBar;

    private User Partner;
    private String partnerID;

    List<Message> dataList = new ArrayList<>();
    MessageListAdapter messageListAdapter;

    //endregion Declarations

    //region Getters

    public User getPartner() {
        return Partner;
    }

    public String getPartnerID() {
        return partnerID;
    }


    //endregion Getters

    //region Android Specific

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        ButterKnife.bind(this);
        setSupportActionBar(layoutToolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        String partnerName = bundle.getString("partnerName");
        partnerID = bundle.getString("partnerID");
        actionBar.setTitle(partnerName);

        this.messageListAdapter = new MessageListAdapter(this.getApplicationContext(), dataList);
        this.lvMessages.setAdapter(this.messageListAdapter);

        this.refresher.setOnRefreshListener(onRefreshListener);

        SocketIOHelper.messageActivity = this;

        SocketIOHelper.openChannel("conversation", onConversationReceived);
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", partnerID);
            SocketIOHelper.emit("conversation", jsonObject);

        } catch (Exception error) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SocketIOHelper.messageActivity = null;
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

    private void loadMore() {
        System.out.println("LOADING MOOORE");
        SocketIOHelper.openChannel("conversation", onConversationReceived2);

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", partnerID);
            jsonObject.put("last", DateHelper.toString(dataList.get(0).getCreationDate()));
            SocketIOHelper.emit("conversation", jsonObject);

        } catch (Exception error) {

        }

    }

    private void addMessage(JsonObject jsonObject) {

        Message newMessage = new Message();
        newMessage.setMessage(jsonObject.get("content").getAsString());
        newMessage.setAuthor(jsonObject.get("author").getAsJsonObject().get("_id").getAsString());
        newMessage.setCreationDate(DateHelper.fromISO8601UTC(jsonObject.get("createdAt").getAsString()));
        dataList.add(newMessage);

    }

    private void notifyDataList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageListAdapter.notifyDataSetChanged();
            }
        });
    }
    
    private Emitter.Listener onConversationReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject JSONObject = (JSONObject) args[0];
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject) jsonParser.parse(JSONObject.toString());

            for (JsonElement msgObj : jsonObject.get("messages").getAsJsonArray()) {
                addMessage(msgObj.getAsJsonObject());
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

            SocketIOHelper.closeChannel("conversation", onConversationReceived);


        }
    };

    private Emitter.Listener onConversationReceived2 = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject JSONObject = (JSONObject) args[0];
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject) jsonParser.parse(JSONObject.toString());
            System.out.println("LOOL");
            System.out.println(jsonObject);
            for (JsonElement msgObj : jsonObject.get("messages").getAsJsonArray()) {
                addMessage(msgObj.getAsJsonObject());
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

            SocketIOHelper.closeChannel("conversation", onConversationReceived2);


        }
    };

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadMore();
        }
    };

    private void SendMessage()
    {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("to", partnerID);
            jsonObject.put("content", txtMessage.getText());

            SocketIOHelper.emit("message", jsonObject);
            txtMessage.setText("");

        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    //endregion Private methods

    //region Public methods

    @OnClick(R.id.btnSendMessage)
    public void OnSendMessageClick()
    {
        SendMessage();
    }

    //endregion Public mehtods

    //region Events

    public SCallback onMessageReceived = new SCallback() {
        @Override
        public void onTaskCompleted(JsonObject result) {
            System.out.print("******* MessageActivity: onMessageReceived");
            addMessage(result);
            notifyDataList();
        }
    };

    //endregion Events

}
