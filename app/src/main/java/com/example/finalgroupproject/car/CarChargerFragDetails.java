package com.example.finalgroupproject.car;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.finalgroupproject.R;
import com.google.android.material.snackbar.Snackbar;

public class CarChargerFragDetails extends Fragment {

    private boolean isTablet;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle dataFromPrevActivity = getArguments();
        // inflates the layout for this fragment
        View result = inflater.inflate(R.layout.eccsf_fragment_detail_layout, container, false);

        TextView nameTV = result.findViewById(R.id.ECCSFNameFragTV);
        String name = dataFromPrevActivity.getString("Name");
        nameTV.setText(name);

        TextView latitudeTV = result.findViewById(R.id.ECCSFLatitudeFragTV);
        String latitude = dataFromPrevActivity.getString("Latitude");
        latitudeTV.setText(latitude);

        TextView longitudeTV = result.findViewById(R.id.ECCSFLongitudeFragTV);
        String longitude = dataFromPrevActivity.getString("Longitude");
        longitudeTV.setText(longitude);

        TextView phoneTV = result.findViewById(R.id.ECCSFPhoneFragTV);
        String phoneNum = dataFromPrevActivity.getString("Phone");
        phoneTV.setText(phoneNum);

        Button directionsBtn = result.findViewById(R.id.ECCSFDirectionsBtn);
        directionsBtn.setOnClickListener(dirBtn -> {
            Toast.makeText(getContext(), "Loading directions...", Toast.LENGTH_LONG).show();
            Uri googleMapsUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
            Intent googleMapsIntent = new Intent(Intent.ACTION_VIEW, googleMapsUri);
            googleMapsIntent.setPackage("com.google.android.apps.maps");
            startActivity(googleMapsIntent);
        });

        Button addToFavsBtn = result.findViewById(R.id.ECCSFFavouritesBtn);
        addToFavsBtn.setOnClickListener(favBtn -> {
            if (getDatabaseId(name, Double.parseDouble(latitude), Double.parseDouble(longitude), phoneNum) != -1) {
                Snackbar.make(result, "Already in Favourites", Snackbar.LENGTH_SHORT).show();
                Log.e("Is in database", getDatabaseId(name, Double.parseDouble(latitude),
                        Double.parseDouble(longitude), phoneNum) + " ");
                return;
            }

            // inserts new sent message into the database
            ContentValues row = new ContentValues();
            row.put(CarChargerDatabaseOpenHelper.COL_LOCATION_TITLE, name);
            row.put(CarChargerDatabaseOpenHelper.COL_LATITUDE, Double.parseDouble(latitude));
            row.put(CarChargerDatabaseOpenHelper.COL_LONGITUDE, Double.parseDouble(longitude));
            row.put(CarChargerDatabaseOpenHelper.COL_PHONE_NUM, phoneNum);
            long rowId = CarChargerFinderActivity.db.insert(CarChargerDatabaseOpenHelper.TABLE_NAME,
                    null, row);

            Snackbar.make(result, "Added to Favourites", Snackbar.LENGTH_SHORT).show();

            if (isTablet) {
                CarChargerFinderActivity parentActivity = (CarChargerFinderActivity) getActivity();
                parentActivity.loadStationsFromDB();
                // removes this fragment since its underlying message has been deleted from the database
                parentActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
            } else {    // user is using a phone
                CarChargerPhoneFragActivity parentActivity = (CarChargerPhoneFragActivity) getActivity();
                Intent backToCCFActivity = new Intent();
                parentActivity.setResult(Activity.RESULT_OK, backToCCFActivity);
                parentActivity.finish();
            }
        });

        Button deleteBtn = result.findViewById(R.id.ECCSFDeleteBtn);
        deleteBtn.setOnClickListener(delBtn -> {
            int dbId = getDatabaseId(name, Double.parseDouble(latitude), Double.parseDouble(longitude), phoneNum);
            if (dbId != -1) {
                int rowsAffected = CarChargerFinderActivity.db.delete(CarChargerDatabaseOpenHelper.TABLE_NAME,
                        CarChargerDatabaseOpenHelper.COL_ID + " = ?", new String[]{String.valueOf(dbId)});
                Snackbar.make(result, "Deleted from Favourites", Snackbar.LENGTH_SHORT).show();
                Log.e("Rows affected", rowsAffected + " ");
                if (isTablet) {
                    CarChargerFinderActivity parentActivity = (CarChargerFinderActivity) getActivity();
                    parentActivity.loadStationsFromDB();
                    // removes this fragment since its underlying message has been deleted from the database
                    parentActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
                }   else { // user is using a phone
                    CarChargerPhoneFragActivity parentActivity = (CarChargerPhoneFragActivity) getActivity();
                    Intent backToCCFActivity = new Intent();
                    parentActivity.setResult(Activity.RESULT_OK, backToCCFActivity);
                    parentActivity.finish();
                }
            } else {
                Snackbar.make(result, "Not in Favourites", Snackbar.LENGTH_SHORT).show();
            }
        });

        return result;
    }

    void setIsTablet(boolean isTablet) {
        this.isTablet = isTablet;
    }

    private int getDatabaseId(String name, double latitude, double longitude, String phoneNum) {
        Cursor qResults = CarChargerFinderActivity.db.query(false, CarChargerDatabaseOpenHelper.TABLE_NAME,
                new String[]{CarChargerDatabaseOpenHelper.COL_ID, CarChargerDatabaseOpenHelper.COL_LOCATION_TITLE,
                        CarChargerDatabaseOpenHelper.COL_LATITUDE, CarChargerDatabaseOpenHelper.COL_LONGITUDE,
                        CarChargerDatabaseOpenHelper.COL_PHONE_NUM},
                null, null, null, null, null, null);
        while (qResults.moveToNext()) {
            if (name.equals(qResults.getString(qResults.getColumnIndex(CarChargerDatabaseOpenHelper.COL_LOCATION_TITLE)))
                    && latitude == qResults.getDouble(qResults.getColumnIndex(CarChargerDatabaseOpenHelper.COL_LATITUDE))
                    && longitude == qResults.getDouble(qResults.getColumnIndex(CarChargerDatabaseOpenHelper.COL_LONGITUDE))
                    && phoneNum.equals(qResults.getString(qResults.getColumnIndex(CarChargerDatabaseOpenHelper.COL_PHONE_NUM)))) {
                return qResults.getInt(qResults.getColumnIndex(CarChargerDatabaseOpenHelper.COL_ID));
            }
        }
        return -1;
    }
}
























