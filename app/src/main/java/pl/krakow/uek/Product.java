package pl.krakow.uek;

import java.util.ArrayList;

/**
 * Created by szymonNitecki on 26.10.16.
 */
public class Product {
    public String productType;
    public String make;
    public String model;
    public String additionalDescription;
    public ArrayList<Review> reviews = new ArrayList<>();
}
