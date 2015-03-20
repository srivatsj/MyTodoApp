package com.example.sjayaram.mytodoapp.Models;

/**
 * Created by sjayaram on 3/17/2015.
 */
import android.database.Cursor;

import com.activeandroid.Cache;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "Items")
public class Item extends Model {

    // This is a regular field
    @Column(name = "Name")
    public String name;

    @Column(name = "Priority")
    public String priority;

    public Item(){
        super();
    }

    public Item(int remoteId, String name, String priority){
        super();
        this.name = name;
        this.priority = priority;
    }

    // Return cursor for result set for all todo items
    public static Cursor fetchResultCursor() {
        String tableName = Cache.getTableInfo(Item.class).getTableName();
        // Query all items without any conditions
        String resultRecords = new Select(tableName + ".*, " + tableName + ".Id as _id").
                from(Item.class).toSql();
        // Execute query on the underlying ActiveAndroid SQLite database
        Cursor resultCursor = Cache.openDatabase().rawQuery(resultRecords, null);
        return resultCursor;
    }


}
