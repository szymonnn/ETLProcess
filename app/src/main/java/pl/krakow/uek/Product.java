package pl.krakow.uek;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by szymonNitecki on 26.10.16.
 */
public class Product extends RealmObject{
    @PrimaryKey
    @Required
    public String id;
    public String productType;
    public String productName;
    public String additionalDescription;
    public RealmList<Review> reviews = new RealmList<>();

    @Override
    public String toString() {
        String rev = "";
        for (Review review : reviews){
            rev = rev + review.toString();
        }
        return "productType: " + productType + "\n" +
                "productName: " + productName + "\n" +
                "additionalDescription: " + additionalDescription + "\n" +
                "reviews: " + rev + "\n";
    }
}
