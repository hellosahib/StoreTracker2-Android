package tech.rtsproduction.stockinventory.Database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class StockContract {

    /*
    * I Cant getString Method here if i want to Retrieve Strings from Strings.xml
    * What i am doing wrong ??
     */

    //MARK :URI CONSTANTS
    public static final String CONTENT_AUTHORITY = "tech.rtsproduction.stockinventory";
    public static final String PATH_PRODUCT_STOCKS = "product_stock";
    private static final String BASE_CONTENT = "content://";
    private static final Uri BASE_URI = Uri.parse(BASE_CONTENT + CONTENT_AUTHORITY);

    private StockContract() {
    }

    public static abstract class StockEntry implements BaseColumns {
        //CONTENT URI
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH_PRODUCT_STOCKS);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT_STOCKS;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT_STOCKS;
        //MARK :TABLE CONSTANTS
        //TABLE NAME
        public static final String TABLE_NAME = "product_stock";
        //MARK :COLUMNS NAME
        public static final String _ID = "_id";
        public static final String COLUMN_PRODUCT_NAME = "product_name";
        public static final String COLUMN_PRODUCT_PRICE = "product_price";
        public static final String COLUMN_PRODUCT_QUANTITY = "product_quantity";
        public static final String COLUMN_PRODUCT_SUPPLIER_NAME = "product_supplier";
        public static final String COLUMN_PRODUCT_SUPPLIER_PHONE = "product_supplier_no";
    }
}
