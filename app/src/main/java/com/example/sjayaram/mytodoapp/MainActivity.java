package com.example.sjayaram.mytodoapp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.example.sjayaram.mytodoapp.Models.Item;
import com.example.sjayaram.mytodoapp.Models.JsonItem;
import com.facebook.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import com.example.sjayaram.mytodoapp.TodoCrudTask;


public class MainActivity extends ActionBarActivity implements AddEditItemDialog.AddEditNameDialogListener {

    private TodoCursorAdapter todoAdapter;
    private ListView lvItems;
    private JsonItem selectedItem;

    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static String SENDER_ID = "539211333012";
    private GoogleCloudMessaging gcm;
    private Context context;
    private static final String TAG = "Todo App";
    private String regId = "";
    private String userId;
    private String serverURL = "http://10.0.2.2:8080/api/";

    SimpleAdapter adapter;

    private List<JsonItem> mylist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String userName = getIntent().getStringExtra("userName");
        userId = getIntent().getStringExtra("userId");

        context = getApplicationContext();

        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regId = getRegistrationId(context);

            if (regId.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }

        Object actionBar = getSupportActionBar();
        android.support.v7.internal.app.WindowDecorActionBar bar = (android.support.v7.internal.app.WindowDecorActionBar) actionBar;

        bar.setTitle("Welcome " + userName);

        lvItems = (ListView)findViewById(R.id.lvItems);

        JsonItem item  = new JsonItem();
        item.userId = userId;

        TodoCrudTask asyncTask = new TodoCrudTask("Get", item, new TodoCrudTask.AsyncResponse() {

            @Override
            public void processFinish(List<JsonItem> list) {
                mylist = list;
                adapter.setItemList(mylist);
                adapter.notifyDataSetChanged();
            }

        });

        asyncTask.execute();

        adapter = new SimpleAdapter(mylist, this);
        lvItems.setAdapter(adapter);

        setUpListViewListener();
        setUpListEditListener();
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try
                {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regId;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                Log.i(TAG, "regid is " + msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i(TAG, msg);
            }
        }.execute(null, null, null);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend()
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(serverURL + "regId");

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("userId", userId));
            nameValuePairs.add(new BasicNameValuePair("deviceRegId", regId));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            Log.d("Result ", sb.toString());
        }catch(Exception e)
        {
            Log.d("InputStream", e.getLocalizedMessage());
        }
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    private void setUpListViewListener() {
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                JsonItem item = adapter.getItem(position);

                TodoCrudTask asyncTask = new TodoCrudTask("Delete", item, new TodoCrudTask.AsyncResponse() {

                    @Override
                    public void processFinish(List<JsonItem> list) {
                        Log.d("Response delete task:", list.toString());
                        adapter.setItemList(list);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "Item Deleted", Toast.LENGTH_SHORT).show();
                    }

                });

                asyncTask.execute();
                return true;
            }
        });

    }

    private void setUpListEditListener() {
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItem =  adapter.getItem(position);
                showEditDialog();
            }
        });
    }

    public JsonItem getSelectedItem() {
        return selectedItem;
    }

    public void onAddItem()
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
            JsonItem newItem = new JsonItem();
            newItem.name = itemName;
            newItem.priority = priority;
            newItem.dueDate = date;
            newItem.userId = userId;

            TodoCrudTask asyncTask = new TodoCrudTask("Add", newItem, new TodoCrudTask.AsyncResponse() {

                @Override
                public void processFinish(List<JsonItem> list) {
                    Log.d("Response Add task:", list.toString());
                    adapter.setItemList(list);
                    adapter.notifyDataSetChanged();
                }

            });

            asyncTask.execute();

        }
        else
        {
            selectedItem.name = itemName;
            selectedItem.priority = priority;
            selectedItem.dueDate = date;

            TodoCrudTask asyncTask = new TodoCrudTask("Edit", selectedItem, new TodoCrudTask.AsyncResponse() {

                @Override
                public void processFinish(List<JsonItem> list) {
                    Log.d("Response Edit task:", list.toString());
                    adapter.setItemList(list);
                    adapter.notifyDataSetChanged();
                    selectedItem = null;
                }

            });

            asyncTask.execute();
        }

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
            onAddItem();
            return true;
        }
        if (id == R.id.logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout(){
        // find the active session which can only be facebook in my app
        Session session = Session.getActiveSession();
        // run the closeAndClearTokenInformation which does the following
        // DOCS : Closes the local in-memory Session object and clears any persistent
        // cache related to the Session.
        session.closeAndClearTokenInformation();
        // return the user to the login screen
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        // make sure the user can not access the page after he/she is logged out
        // clear the activity stack
        finish();
    }

}
