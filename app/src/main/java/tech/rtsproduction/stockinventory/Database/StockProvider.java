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

import java.net.URI;

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
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
