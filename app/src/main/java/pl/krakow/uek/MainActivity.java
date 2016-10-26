package pl.krakow.uek;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> mAllHtmls = new ArrayList<>();

    private static final String BASE_URL = "http://ceneo.pl";

    private int mProductId;

    private ProgressDialog mProgressDialog;

    @BindView(R.id.edit_text)
    EditText mEditText;

    @BindView(R.id.t_button)
    Button mTButton;

    @BindView(R.id.l_button)
    Button mLButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Proszę czekać...");

    }


    @OnClick({R.id.e_button, R.id.t_button, R.id.l_button, R.id.etl_button})
    public void onClick (View view){
        int id = view.getId();
        Log.i("ONCLICK", "");
        try {
            mProductId = Integer.parseInt(mEditText.getText().toString());
            switch (id){
                case R.id.e_button:
                    mTButton.setEnabled(false);
                    mLButton.setEnabled(false);
                    processE();
                    break;
                case R.id.t_button:
                    break;
                case R.id.l_button:
                    break;
                case R.id.etl_button:
                    break;
            }
        } catch (Exception e){
            Toast.makeText(MainActivity.this, "Podana wartość musi być liczbą całkowitą", Toast.LENGTH_SHORT).show();
        }
    }

    private void processE (){
        mAllHtmls.clear();
        Toast.makeText(MainActivity.this, "Rozpoczęto proces E", Toast.LENGTH_SHORT).show();
        String url = String.format("%s/%d%s", BASE_URL, mProductId, "#tab=reviews");
        mProgressDialog.show();
        getAllReviews(url);
    }

    private void getAllReviews(String url) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("RESPONSE", response);
                        mAllHtmls.add(response);
                        Document doc = Jsoup.parse(response);
                        Element pagination = doc.getElementsByClass("pagination").get(0);
                        Elements li = pagination.getElementsByTag("li");
                        Element pageNext = li.select(".arrow-next").first();
                        if (pageNext != null) {
                            String link = pageNext.getElementsByTag("a").first().attr("href");
                            getAllReviews(BASE_URL + link);
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Pobrano " + mAllHtmls.size() + " plików", Toast.LENGTH_SHORT).show();
                            mTButton.setEnabled(true);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Coś poszło nie tak", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

    private void processT (){

    }
}
