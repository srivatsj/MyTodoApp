package com.example.sjayaram.mytodoapp.Models;

import java.util.Date;

/**
 * Created by sjayaram on 4/2/2015.
 */
public class JsonItem {

    public String id;

    public String userId;

    public String name;

    public String priority;

    public Date dueDate;

    public JsonItem(){}

    public JsonItem( String name, String priority, Date date, String userId){
        this.name = name;
        this.priority = priority;
        this.dueDate = date;
        this.userId = userId;
    }
}
