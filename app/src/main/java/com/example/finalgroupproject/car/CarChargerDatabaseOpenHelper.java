package com.example.finalgroupproject.car;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

class CarChargerDatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CharingStationsDB";
    private static final int VERSION = 1;
    static final String TABLE_NAME = "STATIONS";
    static final String COL_ID = "id";
    static final String COL_LOCATION_TITLE = "locationTitle";
    static final String COL_LATITUDE = "latitude";
    static final String COL_LONGITUDE = "longitude";
    static final String COL_PHONE_NUM = "phoneNumber";

    protected CarChargerDatabaseOpenHelper(Activity cx) {
        super(cx, DATABASE_NAME, null, VERSION);
    }

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
