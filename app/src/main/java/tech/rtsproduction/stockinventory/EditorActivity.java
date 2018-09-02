package tech.rtsproduction.stockinventory;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import tech.rtsproduction.stockinventory.Database.DBHelper;
import tech.rtsproduction.stockinventory.Database.StockContract.StockEntry;

public class EditorActivity extends AppCompatActivity {

    /*
    * Why Does Opening This Activity Takes Time And  Bring Load on CPU ?
     */

    //VARIABLES
    Toolbar toolbarEditor;
    TextInputEditText pName,pPrice,pQuantity,pSupplierName,pSupplierNo;

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
        //INIT
        setSupportActionBar(toolbarEditor);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu_editor,menu);
        return true;
    }

    public boolean insertData(){
        //INSERT DATA FROM EDIT TEXTS'S AND PUSH THEM TO DB
        ContentValues values = new ContentValues();
        values.put(StockEntry.COLUMN_PRODUCT_NAME,pName.getText().toString());
        values.put(StockEntry.COLUMN_PRODUCT_PRICE,pPrice.getText().toString());
        values.put(StockEntry.COLUMN_PRODUCT_QUANTITY,pQuantity.getText().toString());
        values.put(StockEntry.COLUMN_PRODUCT_SUPPLIER_NAME,pSupplierName.getText().toString());
        values.put(StockEntry.COLUMN_PRODUCT_SUPPLIER_PHONE,pSupplierNo.getText().toString());
        Uri uri = null;
        try{
            uri = getContentResolver().insert(StockEntry.CONTENT_URI,values);
        }catch (IllegalArgumentException e){
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        if(uri == null){
            Log.e("DB Error",getString(R.string.insertion_failed));
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(true);
        switch (item.getItemId()){
            case R.id.saveMenu:{
                //SAVE THE DATA
                if(insertData()){
                    finish();
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
