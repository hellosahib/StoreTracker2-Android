package tech.rtsproduction.stockinventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import tech.rtsproduction.stockinventory.Database.StockContract.StockEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    /*
     * Why Does Opening This Activity Takes Time And  Bring Load on CPU ?
     */

    //VARIABLES
    Toolbar toolbarEditor;
    TextInputEditText pName, pPrice, pQuantity, pSupplierName, pSupplierNo;
    Button plusQuantity, minusQuantity, orderBtn;
    private Uri mCurrentStockUri;

    public static final int DB_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        //FIND VIEW BY ID'S
        toolbarEditor = findViewById(R.id.toolbarEditor);
        pName = findViewById(R.id.productNameEdit);
        pPrice = findViewById(R.id.productPriceEdit);
        pQuantity = findViewById(R.id.productQuantityEdit);
        pSupplierName = findViewById(R.id.productSupplierEdit);
        pSupplierNo = findViewById(R.id.productSupplierNoEdit);
        plusQuantity = findViewById(R.id.plusQuantityBtnEditor);
        minusQuantity = findViewById(R.id.minusQuantityBtnEditor);
        orderBtn = findViewById(R.id.orderBtnEditor);
        //INIT
        setSupportActionBar(toolbarEditor);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCurrentStockUri = getIntent().getData();
        if (mCurrentStockUri != null) {
            getSupportActionBar().setTitle(R.string.edit_stock);
            getLoaderManager().initLoader(DB_LOADER, null, this);
        }

        plusQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(pQuantity.getText().toString());
                quantity++;
                pQuantity.setText(String.valueOf(quantity));
            }
        });

        minusQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(pQuantity.getText().toString());
                if (quantity > 0) {
                    quantity--;
                    pQuantity.setText(String.valueOf(quantity));
                } else {
                    Toast.makeText(EditorActivity.this, "Value Cant be Negative", Toast.LENGTH_SHORT).show();
                }
            }
        });

        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", pSupplierNo.getText().toString(), null));
                startActivity(phoneIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.deleteMenu);
        if (mCurrentStockUri == null) {
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.saveMenu: {
                //SAVE THE DATA
                if (saveData()) {
                    finish();
                }
                return true;
            }
            case R.id.deleteMenu: {
                //Delete The Record
                showDeleteDialog();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * LOADER METHODS
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, mCurrentStockUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            pName.setText(data.getString(data.getColumnIndex(StockEntry.COLUMN_PRODUCT_NAME)));
            pPrice.setText(String.valueOf(data.getInt(data.getColumnIndex(StockEntry.COLUMN_PRODUCT_PRICE))));
            pQuantity.setText(String.valueOf(data.getInt(data.getColumnIndex(StockEntry.COLUMN_PRODUCT_QUANTITY))));
            pSupplierName.setText(data.getString(data.getColumnIndex(StockEntry.COLUMN_PRODUCT_SUPPLIER_NAME)));
            pSupplierNo.setText(data.getString(data.getColumnIndex(StockEntry.COLUMN_PRODUCT_SUPPLIER_PHONE)));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        pName.setText("");
        pPrice.setText("");
        pQuantity.setText("");
        pSupplierName.setText("");
        pSupplierNo.setText("");
    }

    /**
     * USER IMPLEMENTED FUNCTIONS
     */
    public boolean saveData() {
        ContentValues values = new ContentValues();
        values.put(StockEntry.COLUMN_PRODUCT_NAME, pName.getText().toString());
        values.put(StockEntry.COLUMN_PRODUCT_PRICE, pPrice.getText().toString());
        values.put(StockEntry.COLUMN_PRODUCT_QUANTITY, pQuantity.getText().toString());
        values.put(StockEntry.COLUMN_PRODUCT_SUPPLIER_NAME, pSupplierName.getText().toString());
        values.put(StockEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, pSupplierNo.getText().toString());
        Uri uri = null;
        if (mCurrentStockUri != null) {
            //Update Existing Data
            int update = getContentResolver().update(mCurrentStockUri, values, null, null);
            if (update == -1) {
                throw new IllegalArgumentException("Update Failed " + mCurrentStockUri);
            }
            return true;
        } else {
            //INSERT DATA FROM EDIT TEXTS'S AND PUSH THEM TO DB
            try {
                uri = getContentResolver().insert(StockEntry.CONTENT_URI, values);
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
            if (uri == null) {
                Log.e("DB Error", getString(R.string.insertion_failed));
                return false;
            }
        }
        return true;
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_record);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                if (deletePet()) {
                    Toast.makeText(EditorActivity.this, "Record Deleted", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private boolean deletePet() {
        try {
            getContentResolver().delete(mCurrentStockUri, null, null);
            return true;
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
