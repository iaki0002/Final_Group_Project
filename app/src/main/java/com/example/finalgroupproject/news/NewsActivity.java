package com.example.finalgroupproject.news;
/*
Author: David Lee
Student Number: 040959646
Course: CST2335
Date: Dec 02, 2019
 */

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalgroupproject.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * the activity that shows the list of news articles
 */
public class NewsActivity extends AppCompatActivity {

    private ImageView articleImage;
    private TextView articleTitle;
    private TextView articleDesc;
    private ListView articleList;
    private ProgressBar newsProgress;
    private ArrayList<Articles> articlesArrayList = new ArrayList<>();
    private ArrayList<Articles> savedArrayList = new ArrayList<>();
    private BaseAdapter adapter;
    private String keyword;
    SQLiteDatabase db;
    private Toolbar tBar;


    public static final String NEWS_API = "NEWS_API";
    private Context thisApp ;

    /**
     * onCreate function
     * @param savedInstanceState received instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        articleImage = (ImageView) findViewById(R.id.imageview);
        articleTitle = (TextView) findViewById(R.id.titleview);
        articleDesc = (TextView) findViewById(R.id.descview);
        articleList = findViewById(R.id.articlelist);

        newsProgress = (ProgressBar) findViewById(R.id.newsProgress);

        thisApp = this;

        tBar = findViewById(R.id.toolbarnews);
        setSupportActionBar(tBar);

        Intent mainIntent = getIntent();
        keyword = mainIntent.getStringExtra("keyword");

        adapter = new NewsAdapter(articlesArrayList, this);
        articleList.setAdapter(adapter);

        MyDatabaseOpenHelper dbOpener = new MyDatabaseOpenHelper(this);
        db = dbOpener.getWritableDatabase();

        //query all the results from the database:
        String [] columns = {MyDatabaseOpenHelper.COL_ID, MyDatabaseOpenHelper.COL_TITLE, MyDatabaseOpenHelper.COL_LINK};
        Cursor results = db.query(false, MyDatabaseOpenHelper.TABLE_NAME, columns, null, null, null, null, null, null);

        //find the column indices:
        int titleColIndex = results.getColumnIndex(MyDatabaseOpenHelper.COL_TITLE);
        int linkColIndex = results.getColumnIndex(MyDatabaseOpenHelper.COL_LINK);
        int idColIndex = results.getColumnIndex(MyDatabaseOpenHelper.COL_ID);

        while(results.moveToNext()){
            String title = results.getString(titleColIndex);
            String link = results.getString(linkColIndex);
            long id = results.getLong(idColIndex);
            savedArrayList.add(new Articles(id, title, link));
        }



        articleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Articles articles = (Articles) parent.getItemAtPosition(position);

                View middle = getLayoutInflater().inflate(R.layout.select_actions, null);


                AlertDialog.Builder builder = new AlertDialog.Builder(NewsActivity.this);
                builder.setMessage("What would you like to do?")
                        .setPositiveButton("Edit Saved List", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent savedActivity = new Intent(NewsActivity.this, SavedListActivity.class);
                                startActivity(savedActivity);
                            }
                        })
                        .setNegativeButton("Web Link", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                browserIntent.setData(Uri.parse(articles.getUrl()));
                                startActivity(browserIntent);
                            }
                        })
                        .setNeutralButton("Save Aritcle", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                String title = articles.getTitle().toString();
                                String link = articles.getUrl().toString();

                                ContentValues newRowValues = new ContentValues();
                                newRowValues.put(MyDatabaseOpenHelper.COL_TITLE, title);
                                newRowValues.put(MyDatabaseOpenHelper.COL_LINK, link);
                                long newId = db.insert(MyDatabaseOpenHelper.TABLE_NAME, null, newRowValues);

                                savedArrayList.add(articles);

                                Toast.makeText(NewsActivity.this, "Item Saved", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setView(middle);

                builder.create().show();
            }
        });


        String urlQuery = "https://newsapi.org/v2/everything?apiKey=55c12e64df3b401eaf65e84b5cde7066&q=" + keyword;
        NewsQuery newsQuery = new NewsQuery();
        String query = Uri.encode(urlQuery);

        newsProgress.setVisibility(View.VISIBLE);
        newsQuery.execute(query);
        new NewsQuery().execute(urlQuery);
    }


    /**
     * Async class
     */
    class NewsQuery extends AsyncTask<String, Integer, String> {

        String title;
        String description;
        String webLink;
        Bitmap newsImage;

        /**
         * doInBackground background thread use of AsyncTask
         * @param urls the url of the news search
         * @return String object ret
         */
        @Override
        protected String doInBackground(String... urls) {
            String ret = null;
            URL url;
            HttpsURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpsURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;

                publishProgress(25);
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString();

                JSONObject jObject = new JSONObject(result);
                JSONArray posts = jObject.optJSONArray("articles");
                Articles article;
                publishProgress(50);
                for (int i =0; i < posts.length(); i++){
                    JSONObject post = posts.optJSONObject(i);
                    String title = post.optString("title");
                    String image = post.optString("urlToImage");
                    String description = post.optString("description");
                    String urlLink = post.optString("url");
                    article = new Articles();
                    article.setTitle((title));
                    article.setImage(image);
                    article.setUrl(urlLink);
                    article.setDescription(description);

                    articlesArrayList.add(article);


                }
                publishProgress(100);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException mfe) {
                ret = "Malformed URL exception";
            } catch (IOException ioe) {
                ret = "IO Exception. Is the Wifi connected?";
            }



            return ret;
        }


        /**
         * onPostExecute method of AsyncTask
         * @param sentFromDoInBackground the string object sent from doinbackground
         */
        @Override                   //Type 3
        protected void onPostExecute(String sentFromDoInBackground) {
            super.onPostExecute(sentFromDoInBackground);

        }

        /**
         * onProgressUpdate talks to GUI thread
         * @param values publishprogress values
         */
        @Override                       //Type 2
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //Update GUI stuff only:
            //   newsProgress.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
            newsProgress.setProgress(values[0]);
        }




    }

    /**
     * @param menu menu_news object
     * @return the boolean value of true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_news items for use in the action bar
        MenuInflater inflater = getMenuInflater();
       // inflater.inflate(R.menu.menu_news, menu);
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
                Intent savedActivity = new Intent(NewsActivity.this, SavedListActivity.class);
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

