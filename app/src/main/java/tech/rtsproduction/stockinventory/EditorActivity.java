package tech.rtsproduction.stockinventory;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import tech.rtsproduction.stockinventory.Database.DBHelper;
import tech.rtsproduction.stockinventory.Database.StockContract.StockEntry;

public class EditorActivity extends AppCompatActivity {

    //VARIABLES
    Toolbar toolbarEditor;
    TextInputEditText pName,pPrice,pQuantity,pSupplierName,pSupplierNo;
    DBHelper dbHelper;

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
        dbHelper = new DBHelper(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu_editor,menu);
        return true;
    }

    public void insertData(SQLiteDatabase db){
        //INSERT DATA FROM EDIT TEXTS'S AND PUSH THEM TO DB
        ContentValues values = new ContentValues();
        values.put(StockEntry.COLUMN_PRODUCT_NAME,pName.getText().toString());
        values.put(StockEntry.COLUMN_PRODUCT_PRICE,pPrice.getText().toString());
        values.put(StockEntry.COLUMN_PRODUCT_QUANTITY,pQuantity.getText().toString());
        values.put(StockEntry.COLUMN_PRODUCT_SUPPLIER_NAME,pSupplierName.getText().toString());
        values.put(StockEntry.COLUMN_PRODUCT_SUPPLIER_PHONE,pSupplierNo.getText().toString());
        db.insert(StockEntry.TABLE_NAME,null,values);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(true);
        switch (item.getItemId()){
            case R.id.saveMenu:{
                //SAVE THE DATA
                insertData(dbHelper.getWritableDatabase());
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
