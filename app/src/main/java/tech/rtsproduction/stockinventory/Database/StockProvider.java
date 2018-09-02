package tech.rtsproduction.stockinventory.Database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import tech.rtsproduction.stockinventory.Database.StockContract.StockEntry;


public class StockProvider extends ContentProvider {

    DBHelper dbHelper;

    /**
     * URI matcher code for the content URI for the Stocks table
     */
    private static final int FULL_STOCK = 100;
    /**
     * URI matcher code for the content URI for single item in the Stocks table
     */
    private static final int PARTICULAR_STOCK = 99;

    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /* For all of the content URI patterns that the provider should recognize */
    static {
        mUriMatcher.addURI(StockContract.CONTENT_AUTHORITY, StockContract.PATH_PRODUCT_STOCKS, FULL_STOCK);
        mUriMatcher.addURI(StockContract.CONTENT_AUTHORITY, StockContract.PATH_PRODUCT_STOCKS + "/#", PARTICULAR_STOCK);
    }


    //CONTENT PROVIDER METHODS
    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor;
        switch (mUriMatcher.match(uri)) {
            case FULL_STOCK: {
                cursor = database.query(StockContract.StockEntry.TABLE_NAME, null, null, null, null, null, null);
                break;
            }
            case PARTICULAR_STOCK: {
                selection = StockContract.StockEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(StockContract.StockEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            default: {
                throw new IllegalArgumentException("Cannot Query Unknown URI " + uri);
            }
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch (mUriMatcher.match(uri)){
            case FULL_STOCK:{
                if(values != null){
                    return insertStock(uri,values);
                }
            }
            default:{
                throw new IllegalArgumentException("Insertion Not Supported,Unknown URI " + uri);
            }
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        
        return 0;
    }


    private Uri insertStock(Uri uri,ContentValues values){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        //DATA VALIDATION
        if(TextUtils.isEmpty(values.getAsString(StockEntry.COLUMN_PRODUCT_NAME))){
            throw new IllegalArgumentException("Name Could Not be Null");
        }
        if(values.getAsInteger(StockEntry.COLUMN_PRODUCT_PRICE) <= 0){
            throw new IllegalArgumentException("Price Could Not be Null or less");
        }
        if(values.getAsInteger(StockEntry.COLUMN_PRODUCT_QUANTITY) < 0){
            throw new IllegalArgumentException("Quantity Could not be less than 0");
        }
        if(TextUtils.isEmpty(values.getAsString(StockEntry.COLUMN_PRODUCT_SUPPLIER_NAME))){
            throw new IllegalArgumentException("Supplier Name Could not be Empty");
        }
        if (TextUtils.isEmpty(values.getAsString(StockEntry.COLUMN_PRODUCT_SUPPLIER_PHONE))){
            throw new IllegalArgumentException("Supplier Name Should not be Empty");
        }
        //VALIDATION ENDS HERE
        //DATABASE INSERT RETURN LONG
        long id = database.insert(StockEntry.TABLE_NAME,null,values);
        //-1 SIGNIFY ANY ERROR IN DATA INSERTION
        if(id == -1){
            Log.e("InsertStock","Failed To Insert");
            return null;
        }
        return ContentUris.withAppendedId(uri,id);
    }
}
