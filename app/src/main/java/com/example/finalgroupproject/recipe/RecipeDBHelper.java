package com.example.finalgroupproject.recipe;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecipeDBHelper extends SQLiteOpenHelper {
    /**
     * To create and open DB:
     * it will be display on the ListView.
     * User can select a title, to remove the recipe or go to web page
     */
    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "recipeDatabase.db";
    public static final String TABLE_NAME = "recipes";
    public static final String COL_ID= "recipe_id";
    public static final String COL_TITLE = "title";
    public static final String COL_IMAGE_URL = "image_url";
    public static final String COL_URL = "url";
    //Constructor: pass information to the super constructor of SQLiteOpenHelper
    //@param context, where the database is being opened
    //DATABASE_NAME - the file contains the data
    //null/CursorFactory - create Cursor objects
    //VERSION - version of your database
    public RecipeDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }
    /*
     * execute a string SQL statement
     * @param db, a database object given by Android for running SQL commands
     * TEXT means String in Java
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "( "
                + COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_TITLE + " TEXT, " + COL_IMAGE_URL + " TEXT, "+ COL_URL + " TEXT)");
    }
    /*
     *Insert in the table. add a value for each column in the table, and then insert last.
     *returns database ID of the row just inserted
     */
    public long insert( String title, String imageURL, String sourceURL) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TITLE, title);
        contentValues.put(COL_IMAGE_URL, imageURL);
        contentValues.put(COL_URL, sourceURL);
        //return new id for inserted entity
        return getWritableDatabase().insert(TABLE_NAME, null, contentValues);
    }
    //get information from Database.
    //db.query(true, TABLE_NAME, new String[], null...)
    public Cursor getData() {
        String[] foodTable = {COL_ID,COL_TITLE,COL_IMAGE_URL,COL_URL};
        return getWritableDatabase().query(TABLE_NAME, foodTable, null,null, null, null, null);
    }
    //When the database exist on the device, and the version in Constructor is higher than the version that exists on the device
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    // Useful info.
    // getWritableDatabase() -  reading and writing to the database.
    // The first time this is called, the database will be opened and onCreate(), onUpgrade()
    // and/or onOpen() will be called.
    //Once opened successfully, the database is cached,
    //so you can call this method every time you need to write to the database.
    //(Make sure to call close() when you no longer need the database.)
}


