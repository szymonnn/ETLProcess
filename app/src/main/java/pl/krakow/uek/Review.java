package pl.krakow.uek;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * * Klasa reprezentujaca Opinie
 * @see io.realm.RealmObject
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

    /**
     * @return obiekt w formacie CSV
     */
    public String toCSV (){
        String drawbacksString = "";
        for (RealmString string : drawbacks){
            drawbacksString = drawbacksString + " " + string.toString();
        }

        String advantagesString = "";
        for (RealmString string : advantages){
            advantagesString = advantagesString + " " + string.toString();
        }
        String csv = id + "," + drawbacksString.replace(",", ";") + "," + advantagesString.replace(",", ";") + "," + body.replace(",", ";") + "," + starsCount + "," + author.replace(",", ";") + "," + reviewDate + "," + recommend + "," + thumbUpCount + "," + thumbDownCount + "\n";
        return csv;
    }

    /**
     * @return obiekt w formacie txt
     */
    public String toTxt() {
        String drawbacksString = "";
        for (RealmString string : drawbacks){
            drawbacksString = drawbacksString + "" + string.toString();
        }

        String advantagesString = "";
        for (RealmString string : advantages){
            advantagesString = advantagesString + " " + string.toString();
        }
       return "id opinii: " + id + "\n" +
               "wady: " + drawbacksString + "\n" +
               "zalety: " + advantagesString + "\n" +
               "treść: " + body + "\n" +
               "ocena: " + starsCount + "\n" +
               "autor: " + author + "\n" +
               "data opinii: " + reviewDate + "\n" +
               "polecany: " + recommend + "\n" +
               "+: " + thumbUpCount + "\n" +
               "-: " + thumbDownCount + "\n";
    }
}
