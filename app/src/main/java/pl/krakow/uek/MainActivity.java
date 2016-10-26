package pl.krakow.uek;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> mAllHtmls = new ArrayList<>();

    private static final String BASE_URL = "http://ceneo.pl";

    private String mProductId;

    private ProgressDialog mProgressDialog;

    private Product mProduct;

    @BindView(R.id.edit_text)
    EditText mEditText;

    @BindView(R.id.t_button)
    Button mTButton;

    @BindView(R.id.l_button)
    Button mLButton;

    @BindView(R.id.log_textview)
    TextView mLogTextView;

    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Proszę czekać...");
        mLogTextView.setMovementMethod(new ScrollingMovementMethod());
        Realm.init(this);
        mRealm = Realm.getDefaultInstance();
    }


    @OnClick({R.id.e_button, R.id.t_button, R.id.l_button, R.id.etl_button})
    public void onClick(View view) {
        int id = view.getId();
        Log.i("ONCLICK", "");
        try {
            mProductId = mEditText.getText().toString();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Podana wartość musi być liczbą całkowitą", Toast.LENGTH_SHORT).show();
        }
        if (!mProductId.equals("0")) {
            switch (id) {
                case R.id.e_button:
                    mTButton.setEnabled(false);
                    mLButton.setEnabled(false);
                    processE(false);
                    break;
                case R.id.t_button:
                    processT(false);
                    break;
                case R.id.l_button:
                    processL();
                    break;
                case R.id.etl_button:
                    processE(true);
                    break;
            }
        }
    }

    private void processE(boolean automatic) {
        mAllHtmls.clear();
        mLogTextView.setText(String.format("%s\n%s", mLogTextView.getText(), "Rozpoczęto proces Extract"));
        String url = String.format("%s/%s%s", BASE_URL, mProductId, "#tab=reviews");
        mProgressDialog.show();
        getAllReviews(url, automatic);
    }

    private void getAllReviews(String url, final boolean automatic) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("RESPONSE", response);
                        try {
                            mAllHtmls.add(response);
                            Document doc = Jsoup.parse(response);
                            Element pagination = doc.getElementsByClass("pagination").get(0);
                            Elements li = pagination.getElementsByTag("li");
                            Element pageNext = li.select(".arrow-next").first();
                            if (pageNext != null) {
                                String link = pageNext.getElementsByTag("a").first().attr("href");
                                getAllReviews(BASE_URL + link, automatic);
                            } else {
                                mProgressDialog.dismiss();
                                mLogTextView.setText(String.format("%s\n%s", mLogTextView.getText(), "Pobrano " + mAllHtmls.size() + " plików"));
                                mLogTextView.setText(String.format("%s\n%s", mLogTextView.getText(), "Zakończono proces Extract\n"));
                                mTButton.setEnabled(true);
                                if (automatic) {
                                    processT(automatic);
                                }
                            }
                        } catch (IndexOutOfBoundsException e){
                            e.printStackTrace();
                            mProgressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Produkt o danym id nie istnieje", Toast.LENGTH_SHORT).show();
                            mLogTextView.setText(String.format("%s\n%s", mLogTextView.getText(), "Proces Extract został przerwany\n"));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Coś poszło nie tak", Toast.LENGTH_SHORT).show();
                mLogTextView.setText(String.format("%s\n%s", mLogTextView.getText(), "Proces Extract został przerwany\n"));
            }
        });
        queue.add(stringRequest);
    }

    private void processT(final boolean automatic) {
        mLogTextView.setText(String.format("%s\n%s", mLogTextView.getText(), "Rozpoczęto proces Transform"));
        mProgressDialog.show();
        new AsyncTask<String, String, String>(){

            @Override
            protected String doInBackground(String... strings) {
                mProduct = new Product();
                mProduct.id = mProductId;
                if (mAllHtmls.size() > 0) {
                    Document document = Jsoup.parse(mAllHtmls.get(0));
                    mProduct.productName = document.getElementsByTag("meta").select("[property=\"og:title\"]").get(0).attr("content");
                    mProduct.productType = document.getElementsByTag("meta").select("[property=\"og:type\"]").get(0).attr("content");
                    mProduct.additionalDescription = document.getElementsByTag("meta").select("[property=\"og:description\"]").get(0).attr("content");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLogTextView.setText(String.format("%s\n%s", mLogTextView.getText(), "Przetworzono dane o produkcie"));

                        }
                    });

                    final ArrayList<Review> reviews = new ArrayList<>();
                    for (int i = 0; i < mAllHtmls.size(); i++) {
                        Document doc = Jsoup.parse(mAllHtmls.get(i));
                        Elements allReviews = doc.getElementsByTag("li").select(".product-review");
                        for (Element rev : allReviews) {
                            Review review = new Review();

                            review.id = rev.getElementsByClass("vote-yes").first().attr("data-review-id");

                            // HANDLING ADVANTAGES
                            Elements pros = rev.getElementsByClass("pros-cell").first().getElementsByTag("li");
                            ArrayList<RealmString> advanteges = new ArrayList<>();
                            for (Element el : pros) {
                                advanteges.add(new RealmString(el.text()));
                            }
                            review.advantages.addAll(advanteges);

                            // HANDLING DRAWBACKS
                            Elements cons = rev.getElementsByClass("cons-cell").first().getElementsByTag("li");
                            ArrayList<RealmString> drawbacks = new ArrayList<>();
                            for (Element el : cons) {
                                drawbacks.add(new RealmString(el.text()));
                            }
                            review.drawbacks.addAll(drawbacks);

                            review.body = rev.getElementsByClass("product-review-body").first().text();

                            String starsString = rev.getElementsByClass("review-score-count").first().text();
                            String[] split = starsString.split("/");
                            try {
                                review.starsCount = Double.parseDouble(split[0].replace(",", "."));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                            review.author = rev.getElementsByClass("product-reviewer").first().text();

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String stringDate = rev.getElementsByTag("time").attr("datetime");
                            try {
                                review.reviewDate = sdf.parse(stringDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            try {
                                String recommended = rev.getElementsByClass("product-recommended").first().text();
                                review.recommend = recommended.equals("Polecam");
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                            review.thumbUpCount = Integer.parseInt(rev.getElementsByClass("vote-yes").first().getElementsByTag("span").first().text());
                            review.thumbDownCount = Integer.parseInt(rev.getElementsByClass("vote-no").first().getElementsByTag("span").first().text());

                            reviews.add(review);
                        }
                    }
                    mProduct.reviews.addAll(reviews);
                    Log.i("PRODUCT", mProduct.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLogTextView.setText(String.format("%s\n%s", mLogTextView.getText(), "Przetworzono dane o " + reviews.size() + " opiniach"));
                            mLogTextView.setText(String.format("%s\n%s", mLogTextView.getText(), "Zakończono proces Transform\n"));
                            mLButton.setEnabled(true);
                            mProgressDialog.dismiss();
                            if (automatic){
                                processL();
                            }
                        }
                    });
                }
                return "";
            }
        }.execute();
    }

    private void processL (){
        mLogTextView.setText(String.format("%s\n%s", mLogTextView.getText(), "Rozpoczęto proces Load"));
        mProgressDialog.show();
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(mProduct);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                mProgressDialog.dismiss();
                mLogTextView.setText(String.format("%s\n%s", mLogTextView.getText(), "Zakończono proces Load\n"));
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                startActivity(intent);
            }
        });
    }
}
