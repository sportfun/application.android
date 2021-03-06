package ovh.shr.sportsfun.sportsfunapplication.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;
import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.SportsFunApplication;
import ovh.shr.sportsfun.sportsfunapplication.activity.MessageActivity;
import ovh.shr.sportsfun.sportsfunapplication.models.Conversations;
import ovh.shr.sportsfun.sportsfunapplication.models.Message;
import ovh.shr.sportsfun.sportsfunapplication.models.User;
import ovh.shr.sportsfun.sportsfunapplication.network.NetworkManager;
import ovh.shr.sportsfun.sportsfunapplication.network.RequestType;
import ovh.shr.sportsfun.sportsfunapplication.utilities.DateHelper;
import ovh.shr.sportsfun.sportsfunapplication.utilities.Utils;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder>{

    //region Declarations

    public AdapterView.OnItemClickListener itemClickListener;
    private List<Conversations.ConversationEntity> dataList;
    private SwipeRefreshLayout refresher;
    private Activity currentActivity;


    //endregion Declarations

    //region Constructor

    public ConversationsAdapter() {
        this.dataList = new ArrayList<>();
    }

    //endregion Constructor

    //region Getters & Setters

    public List<Conversations.ConversationEntity> getDataList()
    {
        return this.dataList;
    }

    public SwipeRefreshLayout getRefresher() {
        return refresher;
    }

    public void setRefresher(SwipeRefreshLayout refresher) {
        this.refresher = refresher;
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    //endregion Getters & Setters

    //region Public Methods

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i){
        final int location = dataList.size() - i - 1;


        viewHolder.txtUsername.setText(dataList.get(location).getAuthor());
        viewHolder.txtContent.setText(dataList.get(location).getContent());
        viewHolder.txtTimestamp.setText(DateHelper.toString(dataList.get(location).getCreatedAt()));

        String gravatar = Utils.Gravatar(dataList.get(location).getProfilPic(), 200);
        System.out.println(gravatar);
        Picasso.with(getCurrentActivity().getApplicationContext())
                .load(gravatar)
                .placeholder(R.drawable.baseline_account_circle_black_36)
                .error(R.drawable.baseline_account_circle_black_36)
                .into(viewHolder.profilPic);


        viewHolder.setEntity(dataList.get(location));
        viewHolder.setActivity(this.currentActivity);
        viewHolder.setContext(this.currentActivity.getApplicationContext());

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.msg_conversation_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount(){
        return dataList.size();
    }

    public void reset() {
        dataList.clear();
    }

    private Boolean isExist(String id) {

        for (Conversations.ConversationEntity entity : dataList) {

            if (entity.getId().equals(id))
                return true;
        }
        return false;
    }

    public void addItem(JsonObject obj) {

        System.out.println(obj.toString());

        Conversations.ConversationEntity entity = new Conversations.ConversationEntity();

        entity.setId(obj.get("id").getAsString());
        obj = obj.get("message").getAsJsonObject();
        JsonObject author = obj.get("author").getAsJsonObject();
        JsonObject receiver = obj.get("to").getAsJsonObject();

        if (SportsFunApplication.getCurrentUser().getId().equals(author.get("_id").getAsString())) {
            entity.setAuthor(receiver.get("firstName").getAsString() + " " + receiver.get("lastName").getAsString());
            entity.setAuthorId(receiver.get("_id").getAsString());
            entity.setProfilPic(receiver.get("profilePic").getAsString());
        } else
        {
            entity.setAuthor(author.get("firstName").getAsString() + " " + author.get("lastName").getAsString());
            entity.setAuthorId(author.get("_id").getAsString());
            entity.setProfilPic(author.get("profilePic").getAsString());
        }

        entity.setId(obj.get("_id").getAsString());
        entity.setCreatedAt(DateHelper.fromISO8601UTC(obj.get("createdAt").getAsString()));
        entity.setContent(obj.get("content").getAsString());
        dataList.add(entity);
        dataList.sort(new Comparator<Conversations.ConversationEntity>() {
            @Override
            public int compare(Conversations.ConversationEntity message, Conversations.ConversationEntity t1) {
                return t1.getCreatedAt().compareTo(message.getCreatedAt());
            }
        });

        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });

    }

    public void refreshDatas()
    {
        refresher.setRefreshing(true);
        NetworkManager.PostRequest("api/message", null, RequestType.GET, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                currentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refresher.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String tmp = response.body().string();
                Gson gson = new GsonBuilder().create();
                JsonObject json = gson.fromJson(tmp, JsonObject.class);

                dataList.clear();
                JsonArray elements = json.get("data").getAsJsonArray();

                for (int index = 0; index < elements.size(); index++) {
                    JsonObject obj = elements.get(index).getAsJsonObject();

                    Conversations.ConversationEntity entity = new Conversations.ConversationEntity();

                    JsonObject author = obj.get("author").getAsJsonObject();
                    JsonObject receiver = obj.get("to").getAsJsonObject();

                    if (SportsFunApplication.getCurrentUser().getId().equals(author.get("_id").getAsString())) {
                        entity.setAuthor(receiver.get("firstName").getAsString() + " " + receiver.get("lastName").getAsString());
                        entity.setAuthorId(receiver.get("_id").getAsString());
                        entity.setProfilPic(receiver.get("profilePic").getAsString());
                    } else
                    {
                        entity.setAuthor(author.get("firstName").getAsString() + " " + author.get("lastName").getAsString());
                        entity.setAuthorId(author.get("_id").getAsString());
                        entity.setProfilPic(author.get("profilePic").getAsString());
                    }

                    entity.setId(obj.get("_id").getAsString());
                    entity.setCreatedAt(DateHelper.fromISO8601UTC(obj.get("createdAt").getAsString()));
                    entity.setContent(obj.get("content").getAsString());
                    dataList.add(entity);
                }

                currentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                        refresher.setRefreshing(false);
                    }
                });
            }

        });

    }



    //endregion Public Methods

    //region ViewHolder

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //region Declarations


        public Conversations.ConversationEntity entity;

        public TextView txtUsername;
        public TextView txtContent;
        public TextView txtTimestamp;
        private Activity activity;
        private Context context;
        public CircleImageView profilPic;

        //endregion Declarations

        //region Constructor

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.txtUsername = (TextView) itemView.findViewById(R.id.txtName);
            this.txtTimestamp = (TextView) itemView.findViewById(R.id.txtTimestamp);
            this.txtContent = (TextView) itemView.findViewById(R.id.txtMessage);
            this.profilPic = (CircleImageView) itemView.findViewById(R.id.profilePic);

        }

        //endregion Constructor

        //region Getters & Setters

        public Activity getActivity() {
            return activity;
        }

        public void setActivity(Activity activity) {
            this.activity = activity;
        }

        public Conversations.ConversationEntity getEntity() {
            return entity;
        }

        public void setEntity(Conversations.ConversationEntity entity) {
            this.entity = entity;
        }

        public Context getContext() {
            return context;
        }

        public void setContext(Context context) {
            this.context = context;
        }

        //endregion Getters & Setters

        //region Public methods

        @Override
        public void onClick(View view)
        {
            if (getContext() != null) {
                Intent newIntent = new Intent(getContext(), MessageActivity.class);

                newIntent.putExtra("partnerName", entity.getAuthor());
                newIntent.putExtra("partnerID", entity.getAuthorId());

                getContext().startActivity(newIntent);
            }

        }

        //endregion Public methods


    }


    //endregion ViewHolder

}
