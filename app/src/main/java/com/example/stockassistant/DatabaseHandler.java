package com.example.stockassistant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static android.provider.ContactsContract.CommonDataKinds.Organization.SYMBOL;
import static android.provider.ContactsContract.Intents.Insert.COMPANY;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHandler";
    private static final String DATABASE_NAME = "StockDB";
    private static final String TABLE_NAME = "StockTable";
    private static final String STOCK_SYMBOL = "StockSymbol";
    private static final String COMPANY_NAME = "CompanyName";
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
            " (" + STOCK_SYMBOL + " TEXT not null unique," + COMPANY_NAME + " TEXT not null )";
    private final SQLiteDatabase database;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, 1);
        database = getWritableDatabase();
        Log.d(TAG, "DatabaseHandler: DONE");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public ArrayList<Stock> LoadStocks() {
        ArrayList<Stock> stock = new ArrayList<>();
        Cursor cursor = database.query(TABLE_NAME, new String[]{STOCK_SYMBOL, COMPANY_NAME},
                null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                Stock s = new Stock();
                String stockSymbol = cursor.getString(0);
                String stockName = cursor.getString(1);
                s.setStockSymbol(stockSymbol);
                s.setStockCompanyName(stockName);
                s.setStockLatestPrice(0.0);
                s.setStockPriceChange(0.0);
                s.setStockChangePercentage(0.0);
                stock.add(s);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return stock;
    }

    public void addStock(Stock stock) {
        Log.d(TAG, "addStock: Adding " + stock.getStockSymbol());
        ContentValues values = new ContentValues();
        values.put(STOCK_SYMBOL, stock.getStockSymbol());
        values.put(COMPANY_NAME, stock.getStockCompanyName());
        database.insert(TABLE_NAME,null,values);
        Log.d(TAG, "addStock: Add Complete");

    }

    public void deleteStock(String symbol) {
        Log.d(TAG, "deleteStock: Deleting Stock " + symbol);
        int cnt = database.delete(TABLE_NAME, STOCK_SYMBOL + " = ?", new String[]{symbol});
        Log.d(TAG, "deleteStock: " + cnt);
    }

    public void shutDown() {
        database.close();
    }
}
