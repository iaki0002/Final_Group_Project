package com.example.finalgroupproject.car;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.finalgroupproject.R;

/**
 * @author - Alex Hamilton
 *
 * This class is used to represent the activity that is displayed on a phone to replace the Fragment
 * that would displayed if the user were using a tablet.
 */
public class CarChargerPhoneFragActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_charger_phone_frag);

        Bundle dataToBePassed = getIntent().getExtras();
        CarChargerFragDetails fragDetails = new CarChargerFragDetails();
        fragDetails.setArguments(dataToBePassed);
        fragDetails.setIsTablet(false);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.eccsf_fragment_location, fragDetails)
                .commit();  // this loads the fragment
    }
}
