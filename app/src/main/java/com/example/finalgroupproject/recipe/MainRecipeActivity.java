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
       // searchEditText =findViewById(R.id.recipe_search_edittext);
        progressBar= findViewById(R.id.recipe_search_progressbar);
        myAdapter = new MyListAdapter();
        searchListView.setAdapter(myAdapter);
        progressBar.setVisibility(View.INVISIBLE);
//Search a name
        searchBtn.setOnClickListener(k->{
            Log.d(TAG,"searchBtn.setOnClickListener");
            progressBar.setVisibility(View.VISIBLE);
//go to AsybcTask
            new FoodDataQuery().execute();
        });
//Select an item from listView
        // and call RecipeDetailFragment go into recipe_main_framelayout
        searchListView.setOnItemClickListener( (list, item, position, id) -> {

            Bundle dataToPass = new Bundle();
            dataToPass.putString(TITLE, foodList.get(position).getTitle() );
            dataToPass.putString(IMAGE_URL, foodList.get(position).getImageURL());
            dataToPass.putString(URL, foodList.get(position).getSourceURL());

                RecipeDetailFragment dFragment = new RecipeDetailFragment(); //add a DetailFragment
                dFragment.setArguments( dataToPass ); //pass it a bundle for information
                getSupportFragmentManager()
                        .beginTransaction()
                        //Add the fragment in FrameLayout, MainRecipeActivity
                        .add(R.id.recipe_main_framelayout, dFragment)
                        .addToBackStack("Back") //make the back button undo the transaction
                        .commit(); //actually load the fragment.
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
    //MENU item
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




    private class FoodDataQuery extends AsyncTask<String, String, String> {
        //public final String KEY_RECIPES = "0d4070af456def229c3be03e20c755f7";
        //public final String RECIPES_URL = "https://www.food2fork.com/api/search?key=" + KEY_RECIPES + "&q=";
        public final String RECIPES_URL ="http://torunski.ca/FinalProjectChickenBreast.json";

        public FoodDataQuery(){
        }
        //Type 1
        @Override
        protected String doInBackground(String ... strings) {


            Log.d(TAG,"doInBackground");
            try {
                JSONObject response = new JSONObject(getJSON(RECIPES_URL,3000));
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


    //Adapter - list of the searched food
    private class MyListAdapter extends BaseAdapter {

        public int getCount() {
            return foodList.size();
        } //This function tells how many foodList to show

        public Recipe getItem(int position) {
            return foodList.get(position);
        }  //This returns the string at position p

        public long getItemId(int p) {
            return p;
        } //This returns the database id of the item at position p

        public View getView(int p, View recycled, ViewGroup parent) {
            View thisRow = recycled;

            //list of the titles
            thisRow = getLayoutInflater().inflate(R.layout.recipe_row_layout, null);

            TextView titleText = thisRow.findViewById(R.id.recipe_listview_row_title);
            titleText.setText(getItem(p).getTitle());
            return thisRow;
        }

    }
}//MainRecipeActivity

