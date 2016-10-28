package pl.krakow.uek;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class ResultActivity extends AppCompatActivity {

    @BindView(R.id.list_view)
    ExpandableListView mListView;

    @BindView(R.id.empty_view)
    TextView mEmptyView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Product> products = realm.where(Product.class).findAll();
        ArrayList<Product> toList = new ArrayList<>();
        for (Product product : products){
            toList.add(product);
        }
        mListView.setAdapter(new pl.krakow.uek.ExpandableListAdapter(this, toList));
        mListView.setEmptyView(mEmptyView);


    }
}
