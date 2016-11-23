package pl.krakow.uek.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Klasa reprezentujaca Produkty
 * @see io.realm.RealmObject
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

    /**
     * @return obiekt w formacie CSV
     */
    public String toCSV(){
        String csv = "# Product data\n";
        csv = csv + "# id produktu: " + id + "\n";
        csv = csv + "# nazwa produktu: " + productName + "\n";
        csv = csv + "# rodzaj: " + productType + "\n";
        csv = csv + "# dodatkowy opis: " + additionalDescription + "\n";
        csv = csv + "id opinii, wady, zalety, treść, ocena, autor, data opinii, polecany, +, -\n";
        for (Review review : reviews){
            csv = csv + review.toCSV();
        }
        return csv;
    }

    /**
     * @return obiekt w postaci txt
     */
    public String toTxt() {
        return "id produktu: " + id + "\n" +
                "nazwa produktu: " + productName + "\n" +
                "rodzaj: " + additionalDescription + "\n" +
                "dodatkowy opis: " + additionalDescription + "\n";
    }
}
