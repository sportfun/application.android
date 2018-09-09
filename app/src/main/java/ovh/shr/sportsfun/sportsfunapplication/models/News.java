package ovh.shr.sportsfun.sportsfunapplication.models;


import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import ovh.shr.sportsfun.sportsfunapplication.network.NetworkManager;
import ovh.shr.sportsfun.sportsfunapplication.network.RequestType;
import ovh.shr.sportsfun.sportsfunapplication.utilities.DateHelper;

public class News {

    //region Declarations

    private List<NewsEntity> newsEntityList;


    //endregion Declarations

    //region Getters

    public List<NewsEntity> getNewsEntityList() {
        return newsEntityList;
    }

    //endregion Getters

    //region NewsEntity

    public static class NewsEntity implements Parcelable {

        //region Declarations

        @SerializedName("_id")
        private String id;

        @SerializedName("firstName")
        private String firstname;

        @SerializedName("lastName")
        private String lastname;

        @SerializedName("content")
        private String message;

        @SerializedName("content")
        private Date creationDate;

        private String profilPicUrl;

        private int comments;

        private int likes;
        private List<String> likesIDs;

        //endregion Declarations

        //region Constructors

        public NewsEntity() {

        }

        protected NewsEntity(Parcel in) {
            this.id = in.readString();
            this.firstname = in.readString();
            this.lastname = in.readString();
            this.message = in.readString();
            this.creationDate = new Date(in.readLong());
            this.profilPicUrl = in.readString();
            this.comments = in.readInt();
            this.likes = in.readInt();
            this.likesIDs = new ArrayList<>();
            in.readList(likesIDs, null);

        }

        //endregion Constructors

        //region Getters & Setters

        public String getId() { return id; }

        public void setId(String id) { this.id = id; }

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public String getFullName() { return this.firstname + " " + this.lastname; }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Date getCreationDate() {
            return creationDate;
        }

        public void setCreationDate(Date creationDate) {
            this.creationDate = creationDate;
        }

        public int getLikes() {
            return likes;
        }

        public void setLikes(int likes) {
            this.likes = likes;
        }

        public List<String> getLikesIDs() {
            return likesIDs;
        }

        public void setLikesIDs(List<String> likesIDs) {
            this.likesIDs = likesIDs;
        }

        public void setComments(int comments) {
            this.comments = comments;
        }

        public int getComments() {
            return comments;
        }

        public String getProfilPicUrl() {
            return profilPicUrl;
        }

        public void setProfilPicUrl(String profilPicUrl) {

            if (profilPicUrl.startsWith("http://"))
                this.profilPicUrl = profilPicUrl;
            else
                this.profilPicUrl = NetworkManager.BASE_CDN + profilPicUrl;

        }

        //endregion Getters & Setters

        //region Public methods

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.id);
            parcel.writeString(this.firstname);
            parcel.writeString(this.lastname);
            parcel.writeString(this.message);
            parcel.writeLong(this.creationDate.getTime());
            parcel.writeString(this.profilPicUrl);
            parcel.writeInt(this.comments);
            parcel.writeInt(this.likes);
            parcel.writeList(this.likesIDs);
        }


        @Override
        public int describeContents() {
            return 0;
        }

        public void Refresh(final Handler.Callback callback) {

            NetworkManager.PostRequest("api/post/" + getId(), null, RequestType.GET, new okhttp3.Callback() {

                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    Gson gson = new GsonBuilder().create();
                    JsonObject json = gson.fromJson(response.body().string(), JsonObject.class).get("data").getAsJsonObject();

                    setLikes(json.get("likes").getAsJsonArray().size());

                    callback.handleMessage(null);
                }
            });

        }

        public void LikePost(final Handler.Callback callback) {
            NetworkManager.PostRequest("api/post/like/" + getId(), null, RequestType.PUT, new okhttp3.Callback() {

                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    Gson gson = new GsonBuilder().create();
                    JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);
                    String message = json.get("message").getAsString();
                    if (message.equals("OK"))
                    {
                        Refresh(callback);
                    }
                }
            });
        }

        public void getPost(String id) {

            NetworkManager.PostRequest("api/post/" + id, null, RequestType.GET, new okhttp3.Callback() {

                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {


                    Gson gson = new GsonBuilder().create();
                    JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);
                    JsonObject data = json.get("data").getAsJsonObject();

                    News.NewsEntity tmp = new News.NewsEntity();
                    tmp.setId(data.get("_id").getAsString());
                    tmp.setFirstname(data.get("author").getAsJsonObject().get("firstName").getAsString());
                    tmp.setLastname(data.get("author").getAsJsonObject().get("lastName").getAsString());

                    tmp.setMessage(data.get("content").getAsString());


                }
            });

        }

        //endregion Public methods

        public static final Parcelable.Creator<NewsEntity> CREATOR = new Parcelable.Creator<NewsEntity>() {
            public NewsEntity createFromParcel(Parcel source) {
                return new NewsEntity(source);
            }

            public NewsEntity[] newArray(int size) {
                return new NewsEntity[size];
            }
        };

    }

    //endregion NewsEntity

}
