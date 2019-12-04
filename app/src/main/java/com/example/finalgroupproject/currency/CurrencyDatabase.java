package com.example.finalgroupproject.currency;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * creating the database for saving to favourites (not completed)
 */
public class CurrencyDatabase extends SQLiteOpenHelper {

    /**
     * initializing variables
     */
    public static final int VERSION_NUM = 2;
    public static final String DATABASE_NAME = "CurrencyDB";
    public static final String TABLE_NAME = "CurrencyTable";
    public static final String COLUMN_ID = "BASE_ID";
    public static final String COLUMN_BASE_CURRENCY = "BASE_CURRENCY";
    public static final String COLUMN_TARGET_CURRENCY = "TARGET_CURRENCY";
    public static final String COLUMN_DATE = "DATE";
    public static final String COLUMN_BASE_AMOUNT = "BASE_AMOUNT";
    public static final String COLUMN_BASE_RESULT = "BASE_RESULT";


    public CurrencyDatabase(Activity ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_NAME +
                "( " + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_BASE_CURRENCY + " TEXT, " + COLUMN_TARGET_CURRENCY + " TEXT, "+ COLUMN_DATE + " TEXT, " + COLUMN_BASE_AMOUNT + " TEXT, "   + COLUMN_BASE_RESULT + " TEXT " + " );";
        sqLiteDatabase.execSQL(query);
    }

    /**
     * upgrade or downgrade database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}


