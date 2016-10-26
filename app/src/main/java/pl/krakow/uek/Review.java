package pl.krakow.uek;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by szymonNitecki on 26.10.16.
 */
public class Review extends RealmObject{
    @Required
    @PrimaryKey
    public String id;
    public RealmList<RealmString> drawbacks = new RealmList<>();
    public RealmList<RealmString> advantages = new RealmList<>();
    public String body;
    public double starsCount;
    public String author;
    public Date reviewDate;
    public boolean recommend;
    public int thumbUpCount;
    public int thumbDownCount;

    @Override
    public String toString() {
        return "Review{" +
                "\t" + "id=" + id + "\n" +
                "\t" +", drawbacks=" + drawbacks +"\n" +
                "\t" +", advantages=" + advantages +"\n" +
                "\t" +", body='" + body + '\'' +"\n" +
                "\t" +", starsCount=" + starsCount +"\n" +
                "\t" +", author='" + author + '\'' +"\n" +
                "\t" +", reviewDate=" + reviewDate +"\n" +
                "\t" +", recommend=" + recommend +"\n" +
                "\t" +", thumbUpCount=" + thumbUpCount +"\n" +
                "\t" +", thumbDownCount=" + thumbDownCount +"\n" +
                '}';
    }
}
