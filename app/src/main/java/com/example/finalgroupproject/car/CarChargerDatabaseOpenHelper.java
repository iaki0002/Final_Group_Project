package com.example.finalgroupproject.car;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

class CarChargerDatabaseOpenHelper extends SQLiteOpenHelper {

    /**
     * Class constant that represents the name of the database.
     */
    private static final String DATABASE_NAME = "CharingStationsDB";
    /**
     * Class constant that represents the version number of the database.
     */
    private static final int VERSION = 1;
    /**
     * Class constant that represents the name of the table in the database.
     */
    static final String TABLE_NAME = "STATIONS";
    /**
     * Class constant that represents the id column in the database.
     */
    static final String COL_ID = "id";
    /**
     * Class constant that represents the ChargerStation's name column in the database.
     */
    static final String COL_LOCATION_TITLE = "locationTitle";
    /**
     * Class constant that represents the ChargerStation's latitude column in the database.
     */
    static final String COL_LATITUDE = "latitude";
    /**
     * Class constant that represents the ChargerStation's longitude column in the database.
     */
    static final String COL_LONGITUDE = "longitude";
    /**
     * Class constant that represents the ChargerStation's phone number column in the database.
     */
    static final String COL_PHONE_NUM = "phoneNumber";

    /**
     * Default constructor that is used to bind the Database to the Activity that is passed in.
     * @param cx - {@link Activity}
     */
    CarChargerDatabaseOpenHelper(Activity cx) {
        super(cx, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + COL_LOCATION_TITLE + " TEXT, "
                + COL_LATITUDE + " REAL, " + COL_LONGITUDE + " REAL, " + COL_PHONE_NUM + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
