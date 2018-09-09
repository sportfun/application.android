package ovh.shr.sportsfun.sportsfunapplication.adapters;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;
import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.SportsFunApplication;
import ovh.shr.sportsfun.sportsfunapplication.activity.CommentActivity;
import ovh.shr.sportsfun.sportsfunapplication.models.News;
import ovh.shr.sportsfun.sportsfunapplication.models.User;
import ovh.shr.sportsfun.sportsfunapplication.network.NetworkManager;
import ovh.shr.sportsfun.sportsfunapplication.network.RequestType;
import ovh.shr.sportsfun.sportsfunapplication.utilities.DateHelper;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    //region Declarations

    public AdapterView.OnItemClickListener itemClickListener;
    private List<News.NewsEntity> dataList;
    private SwipeRefreshLayout refresher;
    private Activity currentActivity;

    //endregion Declarations

    //region Constructors

    public NewsAdapter(){

        this.dataList = new ArrayList<>();
    }

    public NewsAdapter(List<News.NewsEntity> data){
        dataList = data;
    }


    //endregion Constructors

    //region Getters & Setters

    public List<News.NewsEntity> getDataList()
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

    //region Public methods

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i){
        final int location = dataList.size() - i - 1;


        viewHolder.txtName.setText(dataList.get(location).getFullName());
        viewHolder.txtMessage.setText(dataList.get(location).getMessage());
        viewHolder.lblLikes.setText(String.format(currentActivity.getString(R.string.lblLikes), dataList.get(location).getLikes()));
        viewHolder.lblComments.setText(String.format(currentActivity.getString(R.string.lblComments), dataList.get(location).getComments()));
        viewHolder.txtTimestamp.setText(DateHelper.toString(dataList.get(location).getCreationDate()));
        viewHolder.setNewsEntity(dataList.get(location));
        viewHolder.setActivity(this.currentActivity);

        System.out.println(dataList.get(location).getProfilPicUrl());

        Picasso.with(getCurrentActivity().getApplicationContext())
                .load(dataList.get(location).getProfilPicUrl())
                .resize(200,200).
                centerCrop()
                .placeholder(R.drawable.baseline_account_circle_black_36)
                .error(R.drawable.baseline_account_circle_black_36)
                .into(viewHolder.profilPic);



        if (dataList.get(location).getLikesIDs().contains(SportsFunApplication.getCurrentUser().getId())) {

            viewHolder.btnLike.setPadding(80, 0, 0, 0);
            viewHolder.btnLike.setText(currentActivity.getText(R.string.post_unlike));

        } else {

            viewHolder.btnLike.setPadding(120, 0, 0, 0);
            viewHolder.btnLike.setText(currentActivity.getText(R.string.post_like));

        }

        //region Like Post

        viewHolder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (viewHolder.getNewsEntity().getLikesIDs().contains(SportsFunApplication.getCurrentUser().getId()))
                    viewHolder.getNewsEntity().getLikesIDs().remove(SportsFunApplication.getCurrentUser().getId());
                else
                    viewHolder.getNewsEntity().getLikesIDs().add(SportsFunApplication.getCurrentUser().getId());

                viewHolder.getNewsEntity().LikePost(new Handler.Callback() {

                    @Override
                    public boolean handleMessage(Message message) {
                        notifyItemChanged(i);

                        currentActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                                refresher.setRefreshing(false);
                            }
                        });

                        return false;
                    }

                });


                //viewHolder.LikePost();
            }
        });

        //endregion Like Post

        //region Comment Post

        viewHolder.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(dataList.get(location).getId());
                CommentActivity.actionStart(viewHolder.getActivity(), dataList.get(location).getId());
            }
        });

        //endregion Comment Post

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_item_layout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount(){
        return dataList.size();
    }

    public void refreshDatas()
    {
        refresher.setRefreshing(true);
        NetworkManager.PostRequest("api/post", null, RequestType.GET, new okhttp3.Callback() {

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

                Gson gson = new GsonBuilder().create();
                JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);

                dataList.clear();

                JsonArray elements = json.get("data").getAsJsonArray();

                for (int index = 0; index < elements.size(); index++) {
                    JsonObject obj = elements.get(index).getAsJsonObject();

                    News.NewsEntity newsEntity = new News.NewsEntity();
                    newsEntity.setId(obj.get("_id").getAsString());
                    newsEntity.setCreationDate(DateHelper.fromISO8601UTC(obj.get("createdAt").getAsString()));

                    newsEntity.setFirstname(obj.get("author").getAsJsonObject().get("firstName").getAsString());
                    newsEntity.setLastname(obj.get("author").getAsJsonObject().get("lastName").getAsString());
                    newsEntity.setProfilPicUrl(obj.get("author").getAsJsonObject().get("profilePic").getAsString());

                    newsEntity.setLikes(obj.get("likes").getAsJsonArray().size());
                    newsEntity.setComments(obj.get("comments").getAsJsonArray().size());


                    List<String> strings = new ArrayList<>();

                    for (JsonElement element : obj.get("likes").getAsJsonArray()) {
                        strings.add(element.getAsString());
                    }

                    newsEntity.setMessage(obj.get("content").getAsString());
                    newsEntity.setLikesIDs(strings);
                    dataList.add(newsEntity);
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



    //endregion Public methods

    //region ViewHolder

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //region Declarations


        public News.NewsEntity newsEntity;

        public TextView txtName;
        public TextView txtTimestamp;
        public TextView txtMessage;
        public TextView lblLikes;
        public TextView lblComments;
        public Button btnLike;
        public Button btnComment;

        public CircleImageView profilPic;

        private Activity activity;

        //endregion Declarations

        //region Constructor

        public ViewHolder(View itemView) {
            super(itemView);
            this.txtName = (TextView) itemView.findViewById(R.id.txtName);
            this.txtTimestamp = (TextView) itemView.findViewById(R.id.txtTimestamp);
            this.txtMessage = (TextView) itemView.findViewById(R.id.txtMessage);
            this.lblLikes = (TextView) itemView.findViewById(R.id.lblLikes);
            this.lblComments = (TextView) itemView.findViewById(R.id.lblComments);
            this.btnLike = (Button) itemView.findViewById(R.id.btnLike);
            this.btnComment = (Button) itemView.findViewById(R.id.btnComment);
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

        public News.NewsEntity getNewsEntity() {
            return newsEntity;
        }

        public void setNewsEntity(News.NewsEntity newsEntity) {
            this.newsEntity = newsEntity;
        }

        //endregion Getters & Setters

        //region Public methods

        @Override
        public void onClick(View view) {
            System.out.println("LOOOOOOL");
        }

        //endregion Public methods


    }


    //endregion ViewHolder


}
