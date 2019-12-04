package com.example.finalgroupproject.recipe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.finalgroupproject.R;
import com.example.finalgroupproject.car.CarChargerFinderActivity;
import com.example.finalgroupproject.currency.MainActivity_currency;
import com.example.finalgroupproject.main.MainActivity;
import com.example.finalgroupproject.news.NewsMainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;


public class MainRecipeActivity extends AppCompatActivity{
    /**
     * Main Page of the Recipe, user search by the food name. the
     * title from RECIPES_URL will be display on the ListView.
     * User select a title, then it will go to the RecipeDetailFragment
     */
    private static final String TAG = "MainRecipeActivity";
    public static final String TITLE = "TITLE";
    public static final String IMAGE_URL = "IMAGE_URL";
    public static final String URL = "URL";

    private ListView searchListView;
    private Button searchBtn;
    private ProgressBar progressBar;
    private ArrayList<Recipe> foodList = new ArrayList<>();
    private BaseAdapter myAdapter;
    private String previous;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_main);
        init();
    }

    private void init(){
        searchListView = findViewById(R.id.foodListView);
        searchBtn = findViewById(R.id.foodSearchButton);
        progressBar= findViewById(R.id.recipe_search_progressbar);
        myAdapter = new MyListAdapter();
//setAdapter
        searchListView.setAdapter(myAdapter);
        progressBar.setVisibility(View.INVISIBLE);
//Search a name
        searchBtn.setOnClickListener(k->{
            Log.d(TAG,"searchBtn.setOnClickListener");
            progressBar.setVisibility(View.VISIBLE);
//AsybcTask execute
            new FoodDataQuery().execute();
        });
//Select an item from ListView and call RecipeDetailFragment go into recipe_main_framelayout
        /**
         * @param list a ListView that was clicked
         * @param item View that was returned by getView()
         * @param position index of the item in the list
         * @param id database ID
         */
        //                                 (ListView, View, position, Long id)
        searchListView.setOnItemClickListener( (list, item, position, id) -> {

            Bundle dataToPass = new Bundle();
            dataToPass.putString(TITLE, foodList.get(position).getTitle() );
            dataToPass.putString(IMAGE_URL, foodList.get(position).getImageURL());
            dataToPass.putString(URL, foodList.get(position).getSourceURL());
                //create a DetailFragment and add into the FrameLayout
                RecipeDetailFragment dFragment = new RecipeDetailFragment();
                //pass it a bundle for information
                dFragment.setArguments( dataToPass );
                getSupportFragmentManager()
                        .beginTransaction()
                        //Add the fragment in FrameLayout, MainRecipeActivity
                        .add(R.id.recipe_main_framelayout, dFragment)
                        //use the back button undo the transaction
                        .addToBackStack("Back")
                        //actually load the fragment.
                        .commit();
        });
//--SharedPreferences---
        EditText searchEditText =findViewById(R.id.recipe_search_edittext);
        prefs = getSharedPreferences("preferences", Context.MODE_PRIVATE);

        String foodName = prefs.getString("ReserveName", "");
        searchEditText.setText(foodName);
    }

    //TOOLBAR, inflate the Menu resource
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recipe_menu, menu);
        return true;
    }
    //MENU item : responds to an Item being selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_recipe_about:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.recipe_dialog_title)
                        .setMessage(R.string.recipe_about)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //code if yes
                            }
                        })
                        //.setNegativeButton("No", null)
                        .show();
                return true;
            case R.id.mainpage:
                startActivity(new Intent(MainRecipeActivity.this, MainActivity.class));
                return true;
            case R.id.recipe_menu_favorite:
                startActivity(new Intent(MainRecipeActivity.this, RecipeFavoriteActivity.class));
                return true;
            case R.id.menu_car:
                startActivity(new Intent(MainRecipeActivity.this, CarChargerFinderActivity.class));
                return true;
            case R.id.menu_currency:
                startActivity(new Intent(MainRecipeActivity.this, MainActivity_currency.class));
                return true;
            case R.id.menu_news:
                startActivity(new Intent(MainRecipeActivity.this, NewsMainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//AsyncTask: query data from Recipes_URL
    private class FoodDataQuery extends AsyncTask<String, String, String> {
        //public final String KEY_RECIPES = "0d4070af456def229c3be03e20c755f7";
        //public final String RECIPES_URL = "https://www.food2fork.com/api/search?key=" + KEY_RECIPES + "&q=";
        public final String RECIPES_URL ="http://torunski.ca/FinalProjectChickenBreast.json";

        public FoodDataQuery(){
        }
//Type 1
        //to avoid the cause problems when synchronize between the GUI and background threads
        @Override
        protected String doInBackground(String ... strings) {
            Log.d(TAG,"doInBackground");
            try {//timeout = 3seconds
                JSONObject response = new JSONObject(getJSON(RECIPES_URL,3000));
                //get JSON Array object "recipes []"
                JSONArray objects = response.getJSONArray("recipes");
                for(int i = 0; i < objects.length(); i++)
                {
                    Log.d(TAG,"FOR LOOP I="+i);
                    JSONObject object = objects.getJSONObject(i);
                    String title= object.getString("title");
                    String imageUrl=object.getString("image_url");
                    String url=object.getString("source_url");
                    foodList.add(new Recipe(title,imageUrl,url));
                }

            } catch (Throwable e) {
                e.printStackTrace();
            }
            return "";
         }
        //reference: https://stackoverflow.com/questions/10500775/parse-json-from-httpurlconnection-object
        /*
         * connect to HTTP server with RECIPES_URL, timeout = 3seconds
         * @param url, RECIPES_URL
         * @param timeout, 3000ms
         */
        public String getJSON(String url, int timeout) {
            HttpURLConnection c = null;
            try {
                //connect to HTTP server with RECIPES_URL
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
                        //build the entire string from the inputstream
                        //json is UTF-8 by default
                        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line+"\n");
                        }
                        br.close();
                        return sb.toString();
                }

            } catch (MalformedURLException ex) {
                //Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                Log.i(TAG, ex.getLocalizedMessage());
            } catch (IOException ex) {
                Log.i(TAG, ex.getLocalizedMessage());
            } finally {
                if (c != null) {
                    try {
                        c.disconnect();
                    } catch (Exception ex) {
                        Log.i(TAG, ex.getLocalizedMessage());
                    }
                }
            }
            return null;
        }
