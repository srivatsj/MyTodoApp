package com.example.sjayaram.mytodoapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TodoCursorAdapter extends CursorAdapter {
    public TodoCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_todo, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template

        TextView tvItemName = (TextView) view.findViewById(R.id.tvItemName);
        TextView tvItemPriority = (TextView) view.findViewById(R.id.tvItemPriority);

        // Extract properties from cursor
        String body = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
        //int priority = cursor.getInt(cursor.getColumnIndexOrThrow("Priority"));

        Date d = new Date(cursor.getLong(cursor.getColumnIndexOrThrow("DueDate")));

        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
        String date = DATE_FORMAT.format(d);

        // Populate fields with extracted properties
        tvItemName.setText(body);
        tvItemPriority.setText(date);
    }
}