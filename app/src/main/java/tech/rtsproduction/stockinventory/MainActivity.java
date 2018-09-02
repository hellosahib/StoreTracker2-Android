package tech.rtsproduction.stockinventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import tech.rtsproduction.stockinventory.Database.StockContract.StockEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //VARIABLES
    Toolbar toolbar;
    FloatingActionButton floatingButton;
    ListView inventoryListView;
    StockCursorAdapter adapter;
    public static final int DB_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //FIND VIEW BY ID'S
        toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        floatingButton = findViewById(R.id.floatingButtonMain);
        inventoryListView = findViewById(R.id.listviewMain);
        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        inventoryListView.setEmptyView(emptyView);

        adapter = new StockCursorAdapter(this, null);
        inventoryListView.setAdapter(adapter);
        getLoaderManager().initLoader(DB_LOADER, null, this);

        //ON CLICK LISTENER
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EditorActivity.class));
            }
        });

        inventoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent gotoEditor = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentUri = ContentUris.withAppendedId(StockEntry.CONTENT_URI, id);
                gotoEditor.setData(currentUri);
                startActivity(gotoEditor);
            }
        });
    }//OnCreate

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
            case R.id.deleteAllMenu: {
                deletePets();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }//OnOptionsItemSelected

    //CURSOR METHODS
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, StockEntry.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    //USER DEFINED FUNCTIONS
    public void addDummyData() {
        ContentValues values = new ContentValues();
        values.put(StockEntry.COLUMN_PRODUCT_NAME, "Jane Doe");
        values.put(StockEntry.COLUMN_PRODUCT_PRICE, 100);
        values.put(StockEntry.COLUMN_PRODUCT_QUANTITY, 250);
        values.put(StockEntry.COLUMN_PRODUCT_SUPPLIER_NAME, "RTS Production");
        values.put(StockEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, "9999990090");
        Uri uri = null;
        try {
            uri = getContentResolver().insert(StockEntry.CONTENT_URI, values);
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        if (uri == null) {
            Toast.makeText(this, R.string.insertion_failed, Toast.LENGTH_SHORT).show();
        }
    }//AddDummyData

    private void deletePets() {
        getContentResolver().delete(StockEntry.CONTENT_URI, null, null);
    }
}//MainActivity
