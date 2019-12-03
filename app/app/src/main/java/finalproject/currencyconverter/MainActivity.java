package finalproject.currencyconverter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public Button convertButton;
    private ArrayList<CountryItem> mCountryList;

    private CountryAdapter mAdapter;
    private CountryAdapter mAdapter2;

    private ProgressBar progressBar;
    private int progressStatus = 0;
    private TextView progressBarText;
    private Handler progressBarHandler = new Handler();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        convertButton = (Button) findViewById(R.id.convertbutton);
        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                convertfirst();
                showProgressBar();
                new CountDownTimer(500, 500) {
                    public void onFinish() {
                        hideProgressBar();
                        openDialog();
                    }
                    public void onTick(long millisUntilFinished) { }
                }.start();
            }
        });



        initList();
        Spinner spinnerCountries = findViewById(R.id.spinnerChooseCountry1);
        mAdapter = new CountryAdapter(this, mCountryList);
        spinnerCountries.setAdapter(mAdapter);

        spinnerCountries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CountryItem clickedItem = (CountryItem) adapterView.getItemAtPosition(i);
                String clickedCountryName = clickedItem.getCountryName();
                Toast.makeText(MainActivity.this, clickedCountryName + " selected", Toast.LENGTH_SHORT).show(); }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        initList();
        Spinner spinnerCountries2 = findViewById(R.id.spinnerChooseCountry2);
        mAdapter2 = new CountryAdapter(this, mCountryList);
        spinnerCountries2.setAdapter(mAdapter2);
        spinnerCountries2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CountryItem clickedItem = (CountryItem) adapterView.getItemAtPosition(i);
                String clickedCountryName = clickedItem.getCountryName();
                Toast.makeText(MainActivity.this, clickedCountryName + " selected", Toast.LENGTH_SHORT).show(); }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });


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
        EditText convertFirst = (EditText) findViewById(R.id.firstnumber);
        convertFirst.setInputType(InputType.TYPE_CLASS_NUMBER);
        EditText convertSecond = (EditText) findViewById(R.id.secondnumber);
        convertFirst.setInputType(InputType.TYPE_CLASS_NUMBER);

        double convertA = Integer.parseInt(convertFirst.getText().toString());
        double resultA = convertA * 2;
        convertSecond.setText(String.valueOf(resultA));
        showProgressBar();
    }

    private void convertsecond() {
        EditText convertFirst = (EditText) findViewById(R.id.firstnumber);
        convertFirst.setInputType(InputType.TYPE_CLASS_NUMBER);
        EditText convertSecond = (EditText) findViewById(R.id.secondnumber);
        convertFirst.setInputType(InputType.TYPE_CLASS_NUMBER);

        double convertB = Integer.parseInt(convertFirst.getText().toString());
        double resultB = convertB / 2;
        convertSecond.setText(String.valueOf(resultB));
    }

//    public void click (View view) {
//        convertfirst();
//        Button button = (Button) findViewById(R.id.convertbutton);
//        button.setEnabled(false);
//    }

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
                return true;
            case R.id.car:
                Toast.makeText(this, "Going to the car charger finder", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.recipe:
                Toast.makeText(this, "Going to the recipe search engine", Toast.LENGTH_SHORT).show();
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

    public void showProgressBar(){
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        progressBarText = (TextView) findViewById(R.id.progressBarText);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(progressStatus < 100) {
                    progressStatus += 50;
                    progressBarHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progressStatus);
                            progressBarText.setText(progressStatus + "/" + progressBar.getMax());
                        }
                    });
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        progressStatus = 0;
    }

    public void hideProgressBar(){
        progressStatus = 0;
        progressBar.setVisibility(View.INVISIBLE);
        progressBarText.setText("");
    }
}
