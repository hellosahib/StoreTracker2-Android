package tech.rtsproduction.stockinventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import tech.rtsproduction.stockinventory.Database.StockContract.StockEntry;

public class StockCursorAdapter extends CursorAdapter {

    private ContentValues updateValues = new ContentValues();

    public StockCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        //Finding Textviews To Populate Data
        TextView productName = view.findViewById(R.id.productTextItem);
        TextView productPrice = view.findViewById(R.id.productPriceTextItem);
        TextView productQuantity = view.findViewById(R.id.productQuantityTextItem);
        final Button saleBtn = view.findViewById(R.id.saleBtnItem);
        //Extract Data From Cursor
        String name = cursor.getString(cursor.getColumnIndexOrThrow(StockEntry.COLUMN_PRODUCT_NAME));
        Integer price = cursor.getInt(cursor.getColumnIndexOrThrow(StockEntry.COLUMN_PRODUCT_PRICE));
        final Integer quantity = cursor.getInt(cursor.getColumnIndexOrThrow(StockEntry.COLUMN_PRODUCT_QUANTITY));
        final long id = cursor.getLong(cursor.getColumnIndexOrThrow(StockEntry._ID));
        //Populate The TextView
        productName.setText(name);
        productPrice.setText("$" + String.valueOf(price));
        productQuantity.setText("Quantity " + String.valueOf(quantity));

        saleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity < 1) {
                    Toast.makeText(context, "Insufficent Quantity", Toast.LENGTH_SHORT).show();
                } else {
                    Uri itemUri = saleProduct(quantity, id);
                    context.getContentResolver().update(itemUri, updateValues, null, null);
                }

            }
        });

    }

    private Uri saleProduct(Integer quant, long id) {
        quant--;
        updateValues.put(StockEntry.COLUMN_PRODUCT_QUANTITY, quant);
        return ContentUris.withAppendedId(StockEntry.CONTENT_URI, id);
    }
}
