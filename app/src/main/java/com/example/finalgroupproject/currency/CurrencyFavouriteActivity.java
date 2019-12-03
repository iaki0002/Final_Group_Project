package com.example.finalgroupproject.currency;
//package finalproject.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.finalgroupproject.R;

import java.util.ArrayList;

public class CurrencyFavouriteActivity extends AppCompatActivity {

    ArrayList<CurrencyConversion> conversionList;
    Cursor cursor;
    SQLiteDatabase database;
    CurrencyDatabase databaseOpener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_favourite);

        database = databaseOpener.getWritableDatabase();


        cursor = database.query(false, CurrencyDatabase.TABLE_NAME, new String[]{CurrencyDatabase.COLUMN_ID,
                        CurrencyDatabase.COLUMN_BASE_CURRENCY, CurrencyDatabase.COLUMN_TARGET_CURRENCY, CurrencyDatabase.COLUMN_DATE, CurrencyDatabase.COLUMN_BASE_AMOUNT,
                        CurrencyDatabase.COLUMN_BASE_RESULT},
                null, null, null, null, null, null);

        cursor.moveToNext();
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(CurrencyDatabase.COLUMN_ID));
            String baseCurrency = cursor.getString(cursor.getColumnIndex(CurrencyDatabase.COLUMN_BASE_CURRENCY));
            String targetCurrency = cursor.getString(cursor.getColumnIndex(CurrencyDatabase.COLUMN_TARGET_CURRENCY));
            String date = cursor.getString(cursor.getColumnIndex(CurrencyDatabase.COLUMN_DATE));
            String baseAmount = cursor.getString(cursor.getColumnIndex(CurrencyDatabase.COLUMN_BASE_AMOUNT));
            String baseResult = cursor.getString(cursor.getColumnIndex(CurrencyDatabase.COLUMN_BASE_AMOUNT));


        }
    }
}
