package pl.krakow.uek;

import io.realm.RealmObject;

/**
 * Created by szymon on 26.10.16.
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
