package ovh.shr.sportsfun.sportsfunapplication.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Message {

    //region Declarations

    private String id;

    private String author;

    private String message;

    private Date creationDate;

    //endregion Declarations

    //region Constructor

    //endregion Constructor

    //region Public Methods

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

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

//endregion Public Methods

}
