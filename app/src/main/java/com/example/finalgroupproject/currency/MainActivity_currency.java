package com.example.finalgroupproject.currency;
//package finalproject.currencyconverter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import com.example.finalgroupproject.R;
import com.example.finalgroupproject.car.CarChargerFinderActivity;
import com.example.finalgroupproject.main.MainActivity;
import com.example.finalgroupproject.recipe.MainRecipeActivity;

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

        // Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        resultTextView = findViewById(R.id.resultTextView);

        firstCurrency = findViewById(R.id.firstnumber);


        picker = (DatePicker) findViewById(R.id.datePicker);
        this.prefs = getApplicationContext().getSharedPreferences("previousConversion", 0);

        databaseOpener = new CurrencyDatabase(this);
        database = databaseOpener.getWritableDatabase();



        convertButton = (Button) findViewById(R.id.convertbutton);
        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        favouriteButton = findViewById(R.id.savefavouritebutton);
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConvertClicked) {
                    ContentValues values = new ContentValues();
                    values.put(CurrencyDatabase.COLUMN_BASE_CURRENCY, baseCurrency);
                    values.put(CurrencyDatabase.COLUMN_TARGET_CURRENCY, targetCurrency);
                    values.put(CurrencyDatabase.COLUMN_DATE, date);
                    values.put(CurrencyDatabase.COLUMN_BASE_AMOUNT, amount);
                    values.put(CurrencyDatabase.COLUMN_BASE_RESULT, result);

                    long results = database.insert(CurrencyDatabase.TABLE_NAME, null, values);
                    makeToast("Saved to favourites successfully");
                }else{
                    makeToast("Please do the conversion first!");
                }

            }
        });


        initList();
        firstSpinner = findViewById(R.id.spinnerChooseCountry1);
        mAdapter = new CountryAdapter(this, mCountryList);
        firstSpinner.setAdapter(mAdapter);

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
        initList();
        secondSpinner = findViewById(R.id.spinnerChooseCountry2);
        mAdapter2 = new CountryAdapter(this, mCountryList);
        secondSpinner.setAdapter(mAdapter2);
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

    private void makeToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void setPrevValue() {
        String prefBaseCurrency = prefs.getString("baseCurrency", "CAD");
        String prefTargetCurrency = prefs.getString("targetCurrency", "USD");
        String prefDate = prefs.getString("date", "2019-01-01");
        String prefAmount = prefs.getString("amount", "0");
        firstCurrency.setText(prefAmount);
        int position=0;
        for(int i=0;i<mCountryList.size();i++){
            if(mCountryList.get(i).getCountryName().equalsIgnoreCase(prefBaseCurrency)){
                position=i;
            }
        }
        firstSpinner.setSelection(position);
        for(int i=0;i<mCountryList.size();i++){
            if(mCountryList.get(i).getCountryName().equalsIgnoreCase(prefTargetCurrency)){
                position=i;
            }
        }
        secondSpinner.setSelection(position);

        picker.updateDate(Integer.valueOf(prefDate.split("-")[0]),Integer.valueOf(prefDate.split("-")[1])-1,Integer.valueOf(prefDate.split("-")[2]));

        //resultTextView.setText(prefAmount);


    }

    private void initList() {
        mCountryList = new ArrayList<>();
        mCountryList.add(new CountryItem("CAD", R.drawable.canada));
        mCountryList.add(new CountryItem("CNY", R.drawable.china));
        mCountryList.add(new CountryItem("GBP", R.drawable.england));
        mCountryList.add(new CountryItem("EUR", R.drawable.eu));
        mCountryList.add(new CountryItem("JPY", R.drawable.japan));
    }

    private void convertfirst() {

        // EditText convertFirst = (EditText) findViewById(R.id.firstnumber);
        firstCurrency.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (firstCurrency.getText().toString().isEmpty()) {
            return;
        }
        isConvertClicked=true;
        amount = Double.valueOf(firstCurrency.getText().toString());
        new CurrencyQuery().execute();
    }


    public void reset(View view) {
        Button button = (Button) findViewById(R.id.convertbutton);
        button.setEnabled(true);
        EditText firstText = (EditText) findViewById(R.id.firstnumber);
        EditText secondText = (EditText) findViewById(R.id.secondnumber);
        firstText.setText("");
        secondText.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mymenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mainpage:
                Toast.makeText(this, "Going to the main page", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity_currency.this, MainActivity.class));
                return true;
            case R.id.car:
                Toast.makeText(this, "Going to the car charger finder", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity_currency.this, CarChargerFinderActivity.class));
                return true;
            case R.id.recipe:
                Toast.makeText(this, "Going to the recipe search engine", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity_currency.this, MainRecipeActivity.class));
                return true;
            case R.id.news:
                Toast.makeText(this, "Going to the news headlines api", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.about:
                Toast.makeText(this, "Currency converter app, a part of the final project for CST_Blah_Blah Android Development. Author: Dmitrii Iakimchuk", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openDialog() {
        ConvertDialog convertDialog = new ConvertDialog();
        convertDialog.show(getSupportFragmentManager(), "Dialog text here");
    }


    public class CurrencyQuery extends AsyncTask<String, Integer, String> {

        @Override                       //Type 1
        protected String doInBackground(String... strings) {
            // progressBar.setVisibility(View.INVISIBLE);
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


        @Override                   //Type 3
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

    private void displayResult() {
        EditText convertFirst = (EditText) findViewById(R.id.firstnumber);

        EditText convertSecond = (EditText) findViewById(R.id.secondnumber);

        result = Double.valueOf(convertFirst.getText().toString()) * rate;
        convertSecond.setText(result + "");

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