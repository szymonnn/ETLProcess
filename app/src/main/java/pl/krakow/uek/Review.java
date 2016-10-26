package pl.krakow.uek;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by szymonNitecki on 26.10.16.
 */
public class Review {
    public ArrayList<String> drawbacks = new ArrayList<>();
    public ArrayList<String> advantages = new ArrayList<>();
    public String body;
    int starsCount;
    public String author;
    public Date reviewDate;
    public boolean recommend;
    public int thumbUpCount;
    public int thumbDownCount;
}
