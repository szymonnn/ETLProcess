package pl.krakow.uek.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import pl.krakow.uek.model.Product;
import pl.krakow.uek.R;

/**
 * Ekran wyswietlajacy liste wszystkich obiektow w bazie danych
 */
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
        mListView.setAdapter(new pl.krakow.uek.utils.ExpandableListAdapter(this, toList));
        mListView.setEmptyView(mEmptyView);


    }
}