//Type 3
        @Override
        protected void onPostExecute(String strings) {
            super.onPostExecute(strings);
            Log.d(TAG,"onPostExecute");
            updateView();
        }
    }

    private void updateView(){
        Log.d(TAG,"updateView");
        progressBar.setVisibility(View.INVISIBLE);
        myAdapter.notifyDataSetChanged();
    }
//SharedPreference - save
    public void onPause(){
        super.onPause();
        EditText searchEditText =findViewById(R.id.recipe_search_edittext);
        /*** save ***/
        SharedPreferences.Editor editor = prefs.edit();
        previous = searchEditText.getText().toString();
        editor.putString("ReserveName", previous);
        editor.apply();
    }

//Adapter - populate the ListView with data, fill the list
    private class MyListAdapter extends BaseAdapter {
        //1st call
        //return the size of the ArrayList
        public int getCount() {
            return foodList.size();
        }
         /*
         * returns an object (Recipe) at position
         * call be getView()
         * @param position, row position
         */
        public Recipe getItem(int position) {
            return foodList.get(position);
        }
        /*
         * returns database id of the item at row position
         * @param p, the item at position
         */
        public long getItemId(int p) {
            return p;
        }
        /* 2nd call
         * This specify how each row looks,
         * creates a View object to go in a row of the ListView
         * @param p, the item at position
         * @param recycled, a OLD view, recycled view, reuse memory of row that have scrolled of the screen
         */
        public View getView(int p, View recycled, ViewGroup parent) {
            View thisRow = recycled;

            //load XML layout file
            thisRow = getLayoutInflater().inflate(R.layout.recipe_row_layout, null);
            //look  in the layout loaded in a newView
            TextView titleText = thisRow.findViewById(R.id.recipe_listview_row_title);
            titleText.setText(getItem(p).getTitle());
            return thisRow;
        }
    }
}//MainRecipeActivity

