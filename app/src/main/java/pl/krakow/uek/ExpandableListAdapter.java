package pl.krakow.uek;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by szymon on 27.10.16.
 */
public class ExpandableListAdapter extends android.widget.BaseExpandableListAdapter {

    public ArrayList<Review> mReviews, mTempReviews;
    public ArrayList<Product> mProducts = new ArrayList<>();
    public LayoutInflater mInflater;
    public Context mContext;

    public ExpandableListAdapter(Context mContext, ArrayList<Product> mProducts) {
        this.mProducts = mProducts;
        this.mContext = mContext;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getGroupCount() {
        return mProducts.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return mProducts.get(i).reviews.size();
    }

    @Override
    public Object getGroup(int i) {
        return null;
    }

    @Override
    public Object getChild(int i, int i1) {
        return null;
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

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

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int i) {

    }

    @Override
    public void onGroupCollapsed(int i) {

    }

    @Override
    public long getCombinedChildId(long l, long l1) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long l) {
        return 0;
    }
}
