package com.thevarunshah.checkin;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.thevarunshah.backend.AttendeeAdapter;
import com.thevarunshah.backend.Backend;
import com.thevarunshah.backend.Event;
import com.thevarunshah.backend.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class AdminCheckInViewActivity extends AppCompatActivity{

    Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_checkin_view);

        b = getIntent().getBundleExtra("bundle");

        String eventsListResponse = doHttpPost(b.getInt("id"));
        final ArrayList<User> users = new ArrayList<User>();
        try {
            JSONObject eventsJSONResponse = new JSONObject(eventsListResponse);
            JSONObject mainResponse = eventsJSONResponse.getJSONObject("response");
            JSONArray response = mainResponse.getJSONArray("attendees");
            for (int i = 0; i < response.length(); i++) {
                JSONObject jsonObject = response.getJSONObject(i);
                int id = jsonObject.getInt("id");
                String name = jsonObject.getString("name");
                JSONObject pivot = jsonObject.getJSONObject("pivot");
                String checkedIn = pivot.getString("has_checked_in");
                User u;
                if(checkedIn.equals("0")){
                    u = new User(id, name, false);
                }
                else{
                    u = new User(id, name, true);
                }
                users.add(u);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListView lv = (ListView) findViewById(R.id.admin_listview);
        AttendeeAdapter listAdapter = new AttendeeAdapter(this, users);
        lv.setAdapter(listAdapter);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT"); // This will contain your scan result
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
            }
        }
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            //we have a result
            String scanContent = scanningResult.getContents();
            //String scanFormat = scanningResult.getFormatName();
            doHttpPost(b.getInt("id"), scanContent);
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void doHttpPost(int id, String rider_token){

        URL url;
        String param;
        HttpURLConnection conn;

        try{

            url = new URL(Backend.baseURL + "/events/checkin/" + id);
            param = "token=" + URLEncoder.encode(Backend.token, "UTF-8") + "&rider_token=" + URLEncoder.encode(rider_token, "UTF-8") ;

            conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("PUT");
            conn.setFixedLengthStreamingMode(param.getBytes().length);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.close();

            String response = "";
            Scanner inStream = new Scanner(conn.getInputStream());
            while(inStream.hasNextLine()) {
                response += (inStream.nextLine());
            }

            Log.d("Event View - Check in", response);
            Intent i = new Intent(AdminCheckInViewActivity.this, AdminCheckInViewActivity.class);
            Bundle extra = new Bundle();
            extra.putInt("id", b.getInt("id"));
            i.putExtra("bundle", extra);
            startActivity(i);

        } catch(final IOException e){

            Log.d("Event View - Check in", e.toString());
        }
    }

    private String doHttpPost(int id){

        URL url;
        String param;
        HttpURLConnection conn;

        try{

            url = new URL(Backend.baseURL + "/events/" + id + "/details");
            param = "token=" + URLEncoder.encode(Backend.token, "UTF-8");

            conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setFixedLengthStreamingMode(param.getBytes().length);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.close();

            String response = "";
            Scanner inStream = new Scanner(conn.getInputStream());
            while(inStream.hasNextLine()) {
                response += (inStream.nextLine());
            }

            Log.d("Admin View", response);
            return response;

        } catch(final IOException e){

            Log.d("Admin View", e.toString());
            return "";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_view_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.start_check_in:
                IntentIntegrator scanIntegrator = new IntentIntegrator(AdminCheckInViewActivity.this);
                scanIntegrator.initiateScan();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
