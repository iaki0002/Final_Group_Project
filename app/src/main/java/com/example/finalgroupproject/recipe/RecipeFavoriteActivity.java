package com.example.finalgroupproject.recipe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalgroupproject.R;
import com.example.finalgroupproject.currency.MainActivity_currency;
import com.example.finalgroupproject.main.MainActivity;

import java.util.ArrayList;

import static android.app.PendingIntent.getActivity;

public class RecipeFavoriteActivity extends AppCompatActivity {

    private ListView listView;
    private Recipe recipe;
    private Button removeFavoriteBtn;
    private Button gotoWebpageBtn;
    public ArrayList<Recipe> foodList = new ArrayList<>();
    //private BaseAdapter MyFavoriteListAdapter;

    private static final String TAG = "RecipeFavoriteActivity";
    public static final String ITEM_ID = "ID";
    public static final String TITLE = "TITLE";
    public static final String IMAGE_URL = "IMAGE_URL";
    public static final String URL = "URL";

    private MyListAdapter myAdapter;
    private RecipeDBHelper dbHelper;
    private SQLiteDatabase db;

    private Long selectID;
    private String selectTitle,selectImage_URL, selectSource_URL;
    //--------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_favorite);
        init();
    }

    private void init() {
        listView = findViewById(R.id.food_favorite_ListView);
        removeFavoriteBtn = findViewById(R.id.recipe_remove_favorite_btn);
        gotoWebpageBtn = findViewById(R.id.recipe_favorite_webview_btn);
        Cursor results = new RecipeDBHelper(this).getData();
        myAdapter = new MyListAdapter();
        listView.setAdapter(myAdapter);
        //find the column indices:
        int idColIndex = results.getColumnIndex(RecipeDBHelper.COL_ID);
        int titleColIndex = results.getColumnIndex(RecipeDBHelper.COL_TITLE);
        int imageColumnIndex = results.getColumnIndex(RecipeDBHelper.COL_IMAGE_URL);
        int urlColIndex = results.getColumnIndex(RecipeDBHelper.COL_URL);

        //iterate over the results, return true if there is a next item:
        while(results.moveToNext())
        {
            String imageUrl = results.getString(imageColumnIndex);
            String url = results.getString(urlColIndex);
            String title = results.getString(titleColIndex);
            long id = results.getLong(idColIndex);
            //add the new Contact to the array list:
            foodList.add(new Recipe(id, title,imageUrl, url));
        }

//Select an item and choose the button Favorite or Webpage
        listView.setOnItemClickListener( (list, item, position, id) -> {
        int selectPosition = position;
        selectID = id;
        selectTitle = foodList.get(position).getTitle();
        selectImage_URL = foodList.get(position).getImageURL();
        selectSource_URL = foodList.get(position).getSourceURL();
        });

//Remove Favorite from the DB and listView (foodList)
        removeFavoriteBtn.setOnClickListener( clk -> {
            dbHelper = new RecipeDBHelper(this);
            db = dbHelper.getWritableDatabase();

            int index = -1;
            for (int i = 0; i < foodList.size(); i++) {
                if (foodList.get(i).getTitle() == selectTitle) {
                    index = i;
                    break;
                }
            }

            db.delete(RecipeDBHelper.TABLE_NAME, RecipeDBHelper.COL_TITLE + "= ?", new String[]{selectTitle});

            foodList.remove(index);
            myAdapter.notifyDataSetChanged();
            db.close();
            Toast.makeText(this, "The item has been removed !", Toast.LENGTH_LONG).show();

        });

//Goto Web page
        gotoWebpageBtn.setOnClickListener(k->{
            Toast.makeText(this, "Go to the Web Page!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, RecipeWebViewActivity.class);
            intent.putExtra(TITLE,selectSource_URL);
            startActivity(intent);
        });
    }//init()

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
                startActivity(new Intent(RecipeFavoriteActivity.this, MainActivity.class));
                return true;
            case R.id.recipe_menu_favorite:
                startActivity(new Intent(RecipeFavoriteActivity.this, RecipeFavoriteActivity.class));
                return true;
            case R.id.menu_car:
                startActivity(new Intent(RecipeFavoriteActivity.this, MainActivity.class));
            case R.id.menu_currency:
                startActivity(new Intent(RecipeFavoriteActivity.this, MainActivity_currency.class));
                return true;
            case R.id.menu_news:
                startActivity(new Intent(RecipeFavoriteActivity.this, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//list of the searched food
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

            thisRow = getLayoutInflater().inflate(R.layout.recipe_row_layout, null);

            TextView titleText = thisRow.findViewById(R.id.recipe_listview_row_title);
            titleText.setText(getItem(p).getTitle());

            return thisRow;
        }

    }


}
