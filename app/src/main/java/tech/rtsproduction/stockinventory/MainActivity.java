package tech.rtsproduction.stockinventory;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import tech.rtsproduction.stockinventory.Database.StockContract.StockEntry;

public class MainActivity extends AppCompatActivity {

    //VARIABLES
    Toolbar toolbar;
    FloatingActionButton floatingButton;
    ListView inventoryListView;
    ArrayList<String> stockData;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //FIND VIEW BY ID'S
        toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        floatingButton = findViewById(R.id.floatingButtonMain);
        inventoryListView = findViewById(R.id.listviewMain);
        //INIT
        stockData = new ArrayList<>();
        //ON CLICK LISTENER
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EditorActivity.class));
            }
        });
    }//OnCreate

    @Override
    protected void onStart() {
        super.onStart();
        //CLEARING ANY PREVIOUS DATA
        stockData.clear();
        //RE-POPULATING DATA
        getData();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stockData);
        inventoryListView.setAdapter(adapter);
    }//OnStart


    /** Target API IS FOR QUERY METHOD WHICH IS ONLY VALID ON SDK 26 AND ABOVE */
    @TargetApi(26)
    public void getData() {
        Cursor cursor = getContentResolver().query(StockEntry.CONTENT_URI,null,null,null);
        while (cursor.moveToNext()) {
            stockData.add("Product name " + cursor.getString(cursor.getColumnIndex(StockEntry.COLUMN_PRODUCT_NAME)) + " Product Quantity " + cursor.getString(cursor.getColumnIndex(StockEntry.COLUMN_PRODUCT_QUANTITY)));
        }
        cursor.close();
    }//GetData

    public void addDummyData() {
        ContentValues values = new ContentValues();
        values.put(StockEntry.COLUMN_PRODUCT_NAME, "Jane Doe");
        values.put(StockEntry.COLUMN_PRODUCT_PRICE, 100);
        values.put(StockEntry.COLUMN_PRODUCT_QUANTITY, 250);
        values.put(StockEntry.COLUMN_PRODUCT_SUPPLIER_NAME, "RTS Production");
        values.put(StockEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, "9999990090");
        Uri uri = null;
        try{
            uri = getContentResolver().insert(StockEntry.CONTENT_URI,values);
        } catch (IllegalArgumentException e){
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        if(uri == null){
            Toast.makeText(this,R.string.insertion_failed, Toast.LENGTH_SHORT).show();
        }
    }//AddDummyData


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.context_menu_main, menu);
        return true;
    }//OnCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addDummyMenu: {
                addDummyData();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }//OnOptionsItemSelected
}//MainActivity
