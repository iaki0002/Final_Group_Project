package com.example.finalgroupproject.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.finalgroupproject.R;

import com.example.finalgroupproject.car.CarChargerFinderActivity;
import com.example.finalgroupproject.car.CarChargerFragDetails;
import com.example.finalgroupproject.currency.MainActivity_currency;
import com.example.finalgroupproject.news.NewsMainActivity;
import com.example.finalgroupproject.recipe.MainRecipeActivity;

public class MainActivity extends AppCompatActivity {
    Button carButton, cookButton, currencyButton, newsButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //--- Electric Car Charging Station finder ---
        carButton = findViewById(R.id.carButton);
        carButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CarChargerFinderActivity.class));
            }
        });
        //--- Recipe search engine ---
        cookButton = findViewById(R.id.cookButton);
        cookButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            startActivity(new Intent(MainActivity.this, MainRecipeActivity.class));
            }
        });
        //--- Foreign currency conversion api ---
        currencyButton = findViewById(R.id.currencyButton);
        currencyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MainActivity_currency.class));
            }
        });


        //--- News Api.org headlines api ---
        newsButton = findViewById(R.id.newsButton);
        newsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NewsMainActivity.class));
            }
        });

    }
}
