package ovh.shr.sportsfun.sportsfunapplication.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;
import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.models.News;
import ovh.shr.sportsfun.sportsfunapplication.network.NetworkManager;
import ovh.shr.sportsfun.sportsfunapplication.network.RequestType;
import ovh.shr.sportsfun.sportsfunapplication.utilities.DateHelper;
import ovh.shr.sportsfun.sportsfunapplication.utilities.Utils;

public class CommentAdapter extends BaseAdapter {

    //region Declaration

    private List<News.NewsEntity> dataList;
    private LayoutInflater inflater;
    private Activity currentActivity;
    private String ID;

    //endregion Declarations

    //region Constructor

    public CommentAdapter(Activity activity, List<News.NewsEntity> contents){
        this.dataList = contents;
        this.inflater = LayoutInflater.from(activity.getApplicationContext());
        this.currentActivity = activity;
    }


    //endregion Constructor

    //region Public methods


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    @Override
    public int getCount() {
        return this.dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final ViewHolder holder;

        if (view == null) {

            holder = new ViewHolder();
            view = inflater.inflate(R.layout.layout_comment_item, null);
            holder.avatar = (CircleImageView) view.findViewById(R.id.comment_icon);
            holder.content = (TextView) view.findViewById(R.id.comment_content);
//            holder.time = (TextView) view.findViewById(R.id.time);
            holder.nickname = (TextView) view.findViewById(R.id.comment_author);
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.nickname.setText(dataList.get(i).getFullName());
        holder.content.setText(dataList.get(i).getMessage());
        //holder.time.setText(DateHelper.toString(dataList.get(0).getCreationDate()));

        String gravatar = Utils.Gravatar(dataList.get(i).getProfilPicUrl(), 500);
        Picasso.with(currentActivity.getApplicationContext())
                .load(gravatar)
                .placeholder(R.drawable.baseline_account_circle_black_36)
                .error(R.drawable.baseline_account_circle_black_36)
                .into(holder.avatar);

        return view;
    }


    public void getDatas() {

        NetworkManager.PostRequest("api/post/comments/" + this.getID(), null, RequestType.GET, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {


                Gson gson = new GsonBuilder().create();
                JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);
                dataList.clear();

                JsonArray elements = json.get("data").getAsJsonArray();

                for (int index = 0; index < elements.size(); index++) {
                    JsonObject obj = elements.get(index).getAsJsonObject();
                    JsonObject author = obj.get("author").getAsJsonObject();

                    News.NewsEntity newsEntity = new News.NewsEntity();
                    newsEntity.setId(obj.get("_id").getAsString());
                    newsEntity.setCreationDate(DateHelper.fromISO8601UTC(obj.get("createdAt").getAsString()));
                    newsEntity.setMessage(obj.get("content").getAsString());
                    newsEntity.setFirstname(author.get("firstName").getAsString());
                    newsEntity.setLastname(author.get("lastName").getAsString());
                    newsEntity.setProfilPicUrl(author.get("profilePic").getAsString());

                    dataList.add(newsEntity);
                }

                currentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });

            }
        });


    }


    //endregion Public methods

    //region ViewHolder

    public final class ViewHolder {
        public CircleImageView avatar;
        public TextView content;
        public TextView time;
        public TextView nickname;
    }

    //endregion ViewHolder

}
