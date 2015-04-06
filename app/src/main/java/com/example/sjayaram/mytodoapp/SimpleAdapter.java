package com.example.sjayaram.mytodoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.sjayaram.mytodoapp.Models.JsonItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sjayaram on 4/2/2015.
 */
public class SimpleAdapter extends ArrayAdapter<JsonItem> {

    private List<JsonItem> itemList;
    private Context context;

    public SimpleAdapter(List<JsonItem> itemList, Context ctx) {
        super(ctx, android.R.layout.simple_list_item_1, itemList);
        this.itemList = itemList;
        this.context = ctx;
    }

    public int getCount() {
        if (itemList != null)
            return itemList.size();
        return 0;
    }

    public JsonItem getItem(int position) {
        if (itemList != null)
            return itemList.get(position);
        return null;
    }

    public long getItemId(int position) {
        if (itemList != null)
            return itemList.get(position).hashCode();
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_todo, null);
        }

        JsonItem c = itemList.get(position);

        TextView tvItemName = (TextView) view.findViewById(R.id.tvItemName);
        TextView tvItemPriority = (TextView) view.findViewById(R.id.tvItemPriority);

        tvItemName.setText(c.name);

        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
        String date = DATE_FORMAT.format(c.dueDate);

        tvItemPriority.setText(date);

        return view;

    }

    public List<JsonItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<JsonItem> itemList) {
        this.itemList = itemList;
    }
}
