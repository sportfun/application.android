package ovh.shr.sportsfun.sportsfunapplication.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import ovh.shr.sportsfun.sportsfunapplication.network.NetworkManager;
import ovh.shr.sportsfun.sportsfunapplication.utilities.Utils;
/**
 * Created by king_j on 14/03/2018.
 */

public class User {

    //region Declarations

    @SerializedName("_id")
    private String id;

    @SerializedName("username")
    private String username;

    @SerializedName("firstName")
    private String firstname;

    @SerializedName("lastName")
    private String lastname;

    @SerializedName("email")
    private String email;

    @SerializedName("birthDate")
    private String birthdate;

    @SerializedName("profilePic")
    private String profilePic;

    @SerializedName("coverPic")
    private String coverPic;

    @SerializedName("bio")
    private String bio;

    @SerializedName("goal")
    private int goal;

    @SerializedName("links")
    private List<String> links;

    private String profilPicUrl;

    //endregion Declarations

    //region Getters & Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getProfilePic() {
        if (profilePic.startsWith("http://") || profilePic.startsWith("https://"))
            return this.profilePic;
        else
            return NetworkManager.BASE_CDN + this.profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
        this.setProfilPicUrl(this.profilePic);
    }

    public String getCoverPic() {
        return coverPic;
    }

    public void setCoverPic(String coverPic) {
        this.coverPic = coverPic;
    }

    public String getFullName() { return this.firstname + " " + this.lastname; }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public String getProfilPicUrl()
    {
        if (profilPicUrl.startsWith("http://") || profilPicUrl.startsWith("https://"))
           return this.profilPicUrl;
        else
            return NetworkManager.BASE_CDN + profilPicUrl;
    }

    public void setProfilPicUrl(String profilPicUrl) {

        if (profilPicUrl.startsWith("http://") || profilPicUrl.startsWith("https://"))
            this.profilPicUrl = profilPicUrl;
        else
            this.profilPicUrl = NetworkManager.BASE_CDN + profilPicUrl;

    }

    public String getGravatarURL() {
        return "https://www.gravatar.com/avatar/" + Utils.MD5Hex(this.profilePic);
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    //endregion Getters & Setters
}


