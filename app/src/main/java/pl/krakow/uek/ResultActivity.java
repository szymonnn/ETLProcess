package pl.krakow.uek;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class ResultActivity extends AppCompatActivity {

    @BindView(R.id.list_view)
    ListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Product> products = realm.where(Product.class).findAll();
        ArrayList<String> toList = new ArrayList<>();
        for (Product product : products){
            toList.add(product.toString());
        }
        mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, toList));

    }
}
