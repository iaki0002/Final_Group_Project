package com.example.finalgroupproject.news;

/*
Author: David Lee
Student Number: 040959646
Course: CST2335
Date: Dec 02, 2019
 */

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.finalgroupproject.R;
import com.google.android.material.snackbar.Snackbar;


/**
 *  the main activity class which runs when the console initializes
 */
public class NewsMainActivity extends AppCompatActivity {

    private EditText searchEdit;
    private Button searchButton;

    private Toolbar tBar;

    private Toast toast;
    private Snackbar snackBar;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEYWORD = "keyword";
    String keyword;

    /**
     * creates a custom dialog
     */
    public void showDialog (){
        Dialog dialog = new Dialog(NewsMainActivity.this);
        dialog.setContentView(R.layout.news_custom_dialog_layout);
        dialog.show();
    }

    /**
     * onCreate function
     * @param savedInstanceState received instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        keyword = sharedPreferences.getString(KEYWORD, "");

        searchEdit = (EditText) findViewById(R.id.searchedit);
        searchButton = (Button) findViewById(R.id.buttonsearch);

        tBar = findViewById(R.id.toolbar);
        setSupportActionBar(tBar);
        searchEdit.setText(keyword);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchEdit.getText().toString() == ""){
                    showDialog();
                }else {
                    Toast.makeText(NewsMainActivity.this, "Searching " + searchEdit.getText().toString(), Toast.LENGTH_SHORT).show();
                    snackBar = Snackbar.make(tBar, "Article Saved", Snackbar.LENGTH_SHORT).setAction("Action",null);
                    snackBar.show();
                    Intent newsActivity = new Intent(NewsMainActivity.this, NewsActivity.class);
                    newsActivity.putExtra("keyword", searchEdit.getText().toString());
                    startActivity(newsActivity);

                }
            }
        });

    }

    /**
     * onPause function of the lifecycle
     */
    @Override
    protected void onPause() {
        super.onPause();

        // save a user's email address
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEYWORD, searchEdit.getText().toString());

        editor.apply();
    }

    /**
     * @param menu the menu_news object
     * @return boolean value of true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_news items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.news_menu, menu);
        return true;
    }

    /**
     * @param item menuitem object
     * @return boolean value of true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.help:
                showHelp();
                break;

            case R.id.exit:
                Snackbar sb = Snackbar.make(tBar, "Exit?", Snackbar.LENGTH_LONG);
                sb.setAction("Terminate", e -> finish());
                sb.show();
                break;

            case R.id.edit:
                Intent savedActivity = new Intent(NewsMainActivity.this, SavedListActivity.class);
                startActivity(savedActivity);
        }
        return true;
    }

    /**
     * creates a custom dialog of instructions
     */
    public void showHelp()
    {
        View middle = getLayoutInflater().inflate(R.layout.help_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("HELP")
                .setView(middle);

        builder.create().show();

    }

}
