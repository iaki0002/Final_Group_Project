package com.example.finalgroupproject.car;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalgroupproject.R;
import com.example.finalgroupproject.currency.MainActivity_currency;
import com.example.finalgroupproject.news.NewsMainActivity;
import com.example.finalgroupproject.recipe.MainRecipeActivity;
import com.google.android.material.snackbar.Snackbar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.xmlpull.v1.XmlPullParser.START_TAG;

/**
 * @author - Alex Hamilton
 *
 * This class represents the primary Activity that is used in the Electric Car Charger Station Finder app.
 */
public class CarChargerFinderActivity extends AppCompatActivity {

    /**
     * Instance field that holds the ChargingStation references that
     * are instantiated using information from the server's response.
     */
    List<ChargingStation> stationList = new ArrayList<>();
    /**
     * Instance field that binds the ChargingStations in stationList to the GUI ListView.
     */
    private BaseAdapter lvAdapter;
    /**
     * Instance field that allows the user to enter his/her latitude.
     */
    private EditText latitudeET;
    /**
     * Instance field that allows the user to enter his/her longitude.
     */
    private EditText longitudeET;
    /**
     * Instance field that displays the search's progress in the GUI.
     */
    private ProgressBar progressBar;
    /**
     * Constant that is used to transfer data between this Activity and CarChargerPhoneFragActivity.
     */
    private final int LV_ITEM_SELECTED_ACTIVITY = 99;
    /**
     * Database that is shared amongst the classes in this package.
     */
    static SQLiteDatabase db;
    /**
     * Constant that is used to transfer data between this Activity and CarChargerPhoneFragActivity.
     */
    final static int DELETED_FROM_FAVS = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_charger_finder);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar();
      
        // creates and opens the database
        CarChargerDatabaseOpenHelper dbOpener = new CarChargerDatabaseOpenHelper(CarChargerFinderActivity.this);
        db = dbOpener.getWritableDatabase();
        ListView lv = findViewById(R.id.ECCSFListView);
        lv.setAdapter(lvAdapter = new LVAdapter());

        findViewById(R.id.ECCSFLatitudeTV);
        findViewById(R.id.ECCSFLongitudeTV);

        SharedPreferences prefs = getSharedPreferences("StoredData", MODE_PRIVATE);
        StationQuery onCreateQuery = new StationQuery();
        onCreateQuery.execute();

        latitudeET = findViewById(R.id.ECCSFLatitudeET);
        latitudeET.setText(String.valueOf(prefs.getFloat("Latitude", 0)));

        longitudeET = findViewById(R.id.ECCSFLongitudeET);
        longitudeET.setText(String.valueOf(prefs.getFloat("Longitude", 0)));

        progressBar = findViewById(R.id.ECCSFProgressBar);

        Button findBtn = findViewById(R.id.ECCSFFindButton);
        findBtn.setOnClickListener(btn -> {
            Toast.makeText(this, R.string.searchingStr,
                    Toast.LENGTH_SHORT).show();

            float latitude = Float.parseFloat(latitudeET.getText().toString());
            float longitude = Float.parseFloat(longitudeET.getText().toString());

            if (latitude < 0 || latitude > 90) {
                // TODO: add error handling when server is fixed
            } else if (longitude < 0 || longitude > 180) {
                // TODO: add error handling when server is fixed
            } else {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putFloat("Latitude", latitude);
                editor.putFloat("Longitude", longitude);
                editor.apply();

                StationQuery findQuery = new StationQuery();
                findQuery.execute(latitude, longitude);
            }
        });

        boolean isTablet = findViewById(R.id.eccsf_fragment_location) != null;

        lv.setOnItemClickListener((parent, view, pos, id) -> {
            Bundle lvItemStationData = new Bundle();
            lvItemStationData.putString("Name", stationList.get(pos).getTitle());
            lvItemStationData.putString("Latitude", String.valueOf(stationList.get(pos).getLatitude()));
            lvItemStationData.putString("Longitude", String.valueOf(stationList.get(pos).getLongitude()));
            lvItemStationData.putString("Phone", stationList.get(pos).getPhoneNum());

            if (isTablet) {
                CarChargerFragDetails fragDetails = new CarChargerFragDetails();

                fragDetails.setArguments(lvItemStationData);
                fragDetails.setIsTablet(true);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.eccsf_fragment_location, fragDetails)
                        .commit();  // this loads the fragment
            } else {
                Intent goToPhoneFragActivity = new Intent(CarChargerFinderActivity.this, CarChargerPhoneFragActivity.class);
                goToPhoneFragActivity.putExtras(lvItemStationData);
                startActivityForResult(goToPhoneFragActivity, LV_ITEM_SELECTED_ACTIVITY);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.recipeMenuButton:
                new AlertDialog.Builder(this)
                        .setMessage(R.string.ECCSFAlertRSEStr)
                        .setPositiveButton(R.string.yesStr, (dialog, id) -> {
                            Intent goToRecipeActivity = new Intent(CarChargerFinderActivity.this, MainRecipeActivity.class);
                            startActivity(goToRecipeActivity);
                        })
                        .setNegativeButton(R.string.noStr, null)
                        .create()
                        .show();
                break;
            case R.id.newsAPIMenuButton:
                new AlertDialog.Builder(this)
                        .setMessage(R.string.ECCSFAlertNAPIStr)
                        .setPositiveButton(R.string.yesStr, (dialog, id) -> {
                            Intent goToNewsActivity = new Intent(CarChargerFinderActivity.this, NewsMainActivity.class);
                            startActivity(goToNewsActivity);
                        })
                        .setNegativeButton(R.string.noStr, null)
                        .create()
                        .show();
                break;
            case R.id.currencyMenuButton:
                new AlertDialog.Builder(this)
                        .setMessage(R.string.ECCSFAlertCCStr)
                        .setPositiveButton(R.string.yesStr, (dialog, id) -> {
                            Intent goToCurrencyActivity = new Intent(CarChargerFinderActivity.this, MainActivity_currency.class);
                            startActivity(goToCurrencyActivity);
                        })
                        .setNegativeButton(R.string.noStr, (dialog, id) -> closeOptionsMenu())
                        .create()
                        .show();
                break;
            case R.id.aboutOverFlowItem:
                new AlertDialog.Builder(this)
                        .setMessage(R.string.ECCSFAboutInfoStr)
                        .setPositiveButton("Ok", null)
                        .create()
                        .show();
                break;
            case R.id.favouritesOverFlowItem:
                Snackbar.make(latitudeET, R.string.loadFavsStr, 1_000).show();
                loadStationsFromDB();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.eccsf_menu_layout, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LV_ITEM_SELECTED_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                Snackbar.make(latitudeET, R.string.alreadyFavsStr, Snackbar.LENGTH_SHORT).show();
                loadStationsFromDB();
            } else if (resultCode == DELETED_FROM_FAVS) {
                Snackbar.make(latitudeET, R.string.delFavsStr, Snackbar.LENGTH_SHORT).show();
                loadStationsFromDB();
            }
        }
    }

    /**
     * Inner class that is used to bind the ArrayList of ChargingStation references to the ListView
     */
    private class LVAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return stationList.size();
        }

        @Override
        public Object getItem(int position) {
            return stationList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ChargingStation cs = stationList.get(position);
            View currentView = getLayoutInflater().inflate(R.layout.eccsf_listview_row_layout, null);
            TextView currentTV = currentView.findViewById(R.id.ECCSFlvNameTV);
            currentTV.append(" " + cs.getTitle());
            currentTV = currentView.findViewById(R.id.ECCSFlvLatitudeTV);
            currentTV.append(" " + cs.getLatitude());
            currentTV = currentView.findViewById(R.id.ECCSFlvLongitudeTV);
            currentTV.append(" " + cs.getLongitude());
            currentTV = currentView.findViewById(R.id.ECCSFlvPhoneTV);
            currentTV.append(" " + cs.getPhoneNum());

            return currentView;
        }

    }

    /**
     * This inner class is used to connect to the charging stations server using a background thread.
     *
     * @author Alex Hamilton
     * @since Nov. 12, 2019
     */
    private class StationQuery extends AsyncTask<Float, Integer, String> {

        StationQuery() {
            stationList.clear();
            lvAdapter.notifyDataSetChanged();
        }

        @Override
        protected String doInBackground(Float... coordinates) {
            String returnStrForDoInBackground = null;
            String urlStr /*= "https://api.openchargemap.io/v3/poi/?output=json&countrycode=CA&latitude="
                    + coordinates[0] + "&longitude=-" + coordinates[1] + "&maxresults=10"*/;

            // XML file link b/c JSON isn't working
            urlStr = "https://torunski.ca/FinalProjectCarCharging.xml";
            publishProgress(25);
            String stationName = "";
            double lat = -1;
            double lon = -1;

            try {
                // connect to server
                URL url = new URL(urlStr);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inStream = urlConnection.getInputStream();

                publishProgress(50);

                // create parser for the XML
                XmlPullParserFactory pullParserFactory = XmlPullParserFactory.newInstance();
                pullParserFactory.setNamespaceAware(false);
                XmlPullParser xpp = pullParserFactory.newPullParser();
                xpp.setInput(inStream, "UTF-8");

                int count = 1;
                int EVENT_TYPE;
                while ((EVENT_TYPE = xpp.getEventType()) != XmlPullParser.END_DOCUMENT) {
                    if (EVENT_TYPE == START_TAG) {
                        String tagName = xpp.getName();
                        switch (tagName) {
                            case "LocationTitle":
                                xpp.next();
                                stationName = xpp.getText();
                                break;
                            case "Latitude":
                                xpp.next();
                                lat = Double.parseDouble(xpp.getText());
                                break;
                            case "Longitude":
                                xpp.next();
                                lon = Double.parseDouble(xpp.getText());
                                break;
                            case "ContactTelephone1":
                                xpp.next();
                                String phone = xpp.getText();
                                stationList.add(new ChargingStation(stationName, lat, lon, phone));
                                if (++count == 1) {
                                    try {
                                        Thread.sleep(10);
                                    } catch (InterruptedException ie) {
                                        Log.e("THREAD ERROR", ie.getMessage() + " ");
                                    }
                                    publishProgress(75);
                                } else if (count == 10) {
                                    try {
                                        Thread.sleep(10);
                                    } catch (InterruptedException ie) {
                                        Log.e("THREAD ERROR", ie.getMessage() + " ");
                                    }
                                    publishProgress(100);
                                }
                        }
                    }
                    // move cursor to next XML element
                    xpp.next();
                }

                // read JSON from server
                // BufferedReader jsonReader = new BufferedReader(new InputStreamReader(inStream, StandardCharsets.UTF_8), 8);
                // StringBuilder sb = new StringBuilder(50_000);
                // String line;
                // while ((line = jsonReader.readLine()) != null) {
                //     if (line.matches("\\[") || line.matches("]"))
                //         continue;
                //
                //     sb.append(line).append('\n');
                // }
                // String jsonResultStr = sb.toString();
                //
                // publishProgress(75);
                //
                // JSONObject jsonObj = new JSONObject(jsonResultStr);
                // jsonObj = (JSONObject) jsonObj.get("AddressInfo");
                //
                // cs = new ChargingStation(jsonObj.get("Title").toString(),
                //         Double.parseDouble(jsonObj.get("Latitude").toString()),
                //         Double.parseDouble(jsonObj.get("Longitude").toString()),
                //         jsonObj.get("ContactTelephone1").toString());
                // stationList.add(cs);
                //
                // sb.delete(0, sb.length());

                // publishProgress(100);

                inStream.close();
                urlConnection.disconnect();
            } catch (MalformedURLException mue) {
                returnStrForDoInBackground = "Malformed URL Exception";
                Log.e("ERROR MESSAGE", returnStrForDoInBackground);
            } catch (IOException ioe) {
                returnStrForDoInBackground = "I/O Exception. Is wi-fi connected?";
                Log.e("ERROR MESSAGE", returnStrForDoInBackground);
                // } catch (JSONException je) {
                //     returnStrForDoInBackground = "JSON object exception. There is an issue with the JSON file";
                //     Log.e("ERROR MESSAGE", returnStrForDoInBackground);
            } catch (XmlPullParserException xppe) {
                returnStrForDoInBackground = "XML Pull Exception. The XML is not properly formatted.";
            }

            return returnStrForDoInBackground;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String strFromDoInBackground) {
            super.onPostExecute(strFromDoInBackground);
            lvAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.INVISIBLE);
        }

    }


    /**
     * This method is used to load the favourited the ChargerStations from the
     * database and display them in the ListView.
     */
    void loadStationsFromDB() {
        stationList.clear();
        // adds all database entries from the table STATIONS into stationList
        Cursor qResults = db.query(false, CarChargerDatabaseOpenHelper.TABLE_NAME,
                new String[]{CarChargerDatabaseOpenHelper.COL_ID, CarChargerDatabaseOpenHelper.COL_LOCATION_TITLE,
                        CarChargerDatabaseOpenHelper.COL_LATITUDE, CarChargerDatabaseOpenHelper.COL_LONGITUDE,
                        CarChargerDatabaseOpenHelper.COL_PHONE_NUM},
                null, null, null, null, null, null);
        while (qResults.moveToNext()) {
            stationList.add(new ChargingStation(qResults.getString(qResults.getColumnIndex(CarChargerDatabaseOpenHelper.COL_LOCATION_TITLE)),
                    qResults.getDouble(qResults.getColumnIndex(CarChargerDatabaseOpenHelper.COL_LATITUDE)),
                    qResults.getDouble(qResults.getColumnIndex(CarChargerDatabaseOpenHelper.COL_LONGITUDE)),
                    qResults.getString(qResults.getColumnIndex(CarChargerDatabaseOpenHelper.COL_PHONE_NUM))));
        }
        qResults.close();
        lvAdapter.notifyDataSetChanged();
    }

}
