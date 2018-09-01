package tech.rtsproduction.stockinventory.Database;

import android.net.Uri;
import android.provider.BaseColumns;

public class StockContract {

    //MARK :URI CONSTANTS
    public static final String BASE_CONTENT = "content://";
    public static final String CONTENT_AUTHORITY = "tech.rtsproduction.stockinventory";
    public static final Uri BASE_URI = Uri.parse(BASE_CONTENT+CONTENT_AUTHORITY);
    public static final String PATH_PRODUCT_STOCKS = "product_stock";

    private StockContract() {
    }

    public static abstract class StockEntry implements BaseColumns {
        //CONTENT URI
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI,PATH_PRODUCT_STOCKS);

        //MARK :TABLE CONSTANTS
        //TABLE NAME
        public static final String TABLE_NAME = "product_stock";
        //MARK :COLUMNS NAME
        public static final String _ID = "product_id";
        public static final String COLUMN_PRODUCT_NAME = "product_name";
        public static final String COLUMN_PRODUCT_PRICE = "product_price";
        public static final String COLUMN_PRODUCT_QUANTITY = "product_quantity";
        public static final String COLUMN_PRODUCT_SUPPLIER_NAME = "product_supplier";
        public static final String COLUMN_PRODUCT_SUPPLIER_PHONE = "product_supplier_no";
    }
}
