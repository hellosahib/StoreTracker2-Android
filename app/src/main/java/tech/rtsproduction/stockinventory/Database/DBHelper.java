package tech.rtsproduction.stockinventory.Database;

import tech.rtsproduction.stockinventory.Database.StockContract.StockEntry;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "inventory.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE = "CREATE TABLE " + StockEntry.TABLE_NAME + "("
                + StockEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + ","
                + StockEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL" + ","
                + StockEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL" + ","
                + StockEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0" + ","
                + StockEntry.COLUMN_PRODUCT_SUPPLIER_NAME + " TEXT NOT NULL" + ","
                + StockEntry.COLUMN_PRODUCT_SUPPLIER_PHONE + " TEXT"
                + ");";
        db.execSQL(SQL_CREATE_TABLE);
    }//OnCreate

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }//OnUpgrade
}
