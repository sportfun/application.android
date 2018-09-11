package ovh.shr.sportsfun.sportsfunapplication.models;


import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

public class Conversations {

    //region Declarations

    private List<ConversationEntity> newsEntityList;


    //endregion Declarations

    //region Getters

    public List<ConversationEntity> getNewsEntityList() {
        return newsEntityList;
    }

    //endregion Getters

    //region NewsEntity

    public static class ConversationEntity implements Parcelable {

        //region Declarations

        @SerializedName("_id")
        private String id;

        @SerializedName("to")
        private String to;

        private String toId;

        @SerializedName("author")
        private String author;

        private String authorId;

        @SerializedName("profilPic")
        private String profilPic;

        @SerializedName("content")
        private String content;

        @SerializedName("createdAt")
        private Date createdAt;



        //endregion Declarations

        //region Constructors

        public ConversationEntity() {

        }

        protected ConversationEntity(Parcel in) {
            this.id = in.readString();
            this.to = in.readString();
            this.author = in.readString();
            this.profilPic = in.readString();
            this.content = in.readString();
            this.createdAt = new Date(in.readLong());
        }

        //endregion Constructors

        //region Getters & Setters

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Date getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }

        public String getProfilPic() {
            return profilPic;
        }

        public void setProfilPic(String profilPic) {

            if (profilPic.startsWith("http://"))
                this.profilPic = profilPic;
            else
                this.profilPic = NetworkManager.BASE_CDN + profilPic;
        }

        public String getToId() {
            return toId;
        }

        public void setToId(String toId) {
            this.toId = toId;
        }

        public String getAuthorId() {
            return authorId;
        }

        public void setAuthorId(String authorId) {
            this.authorId = authorId;
        }

        //endregion Getters & Setters

        //region Public methods

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.id);
            parcel.writeString(this.to);
            parcel.writeString(this.author);
            parcel.writeString(this.profilPic);
            parcel.writeString(this.content);
            parcel.writeLong(this.createdAt.getTime());
        }


        @Override
        public int describeContents() {
            return 0;
        }


        //endregion Public methods

        public static final Creator<ConversationEntity> CREATOR = new Creator<ConversationEntity>() {
            public ConversationEntity createFromParcel(Parcel source) {
                return new ConversationEntity(source);
            }

            public ConversationEntity[] newArray(int size) {
                return new ConversationEntity[size];
            }
        };

    }

    //endregion NewsEntity

}
