package com.example.sjayaram.mytodoapp;

import android.database.Cursor;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.example.sjayaram.mytodoapp.Models.Item;

import java.util.Date;


public class MainActivity extends ActionBarActivity implements AddEditItemDialog.AddEditNameDialogListener {

    private TodoCursorAdapter todoAdapter;
    private ListView lvItems;
    private Item selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvItems = (ListView)findViewById(R.id.lvItems);

        Cursor todoCursor = Item.fetchResultCursor();
        todoAdapter = new TodoCursorAdapter(this, todoCursor);
        lvItems.setAdapter(todoAdapter);

        setUpListViewListener();
        setUpListEditListener();
    }

    private void setUpListViewListener() {
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                int itemId = cursor.getInt(cursor.getColumnIndexOrThrow("Id"));
                Item item = Item.load(Item.class, itemId);
                item.delete();
                todoAdapter.changeCursor(Item.fetchResultCursor());
                Toast.makeText(getApplicationContext(), "Item Deleted", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }

    private void setUpListEditListener() {
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
                int itemId = cursor.getInt(cursor.getColumnIndexOrThrow("Id"));
                selectedItem = Item.load(Item.class, itemId);
                showEditDialog();
            }
        });
    }

    public Item getSelectedItem() {
        return selectedItem;
    }

    public void onAddItem(View v)
    {
        selectedItem = null;
        showAddDialog();
    }

    private void showAddDialog() {
        FragmentManager fm = getSupportFragmentManager();
        AddEditItemDialog addEditItemDialog = AddEditItemDialog.newInstance("Add Todo Item");
        addEditItemDialog.show(fm, "fragment_add_edit_item");
    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        AddEditItemDialog addEditItemDialog = AddEditItemDialog.newInstance("Edit Todo Item");
        addEditItemDialog.show(fm, "fragment_add_edit_item");
    }

    @Override
    public void onFinishAddEditDialog(String itemName, String priority, Date date) {
        if(selectedItem==null)
        {
            Item newItem = new Item();
            newItem.name = itemName;
            newItem.priority = priority;
            newItem.dueDate = date;
            newItem.save();
        }
        else
        {
            selectedItem.name =  itemName;
            selectedItem.priority = priority;
            selectedItem.dueDate = date;
            selectedItem.save();
        }

        todoAdapter.changeCursor(Item.fetchResultCursor());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
