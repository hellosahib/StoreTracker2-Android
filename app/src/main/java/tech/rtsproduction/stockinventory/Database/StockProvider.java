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

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case FULL_STOCK: {
                return StockEntry.CONTENT_LIST_TYPE;
            }
            case PARTICULAR_STOCK: {
                return StockEntry.CONTENT_ITEM_TYPE;
            }
            default: {
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch (mUriMatcher.match(uri)) {
            case FULL_STOCK: {
                if (values != null) {
                    return insertStock(uri, values);
                }
            }
            default: {
                throw new IllegalArgumentException("Insertion Not Supported,Unknown URI " + uri);
            }
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        switch (mUriMatcher.match(uri)) {
            case FULL_STOCK: {
                getContext().getContentResolver().notifyChange(uri, null);
                return database.delete(StockEntry.TABLE_NAME, selection, selectionArgs);
            }
            case PARTICULAR_STOCK: {
                selection = StockEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                getContext().getContentResolver().notifyChange(uri, null);
                return database.delete(StockEntry.TABLE_NAME, selection, selectionArgs);
            }
            default: {
                throw new IllegalArgumentException("Deletion is not Supported,Invalid Uri " + uri);
            }
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (mUriMatcher.match(uri)) {
            case FULL_STOCK: {
                return updateStock(uri, values, selection, selectionArgs);
            }
            case PARTICULAR_STOCK: {
                selection = StockEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateStock(uri, values, selection, selectionArgs);
            }
            default: {
                throw new IllegalArgumentException("Update Not Performed,Unknown URI " + uri);
            }
        }
    }

    /**
     * USER DEFINED METHODS
     */
    private int updateStock(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            sanityCheck(values);
        } catch (IllegalArgumentException e) {
            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            return -1;
        }
        int updatesDone = database.update(StockEntry.TABLE_NAME, values, selection, selectionArgs);
        if (updatesDone != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updatesDone;
    }

    public void sanityCheck(ContentValues values) {
        //DATA VALIDATION
        if (values.size() == 0) {
            return;
        } else if (values.containsKey(StockEntry.COLUMN_PRODUCT_NAME)) {
            if (TextUtils.isEmpty(values.getAsString(StockEntry.COLUMN_PRODUCT_NAME))) {
                throw new IllegalArgumentException("Name Could Not be Null");
            }
        } else if (values.containsKey(StockEntry.COLUMN_PRODUCT_PRICE)) {
            if (values.getAsInteger(StockEntry.COLUMN_PRODUCT_PRICE) <= 0) {
                throw new IllegalArgumentException("Price Could Not be Null or less");
            }
        } else if (values.containsKey(StockEntry.COLUMN_PRODUCT_QUANTITY)) {
            if (values.getAsInteger(StockEntry.COLUMN_PRODUCT_QUANTITY) < 0) {
                throw new IllegalArgumentException("Quantity Could not be less than 0");
            }
        } else if (values.containsKey(StockEntry.COLUMN_PRODUCT_SUPPLIER_NAME)) {
            if (TextUtils.isEmpty(values.getAsString(StockEntry.COLUMN_PRODUCT_SUPPLIER_NAME))) {
                throw new IllegalArgumentException("Supplier Name Could not be Empty");
            }
        } else if (values.containsKey(StockEntry.COLUMN_PRODUCT_SUPPLIER_PHONE)) {
            if (TextUtils.isEmpty(values.getAsString(StockEntry.COLUMN_PRODUCT_SUPPLIER_PHONE))) {
                throw new IllegalArgumentException("Supplier Name Should not be Empty");
            }
        }
    }

    private Uri insertStock(Uri uri, ContentValues values) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        sanityCheck(values);
        //DATABASE INSERT RETURN LONG
        long id = database.insert(StockEntry.TABLE_NAME, null, values);
        //-1 SIGNIFY ANY ERROR IN DATA INSERTION
        if (id == -1) {
            Log.e("InsertStock", "Failed To Insert");
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }
}
