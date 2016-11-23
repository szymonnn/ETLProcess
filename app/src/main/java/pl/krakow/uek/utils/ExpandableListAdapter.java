package pl.krakow.uek.utils;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import pl.krakow.uek.model.Product;
import pl.krakow.uek.R;
import pl.krakow.uek.model.RealmString;
import pl.krakow.uek.model.Review;

/**
 * Klasa odpowiadajaca za dostosowanie danych z bazy do listy wyswietlanej w ekranie "Zobacz rezultat"
 */
public class ExpandableListAdapter extends android.widget.BaseExpandableListAdapter {

    private ArrayList<Product> mProducts = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context mContext;

    /**
     * konstruktor
     * @param mContext kontekst aplikacji potrzebny do uzyskania jej zasobow
     * @param mProducts lista wszystkich produktów zapisanych w bazie danych
     */
    public ExpandableListAdapter(Context mContext, ArrayList<Product> mProducts) {
        this.mProducts = mProducts;
        this.mContext = mContext;
    }

    /**
     * Niewykorzystana metoda z @android.widget.BaseExpandableListAdapter
     * @param dataSetObserver
     */
    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    /**
     * Niewykorzystana metoda z @android.widget.BaseExpandableListAdapter
     * @param dataSetObserver
     */
    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    /**
     * @return ilosc produktow znajdujacych sie na liscie
     */
    @Override
    public int getGroupCount() {
        return mProducts.size();
    }

    /**
     *
     * @param i pozycja produktu na liscie
     * @return liczba wszystkich opinii dla danego produktu
     */
    @Override
    public int getChildrenCount(int i) {
        return mProducts.get(i).reviews.size();
    }


    /**
     * Niewykorzystana metoda z @android.widget.BaseExpandableListAdapter
     * @param i
     */
    @Override
    public Object getGroup(int i) {
        return null;
    }

    /**
     * Niewykorzystana metoda z @android.widget.BaseExpandableListAdapter
     * @param i
     * @param i1
     * @return
     */
    @Override
    public Object getChild(int i, int i1) {
        return null;
    }

    /**
     * Niewykorzystana metoda z @android.widget.BaseExpandableListAdapter
     * @param i
     * @return
     */
    @Override
    public long getGroupId(int i) {
        return 0;
    }

    /**
     * Niewykorzystana metoda z @android.widget.BaseExpandableListAdapter
     * @param i
     * @param i1
     * @return
     */
    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    /**
     * Niewykorzystana metoda z @android.widget.BaseExpandableListAdapter
     * @return
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * Metoda zwracajaca widok elementu produkt na liscie
     * @param groupPosition pozycja produktu na lisvie
     * @param isExpanded czy element listy jest rozwiniety
     * @param convertView widok przechowujący elementy listy
     * @param parent widok nadrzedny
     * @return widok z wypelnionymi danymi o produkcie
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.group_row, null);
        }

        Product product = mProducts.get(groupPosition);
        ((TextView)convertView.findViewById(R.id.product_name_text_view)).setText("Nazwa: " + product.productName);
        ((TextView)convertView.findViewById(R.id.product_type_text_view)).setText("Typ: " + product.productType);
        ((TextView)convertView.findViewById(R.id.product_description_text_view)).setText("Uwagi: " + product.additionalDescription);
        ((TextView)convertView.findViewById(R.id.reviews_count)).setText("Liczba opinii: " + product.reviews.size());
        return convertView;
    }

    /**
     * Metoda zwracajaca widok elementu opinia na liscie
     * @param i pozycja produktu na liscie produktow
     * @param i1 pozycja opinie na liscie opinii o produkcie
     * @param convertView widok przechowujący elementy listy
     * @param viewGroup widok nadrzedny
     * @return widok z wypelnionymi danymi o opinii
     */
    @Override
    public View getChildView(int i, int i1, boolean b, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.child_item, null);
        }
        Review review = mProducts.get(i).reviews.get(i1);
        ((TextView)convertView.findViewById(R.id.review_author)).setText("Autor: " + review.author);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        ((TextView)convertView.findViewById(R.id.review_date)).setText("Data opinii: " + sdf.format(review.reviewDate));
        ((TextView)convertView.findViewById(R.id.review_content)).setText("Treść: " + review.body);
        ((TextView)convertView.findViewById(R.id.review_star_count)).setText("Ocena: " + review.starsCount + "/5");
        ((TextView)convertView.findViewById(R.id.review_recommended)).setText("Polecony: " + review.recommend);
        ((TextView)convertView.findViewById(R.id.review_thumbs_up)).setText("" + review.thumbUpCount);
        ((TextView)convertView.findViewById(R.id.review_thumbs_down)).setText("" + review.thumbDownCount);
        String cons = "";
        for (RealmString con : review.drawbacks){
            cons = cons + con + "\n";
        }
        ((TextView)convertView.findViewById(R.id.review_cons)).setText("Wady: " +  cons);
        String pros = "";
        for (RealmString pro : review.advantages){
            pros = pros + pro + "\n";
        }
        ((TextView)convertView.findViewById(R.id.review_pros)).setText("Zalety: " + pros);
        return convertView;
    }

    /**
     * Niewykorzystana metoda z @android.widget.BaseExpandableListAdapter
     * @param i
     * @param i1
     * @return
     */
    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    /**
     * Niewykorzystana metoda z @android.widget.BaseExpandableListAdapter
     * @return
     */
    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    /**
     * Niewykorzystana metoda z @android.widget.BaseExpandableListAdapter
     * @return
     */
    @Override
    public boolean isEmpty() {
        return false;
    }

    /**
     * Niewykorzystana metoda z @android.widget.BaseExpandableListAdapter
     * @param i
     */
    @Override
    public void onGroupExpanded(int i) {

    }

    /**
     * Niewykorzystana metoda z @android.widget.BaseExpandableListAdapter
     * @param i
     */
    @Override
    public void onGroupCollapsed(int i) {

    }

    /**
     * Niewykorzystana metoda z @android.widget.BaseExpandableListAdapter
     * @param l
     * @param l1
     * @return
     */
    @Override
    public long getCombinedChildId(long l, long l1) {
        return 0;
    }

    /**
     * Niewykorzystana metoda z @android.widget.BaseExpandableListAdapter
     * @param l
     * @return
     */
    @Override
    public long getCombinedGroupId(long l) {
        return 0;
    }
}
