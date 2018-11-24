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
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.socket.emitter.Emitter;
import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.SportsFunApplication;
import ovh.shr.sportsfun.sportsfunapplication.adapters.MessageListAdapter;
import ovh.shr.sportsfun.sportsfunapplication.models.Message;
import ovh.shr.sportsfun.sportsfunapplication.models.User;
import ovh.shr.sportsfun.sportsfunapplication.network.API;
import ovh.shr.sportsfun.sportsfunapplication.network.SocketIOHelper;
import ovh.shr.sportsfun.sportsfunapplication.utilities.DateHelper;
import ovh.shr.sportsfun.sportsfunapplication.utilities.NotificationHelper;
import ovh.shr.sportsfun.sportsfunapplication.utilities.SCallback;
import ovh.shr.sportsfun.sportsfunapplication.utilities.Utils;

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

    //region Buttons

    @OnClick(R.id.btnSendMessage)
    public void OnSendMessageClick()
    {
       if (SocketIOHelper.isConnected()) {

           try {

               JSONObject jsonObject = new JSONObject();
               jsonObject.put("to", partnerID);
               jsonObject.put("content", txtMessage.getText());
               SocketIOHelper.emit("message", jsonObject);

               JsonObject messageAuthor = new JsonObject();
               messageAuthor.addProperty("_id", SportsFunApplication.getCurrentUser().getId());

               JsonObject message = new JsonObject();
               message.addProperty("content", txtMessage.getText().toString());
               message.add("author", messageAuthor);
               message.addProperty("createdAt", DateHelper.now());

               System.out.print(message.toString());

               insertMessage(message, true);

               txtMessage.setText("");

           } catch (Exception error) {
               Utils.ToastMessage(this, "Impossible d'envoyer le message, veuillez réessayer plus tard");
               error.printStackTrace();
           }

       } else {
           Utils.ToastMessage(this, "Impossible d'envoyer le message, veuillez réessayer plus tard");
       }
    }

    //endregion Buttons

    //region Events

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            System.out.println("******* MessageActivity: onRefreshListener");
            loadMore();
        }
    };


    private Emitter.Listener onConversationReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("******* MessageActivity: onConversationReceived");
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = (JsonObject) parser.parse(args[0].toString());

            for (JsonElement jsonElement : jsonObject.get("messages").getAsJsonArray()) {
                insertMessage(jsonElement.getAsJsonObject(), false);
            }

            sortDatas();

            SocketIOHelper.closeChannel("conversation", onConversationReceived);
        }
    };

    private Emitter.Listener onLoadMoreConversationReceveid = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("******* MessageActivity: onLoadMoreConversationReceveid");
            System.out.println(args[0]);
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = (JsonObject) parser.parse(args[0].toString());

            for (JsonElement jsonElement : jsonObject.get("messages").getAsJsonArray()) {
                insertMessage(jsonElement.getAsJsonObject(), false);
            }

            sortDatas();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refresher.setRefreshing(false);
                    messageListAdapter.notifyDataSetChanged();
                }
            });

            SocketIOHelper.closeChannel("conversation", onLoadMoreConversationReceveid);
        }
    };

    public SCallback onMessageReceived = new SCallback() {
        @Override
        public void onTaskCompleted(JsonObject result) {
            System.out.println("******* MessageActivity: onMessageReceived");
            System.out.println(result.toString());

            insertMessage(result, true);


        }
    };




    //endregion Events

    //region Private methods

    private void insertMessage(JsonObject jsonObject, boolean withRefresh) {

        Message newMessage = new Message();

        newMessage.setMessage(jsonObject.get("content").getAsString());
        newMessage.setAuthor(jsonObject.get("author").getAsJsonObject().get("_id").getAsString());
        newMessage.setCreationDate(DateHelper.fromISO8601UTC(jsonObject.get("createdAt").getAsString()));
        dataList.add(newMessage);

        if (withRefresh)
            notifyDataList(true);

    }

    private void sortDatas() {

        dataList.sort(new Comparator<Message>() {
            @Override
            public int compare(Message message, Message t1) {
                return message.getCreationDate().compareTo(t1.getCreationDate());
            }
        });

        notifyDataList(true);
    }

    private void notifyDataList(final Boolean goLast) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageListAdapter.notifyDataSetChanged();
                if (goLast)
                    lvMessages.setSelection(messageListAdapter.getCount() - 1);
            }
        });
    }

    private void loadMore() {
        SocketIOHelper.openChannel("conversation", onLoadMoreConversationReceveid);
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", partnerID);
            jsonObject.put("last", dataList.get(0).getCreationDate());
            SocketIOHelper.emit("conversation", jsonObject);

        } catch (Exception error) {

        }
    }
    //endregion Private methods

}
