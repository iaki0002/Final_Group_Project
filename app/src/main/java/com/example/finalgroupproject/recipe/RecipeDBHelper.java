package com.example.finalgroupproject.recipe;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecipeDBHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "recipeDatabase.db";
    public static final String TABLE_NAME = "recipes";
    public static final String COL_ID= "recipe_id";
    public static final String COL_TITLE = "title";
    public static final String COL_IMAGE_URL = "image_url";
    public static final String COL_URL = "url";

    public RecipeDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "( "
                + COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_TITLE + " TEXT, " + COL_IMAGE_URL + " TEXT, "+ COL_URL + " TEXT)");
    }

    public long insert( String title, String imageURL, String sourceURL) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TITLE, title);
        contentValues.put(COL_IMAGE_URL, imageURL);
        contentValues.put(COL_URL, sourceURL);
        //return new id for inserted entity
        return getWritableDatabase().insert(TABLE_NAME, null, contentValues);
    }
    //get information from Database
    public Cursor getData() {
        String[] foodTable = {COL_ID,COL_TITLE,COL_IMAGE_URL,COL_URL};
        return getWritableDatabase().query(TABLE_NAME, foodTable, null,null, null, null, null);

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}


