package pl.krakow.uek;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Glowna klasa obslugujaca funkcje procesu ETL
 */
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

    private Menu mMenu;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Eksport CSV");
        menu.add("Eksport txt");
        menu.add("Wyczyść dane");
        menu.add("Zobacz rezultat");
        mMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String title = (String) item.getTitle();
        if (title.equals("Eksport CSV")){
            exportCSV();
        } else if (title.equals("Wyczyść dane")){
            File parent = new File(Environment.getExternalStorageDirectory(), "ETL");
            deleteDirectory(parent);
            mRealm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.where(Product.class).findAll().deleteAllFromRealm();
                    realm.where(Review.class).findAll().deleteAllFromRealm();
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    mLogTextView.setText(String.format("%s\n%s", mLogTextView.getText(), "Wyczyszczono dane oraz usunięto pliki\n"));
                }
            });
        } else if (title.equals("Zobacz rezultat")){
            Intent intent = new Intent(this, ResultActivity.class);
            startActivity(intent);
        } else if (title.equals("Eksport txt")){
            exportTxt();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * Wybiera z bazy danych informacje o wszystkich produktach oraz zapisuje je jako pliki txt w pamieci urzadzenia.
     */
    private void exportTxt() {
        RealmResults<Product> products = mRealm.where(Product.class).findAll();
        mLogTextView.setText(String.format("%s\n%s", mLogTextView.getText(), "Usuwanie poprzednich plików txt"));
        File parent = new File(Environment.getExternalStorageDirectory(), "ETL");
        parent.mkdir();
        File txt = new File(parent, "txt");
        deleteDirectory(txt);
        for (Product product : products){
            try{
                mLogTextView.setText(String.format("%s\n%s", mLogTextView.getText(), "Eksport opinii o produkcie z id: " + product.id));
                parent = new File(Environment.getExternalStorageDirectory(), "ETL");
                parent.mkdir();
                txt = new File(parent, "txt");
                txt.mkdir();
                File productDirectory = new File(txt, product.id);
                productDirectory.mkdir();
                File file = new File(productDirectory, "produkt.txt");
                FileOutputStream fOut = new FileOutputStream(file);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fOut);
                outputStreamWriter.write(product.toTxt());
                outputStreamWriter.close();

                for (Review review : product.reviews){
                    File reviewDirectory = new File(productDirectory, "opinie");
                    reviewDirectory.mkdir();
                    File reviewFile = new File(reviewDirectory, review.id + " " + review.author + review.reviewDate + ".txt");
                    FileOutputStream revfOut = new FileOutputStream(reviewFile);
                    OutputStreamWriter revoutputStreamWriter = new OutputStreamWriter(revfOut);
                    revoutputStreamWriter.write(review.toTxt());
                    revoutputStreamWriter.close();
                }
            }
            catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }
        mLogTextView.setText(String.format("%s\n%s", mLogTextView.getText(), "Zakończono eksport plików txt\n"));
        showOpenFolderDialog(parent);
    }

    /**
     * Usuwa dany plik rekursywnie
     * @param dir plik do usunięcia
     */
    private void deleteDirectory(File dir){
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                File f = new File(dir, children[i]);
                if (f.isDirectory()){
                    deleteDirectory(f);
                }
                f.delete();
            }
        }
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
                    processE(false);
                    break;
                case R.id.t_button:
                    mTButton.setEnabled(false);
                    mTButton.setTextColor(Color.parseColor("#cccccc"));
                    mTButton.setBackgroundResource(R.drawable.button_background_grey);
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
        mTButton.setEnabled(false);
        mTButton.setTextColor(Color.parseColor("#cccccc"));
        mTButton.setBackgroundResource(R.drawable.button_background_grey);
        mLButton.setEnabled(false);
        mLButton.setTextColor(Color.parseColor("#cccccc"));
        mLButton.setBackgroundResource(R.drawable.button_background_grey);
        mLogTextView.setText(String.format("%s\n%s", mLogTextView.getText(), "Rozpoczęto proces Extract"));
        String url = String.format("%s/%s%s", BASE_URL, mProductId, "#tab=reviews");
        mProgressDialog.show();
        getAllReviews(url, automatic);
    }

    /**
     * Metoda odpowiada za pobranie wszystkich plikow z opiniami na temat danego produktu
     * @param url adres url produktu
     * @param automatic wskazuje czy nastepny proces ma uruchomic się automatycznie
     */
    private void getAllReviews(String url, final boolean automatic) {
        mLogTextView.setText(String.format("%s\n%s", mLogTextView.getText(), "Pobieranie " + (mAllHtmls.size() + 1) + " pliku"));
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
                                mTButton.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
                                mTButton.setBackgroundResource(R.drawable.button_background);
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

    /**
     * Metoda odpowiada za przeksztalcenie plikow html do obiektow
     * @param automatic wskazuje czy nastepny proces ma uruchomic się automatycznie
     */
    private void processT(final boolean automatic) {
        mTButton.setEnabled(false);
        mTButton.setTextColor(Color.parseColor("#cccccc"));
        mTButton.setBackgroundResource(R.drawable.button_background_grey);
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
                            mLButton.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
                            mLButton.setBackgroundResource(R.drawable.button_background);
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

    /**
     * Metoda odpowiada za zapisanie obiektow do bazy danych
     */
    private void processL (){
        mLButton.setEnabled(false);
        mLButton.setTextColor(Color.parseColor("#cccccc"));
        mLButton.setBackgroundResource(R.drawable.button_background_grey);
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
            }
        });
    }

    /**
     * Wybiera z bazy danych informacje o wszystkich produktach oraz zapisuje je jako pliki csv w pamieci urzadzenia.
     */
    private void exportCSV (){
        RealmResults<Product> products = mRealm.where(Product.class).findAll();
        mLogTextView.setText(String.format("%s\n%s", mLogTextView.getText(), "Usuwanie poprzednich plików CSV"));
        File parent = new File(Environment.getExternalStorageDirectory(), "ETL");
        parent.mkdir();
        File csv = new File(parent, "csv");
        deleteDirectory(csv);
        for (Product product : products){
            try{
                mLogTextView.setText(String.format("%s\n%s", mLogTextView.getText(), "Eksport opinii o produkcie z id: " + product.id));
                parent = new File(Environment.getExternalStorageDirectory(), "ETL");
                parent.mkdir();
                csv = new File(parent, "csv");
                csv.mkdir();
                File file = new File(csv, product.id + ".csv");
                FileOutputStream fOut = new FileOutputStream(file);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fOut);
                outputStreamWriter.write(product.toCSV());
                outputStreamWriter.close();
            }
            catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }
        mLogTextView.setText(String.format("%s\n%s", mLogTextView.getText(), "Zakończono eksport plików CSV\n"));
        showOpenFolderDialog(parent);
    }

    /**
     * Metoda odpowiada za stworzenie okna dialogowego oraz otwarcie aplikacji obslugujacej przegladanie plikow
     * @param parent folder do otwarcia
     */
    private void showOpenFolderDialog(final File parent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Otwórz folder");
        builder.setMessage("Czy chcesz otworzyć folder z zapisanymi plikami?");
        builder.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri selectedUri = Uri.parse(parent.getPath());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(selectedUri, "resource/folder");

                if (intent.resolveActivityInfo(getPackageManager(), 0) != null)
                {
                    startActivity(intent);
                }
            }
        });
        builder.setNegativeButton("Nie", null);
        builder.create().show();
    }
}
