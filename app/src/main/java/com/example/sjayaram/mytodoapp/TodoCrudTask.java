package com.example.sjayaram.mytodoapp;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import com.example.sjayaram.mytodoapp.Models.JsonItem;
import com.example.sjayaram.mytodoapp.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.example.sjayaram.mytodoapp.Models.JsonItem;

/**
 * Created by sjayaram on 4/2/2015.
 */
public class TodoCrudTask extends AsyncTask<String, Void, List<JsonItem> >{

    public interface AsyncResponse {
        void processFinish(List<JsonItem> list);
    }

    private Exception exception;
    private JsonItem jsonItem;
    private String method;
    private String serverURL = "http://10.0.2.2:8080/api/todos";
    public AsyncResponse delegate = null;//Call back interface

    public TodoCrudTask(String method, JsonItem jsonItem, AsyncResponse asyncResponse){
        this.jsonItem = jsonItem;
        this.method = method;
        delegate = asyncResponse;//Assigning call back interfacethrough constructor
    }

    protected List<JsonItem> doInBackground(String... params)
    {
        String name = jsonItem.name;
        String priority = jsonItem.priority;
        Date dueDate = jsonItem.dueDate;
        String userId = jsonItem.userId;
        String id = jsonItem.id;

        List<JsonItem> myList = new ArrayList<>();
        HttpResponse response = null;

        try {

            HttpClient httpclient = new DefaultHttpClient();

            if("Add".equals(method))
            {
                HttpPost httpPost = new HttpPost(serverURL);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("name", name));
                nameValuePairs.add(new BasicNameValuePair("priority", priority));
                nameValuePairs.add(new BasicNameValuePair("dueDate", dueDate.toString()));
                nameValuePairs.add(new BasicNameValuePair("userId", userId));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                httpclient.execute(httpPost);
            }
            else if("Edit".equals(method))
            {
                HttpPut httpPut = new HttpPut(serverURL + "/" + id);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("name", name));
                nameValuePairs.add(new BasicNameValuePair("priority", priority));
                nameValuePairs.add(new BasicNameValuePair("dueDate", dueDate.toString()));
                httpPut.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                httpclient.execute(httpPut);
            }
            else if("Delete".equals(method))
            {
                HttpDelete httpDelete = new HttpDelete(serverURL + "/" + id);
                httpclient.execute(httpDelete);
            }

            HttpGet httpGet = new HttpGet(serverURL + "/" + userId);
            response = httpclient.execute(httpGet);

            myList = readItems(response);


        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return myList;
    }

    private List<JsonItem> readItems(HttpResponse response) throws Exception
    {
        List<JsonItem> myList = new ArrayList<>();
        InputStream inputStream = null;
        StringBuilder sb = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
        String line = null;

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        System.out.println("finalResult " + sb.toString());

        JSONArray todos = new JSONArray(sb.toString());

        //Loop the Array
        for (int i = 0; i < todos.length(); i++) {

            HashMap<String, String> map = new HashMap<String, String>();
            JSONObject e = todos.getJSONObject(i);

            JsonItem jsonItem1 = new JsonItem();
            jsonItem1.id = e.getString("_id");
            jsonItem1.name = e.getString("name");
            jsonItem1.priority = e.getString("priority");


            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            Date result1 = df1.parse(e.getString("dueDate"));

            jsonItem1.dueDate = result1;
            jsonItem1.userId = e.getString("userId");

            myList.add(jsonItem1);
        }

        return myList;

    }

    protected void onPostExecute(List<JsonItem> result) {
        delegate.processFinish(result);
        Log.i("Result after add" , result.toString());
    }


}
