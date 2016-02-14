package com.thevarunshah.checkin;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.thevarunshah.backend.Backend;
import com.thevarunshah.backend.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class YourEventsActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.your_events_activity);

        String eventsListResponse = doHttpPost();
        final ArrayList<Event> registeredEvents = new ArrayList<Event>();
        final ArrayList<Event> createdEvents = new ArrayList<Event>();
        try {
            JSONObject eventsJSONResponse = new JSONObject(eventsListResponse);
            JSONObject responseObject = eventsJSONResponse.getJSONObject("response");
            JSONArray registered = responseObject.getJSONArray("registered");
            for(int i = 0; i < registered.length(); i++){
                JSONObject jsonObject = registered.getJSONObject(i);
                int id = jsonObject.getInt("id");
                String adminId = jsonObject.getString("user_id");
                String name = jsonObject.getString("name");
                String bustime = jsonObject.getString("bustime");
                String description = jsonObject.getString("description");
                Event e = new Event(id, adminId, name, bustime, description);
                registeredEvents.add(e);
            }
            JSONArray created = responseObject.getJSONArray("events");
            for(int i = 0; i < created.length(); i++){
                JSONObject jsonObject = created.getJSONObject(i);
                int id = jsonObject.getInt("id");
                String adminId = jsonObject.getString("user_id");
                String name = jsonObject.getString("name");
                String bustime = jsonObject.getString("bustime");
                String description = jsonObject.getString("description");
                Event e = new Event(id, adminId, name, bustime, description);
                createdEvents.add(e);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListView registeredLV = (ListView) findViewById(R.id.registered_events_listview);
        ArrayList<HashMap<String, String>> registeredData = new ArrayList<HashMap<String, String>>();
        for(Event e : registeredEvents) {
            HashMap<String, String> datum = new HashMap<String, String>();
            datum.put("EventName", e.name);
            datum.put("EventDate", e.date);
            registeredData.add(datum);
        }
        SimpleAdapter registeredAdapter = new SimpleAdapter(this, registeredData, android.R.layout.simple_list_item_2,
                new String[] {"EventName", "EventDate"}, new int[] {android.R.id.text1, android.R.id.text2});
        registeredLV.setAdapter(registeredAdapter);
        registeredLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Event e = registeredEvents.get(position);
                Intent i = new Intent(YourEventsActivity.this, EventViewActivity.class);
                Bundle extra = new Bundle();
                extra.putInt("id", e.id);
                extra.putString("name", e.name);
                extra.putString("date", e.date);
                extra.putString("time", e.time);
                extra.putString("description", e.description);
                i.putExtra("bundle", extra);
                startActivity(i);
            }
        });

        ListView createdLV = (ListView) findViewById(R.id.created_events_listview);
        ArrayList<HashMap<String, String>> createdData = new ArrayList<HashMap<String, String>>();
        for(Event e : createdEvents) {
            HashMap<String, String> datum = new HashMap<String, String>();
            datum.put("EventName", e.name);
            datum.put("EventDate", e.date);
            createdData.add(datum);
        }
        SimpleAdapter createdAdapter = new SimpleAdapter(this, createdData, android.R.layout.simple_list_item_2,
                new String[] {"EventName", "EventDate"}, new int[] {android.R.id.text1, android.R.id.text2});
        createdLV.setAdapter(createdAdapter);
        createdLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Event e = createdEvents.get(position);
                Intent i = new Intent(YourEventsActivity.this, EventViewActivity.class);
                Bundle extra = new Bundle();
                extra.putInt("id", e.id);
                extra.putString("name", e.name);
                extra.putString("date", e.date);
                extra.putString("time", e.time);
                extra.putString("description", e.description);
                i.putExtra("bundle", extra);
                startActivity(i);
            }
        });
    }

    private String doHttpPost(){

        URL url;
        HttpURLConnection conn;

        try{

            url = new URL(Backend.baseURL + "/user/events?token=" + Backend.token);

            conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String response = "";
            Scanner inStream = new Scanner(conn.getInputStream());
            while(inStream.hasNextLine()) {
                response += (inStream.nextLine());
            }

            Log.d("Your Events", response);
            return response;

        } catch(final IOException e){

            Log.d("Your Events", e.toString());
            return "";
        }
    }
}
