package ovh.shr.sportsfun.sportsfunapplication.models;

import java.util.Date;

public class GameInfo {

    //region Declarations

    private String id;
    private String game;
    private String type;
    private Date date;
    private int score;
    private int timeSpent;

    //endregion Declarations

    //region Getters && Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(int timeSpent) {
        this.timeSpent = timeSpent;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

//endregion Getters && Setters

}
