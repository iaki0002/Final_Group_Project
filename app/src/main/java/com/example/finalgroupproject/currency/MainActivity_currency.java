package com.example.finalgroupproject.currency;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalgroupproject.R;
import com.example.finalgroupproject.car.CarChargerFinderActivity;
import com.example.finalgroupproject.main.MainActivity;
import com.example.finalgroupproject.news.NewsMainActivity;
import com.example.finalgroupproject.recipe.MainRecipeActivity;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MainActivity_currency extends AppCompatActivity {

    /**
     * Initializing variables
     */

    public Button convertButton;
    private ArrayList<CountryItem> mCountryList;
    private CountryAdapter mAdapter;
    private CountryAdapter mAdapter2;
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private TextView resultTextView;
    private Handler progressBarHandler = new Handler();
    private String baseCurrency;
    private String targetCurrency;
    private double rate;
    private String date;
    private DatePicker picker;
    private double amount;
    SharedPreferences prefs;
    EditText firstCurrency;
    Spinner firstSpinner;
    Spinner secondSpinner;
    SQLiteDatabase database;
    CurrencyDatabase databaseOpener;
    private Button favouriteButton;
    private boolean isConvertClicked = false;
    private double result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_currency);

        resultTextView = findViewById(R.id.resultTextView);

        firstCurrency = findViewById(R.id.firstnumber);

        progressBar = findViewById(R.id.currencyProgressBar);
        progressBar.setVisibility(View.INVISIBLE);


        picker = (DatePicker) findViewById(R.id.datePicker);
        this.prefs = getApplicationContext().getSharedPreferences("previousConversion", 0);

        databaseOpener = new CurrencyDatabase(this);
        database = databaseOpener.getWritableDatabase();


        convertButton = (Button) findViewById(R.id.convertbutton);
        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                convertfirst();
                date = picker.getYear() + "-" + (picker.getMonth() + 1) + "-" + picker.getDayOfMonth();
                new CountDownTimer(500, 500) {
                    public void onFinish() {
                        openDialog();
                    }

                    public void onTick(long millisUntilFinished) {
                    }
                }.start();
            }
        });


        /**
         * setting database for the favourite tab (incomplete)
         */
        favouriteButton = findViewById(R.id.savefavouritebutton);
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * storing data in the ContentValues
                 */
                if (isConvertClicked) {
                    ContentValues values = new ContentValues();
                    values.put(CurrencyDatabase.COLUMN_BASE_CURRENCY, baseCurrency);
                    values.put(CurrencyDatabase.COLUMN_TARGET_CURRENCY, targetCurrency);
                    values.put(CurrencyDatabase.COLUMN_DATE, date);
                    values.put(CurrencyDatabase.COLUMN_BASE_AMOUNT, amount);
                    values.put(CurrencyDatabase.COLUMN_BASE_RESULT, result);

                    /**
                     * passing results from the ContentValues into the database
                     */
                    long results = database.insert(CurrencyDatabase.TABLE_NAME, null, values);
                    /**
                     * creating a toast and a snackbar to let the user know about the outcome
                     */
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Saved to favourites succesfully!", Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .show();
                } else {
                    makeToast("Please do the conversion first!");
                }

            }
        });

        /**
         * initializing the spinner for choosing initial currency
         */
        initList();
        firstSpinner = findViewById(R.id.spinnerChooseCountry1);
        mAdapter = new CountryAdapter(this, mCountryList);
        firstSpinner.setAdapter(mAdapter);

        /**
         *letting user choose the currency, show toast with the selected currency once done
         */
        firstSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CountryItem clickedItem = (CountryItem) adapterView.getItemAtPosition(i);
                baseCurrency = clickedItem.getCountryName();
                Toast.makeText(MainActivity_currency.this, baseCurrency + " selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        /**
         * initializing the spinner for choosing target currency
         */
        initList();
        secondSpinner = findViewById(R.id.spinnerChooseCountry2);
        mAdapter2 = new CountryAdapter(this, mCountryList);
        secondSpinner.setAdapter(mAdapter2);

        /**
         *letting user choose the currency, show toast with the selected currency once done
         */
        secondSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CountryItem clickedItem = (CountryItem) adapterView.getItemAtPosition(i);
                targetCurrency = clickedItem.getCountryName();
                Toast.makeText(MainActivity_currency.this, targetCurrency + " selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        setPrevValue();
    }


    private void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * creating a  method to show previously chosen values after restarting the application
     */
    private void setPrevValue() {
        String prefBaseCurrency = prefs.getString("baseCurrency", "CAD");
        String prefTargetCurrency = prefs.getString("targetCurrency", "USD");
        String prefDate = prefs.getString("date", "2019-01-01");
        String prefAmount = prefs.getString("amount", "0");
        firstCurrency.setText(prefAmount);
        int position = 0;
        for (int i = 0; i < mCountryList.size(); i++) {
            if (mCountryList.get(i).getCountryName().equalsIgnoreCase(prefBaseCurrency)) {
                position = i;
            }
        }
        firstSpinner.setSelection(position);
        for (int i = 0; i < mCountryList.size(); i++) {
            if (mCountryList.get(i).getCountryName().equalsIgnoreCase(prefTargetCurrency)) {
                position = i;
            }
        }
        secondSpinner.setSelection(position);

        picker.updateDate(Integer.valueOf(prefDate.split("-")[0]), Integer.valueOf(prefDate.split("-")[1]) - 1, Integer.valueOf(prefDate.split("-")[2]));
    }


    /**
     * creating the list of available currencies with country flags
     */
    private void initList() {
        mCountryList = new ArrayList<>();
        mCountryList.add(new CountryItem("CAD", R.drawable.canada));
        mCountryList.add(new CountryItem("CNY", R.drawable.china));
        mCountryList.add(new CountryItem("GBP", R.drawable.england));
        mCountryList.add(new CountryItem("EUR", R.drawable.eu));
        mCountryList.add(new CountryItem("JPY", R.drawable.japan));
        mCountryList.add(new CountryItem("BGN", R.drawable.bulgaria));
        mCountryList.add(new CountryItem("CZK", R.drawable.czechia));
        mCountryList.add(new CountryItem("DKK", R.drawable.denmark));
        mCountryList.add(new CountryItem("HUF", R.drawable.hungary));
        mCountryList.add(new CountryItem("PLN", R.drawable.poland));
        mCountryList.add(new CountryItem("RON", R.drawable.romania));
        mCountryList.add(new CountryItem("SEK", R.drawable.sweden));
        mCountryList.add(new CountryItem("CHF", R.drawable.switzerland));
        mCountryList.add(new CountryItem("ISK", R.drawable.iceland));
        mCountryList.add(new CountryItem("NOK", R.drawable.norway));
        mCountryList.add(new CountryItem("HRK", R.drawable.croatia));
        mCountryList.add(new CountryItem("RUB", R.drawable.russia));
        mCountryList.add(new CountryItem("TRY", R.drawable.turkey));
        mCountryList.add(new CountryItem("AUD", R.drawable.australia));
        mCountryList.add(new CountryItem("BRL", R.drawable.brazil));
        mCountryList.add(new CountryItem("HKD", R.drawable.hongkong));
        mCountryList.add(new CountryItem("IDR", R.drawable.indonesia));
        mCountryList.add(new CountryItem("ILS", R.drawable.israel));
        mCountryList.add(new CountryItem("USD", R.drawable.states));
        mCountryList.add(new CountryItem("INR", R.drawable.india));
        mCountryList.add(new CountryItem("KRW", R.drawable.southkorea));
        mCountryList.add(new CountryItem("MXN", R.drawable.mexico));
        mCountryList.add(new CountryItem("MYR", R.drawable.malaysia));
        mCountryList.add(new CountryItem("NZD", R.drawable.newzealand));
        mCountryList.add(new CountryItem("PHP", R.drawable.philippines));
        mCountryList.add(new CountryItem("SGD", R.drawable.singapore));
        mCountryList.add(new CountryItem("THB", R.drawable.thailand));
        mCountryList.add(new CountryItem("ZAR", R.drawable.southafrica));

    }

    /**
     * function to convert currency
     */
    private void convertfirst() {
        firstCurrency.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (firstCurrency.getText().toString().isEmpty()) {
            return;
        }
        isConvertClicked = true;
        amount = Double.valueOf(firstCurrency.getText().toString());
        new CurrencyQuery().execute();
    }


    /**
     * function to reset TextView values to 0 (not used)
     */
    public void reset(View view) {
        Button button = (Button) findViewById(R.id.convertbutton);
        button.setEnabled(true);
        EditText firstText = (EditText) findViewById(R.id.firstnumber);
        EditText secondText = (EditText) findViewById(R.id.secondnumber);
        firstText.setText("");
        secondText.setText("");
    }

    /**
     *inflating the toolbar menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.currency_menu, menu);
        return true;
    }

    /**
     *creating items of the toolbar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_currency_about:
                new AlertDialog.Builder(this)
                        .setMessage(R.string.currency_about)
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, id) -> {
                        })
                        .show();
                return true;
            case R.id.mainpage:
                startActivity(new Intent(MainActivity_currency.this, MainActivity.class));
                return true;
            case R.id.menu_car:
                startActivity(new Intent(MainActivity_currency.this, CarChargerFinderActivity.class));
                return true;
            case R.id.menu_recipe:
                startActivity(new Intent(MainActivity_currency.this, MainRecipeActivity.class));
                return true;
            case R.id.menu_news:
                startActivity(new Intent(MainActivity_currency.this, NewsMainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * creating a dialog to show that the conversion was successful
     */
    public void openDialog() {
        ConvertDialog convertDialog = new ConvertDialog();
        convertDialog.show(getSupportFragmentManager(), "Dialog text here");
    }

    /**
     * main logic of the conversion. takes values from both spinners and the initial currency EditText and inputs them in a link. sends the link to the server to get the result
     */
    public class CurrencyQuery extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            String queryURL = "https://api.exchangeratesapi.io/" + date + "?base=" + baseCurrency + "&symbols=" + targetCurrency + "";
            try {
                JSONObject jo = new JSONObject(getJSON(queryURL, 20000));
                rate = jo.getJSONObject("rates").getDouble(targetCurrency);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            displayResult();
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
        }

        public String getJSON(String url, int timeout) {
            HttpURLConnection c = null;
            try {
                URL u = new URL(url);
                c = (HttpURLConnection) u.openConnection();
                c.setRequestMethod("GET");
                c.setRequestProperty("Content-length", "0");
                c.setUseCaches(false);
                c.setAllowUserInteraction(false);
                c.setConnectTimeout(timeout);
                c.setReadTimeout(timeout);
                c.connect();
                int status = c.getResponseCode();

                switch (status) {
                    case 200:
                    case 201:
                        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        br.close();
                        return sb.toString();
                }

            } catch (MalformedURLException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (c != null) {
                    try {
                        c.disconnect();
                    } catch (Exception ex) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            return null;
        }
    }

    /**
     * function to display result of the conversion
     */
    private void displayResult() {
        EditText convertFirst = (EditText) findViewById(R.id.firstnumber);

        EditText convertSecond = (EditText) findViewById(R.id.secondnumber);

        result = Double.valueOf(convertFirst.getText().toString()) * rate;
        convertSecond.setText(result + "");
        progressBar.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = this.prefs.edit();
        editor.putString("baseCurrency", baseCurrency);
        editor.putString("targetCurrency", targetCurrency);
        editor.putString("date", date);
        editor.putString("amount", amount + "");

        editor.commit();
    }
}