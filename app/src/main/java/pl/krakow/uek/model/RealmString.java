package pl.krakow.uek.model;

import io.realm.RealmObject;

/**
 * Klasa reprezentująca tekst w Realmie. Stworzona, poneważ Realm nie może zapisać tablicy String
 */
public class RealmString extends RealmObject{
    private String mString;

    public RealmString (){

    }
    public RealmString(String text) {
        mString = text;
    }

    @Override
    public String toString() {
        return mString;
    }
}
