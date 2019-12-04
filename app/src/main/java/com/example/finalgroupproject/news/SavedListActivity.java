package com.example.finalgroupproject.news;

/*
Author: David Lee
Student Number: 040959646
Course: CST2335
Date: Dec 02, 2019
 */
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import android.widget.ListView;

import com.example.finalgroupproject.R;

import java.util.ArrayList;

/**
 * activity for a list of saved articles
 */
public class SavedListActivity extends AppCompatActivity {

    private ListView savedList;

    private ArrayList<Articles> savedArrayList = new ArrayList<>();
    private BaseAdapter adapter;
    private Cursor cursor;
    SQLiteDatabase db;

    /**
     * onCreate function
     * @param savedInstanceState received instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_list);

        savedList = (ListView) findViewById(R.id.savedlist);


        adapter = new SavedAdapter(savedArrayList, this);
        savedList.setAdapter(adapter);

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

        savedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Articles articles = (Articles) parent.getItemAtPosition(position);

                View middle = getLayoutInflater().inflate(R.layout.saved_actions, null);


                AlertDialog.Builder builder = new AlertDialog.Builder(SavedListActivity.this);
                builder.setMessage("What would you like to do?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String deleteId = Long.toString(articles.getId());
                                // Define 'where' part of query.
                                String selection = MyDatabaseOpenHelper.COL_ID + " = ?";
                                // Specify arguments in placeholder order.
                                String[] selectionArgs = { deleteId };
                                // Issue SQL statement.
                                db.delete(MyDatabaseOpenHelper.TABLE_NAME, selection, selectionArgs);
                                savedArrayList.remove(articles);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Web Link", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String linkId = Long.toString(articles.getId());
                                Cursor c = db.rawQuery("SELECT "+ MyDatabaseOpenHelper.COL_LINK + " FROM " +MyDatabaseOpenHelper.TABLE_NAME + " WHERE " + MyDatabaseOpenHelper.COL_ID + " = ?", new String[] {linkId});
                                if (c.moveToFirst()){
                                    do {
                                        // Passing values
                                        String link = c.getString(0);
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                        browserIntent.setData(Uri.parse(link));
                                        startActivity(browserIntent);
                                    } while(c.moveToNext());
                                }
                            }



                        })

                        .setView(middle);

                builder.create().show();

            }
        });

    }
}

