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

public class EventsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_activity);

        String eventsListResponse = doHttpPost();
        final ArrayList<Event> events = new ArrayList<Event>();
        try {
            JSONObject eventsJSONResponse = new JSONObject(eventsListResponse);
            JSONArray response = eventsJSONResponse.getJSONArray("not_registered");
            for(int i = 0; i < response.length(); i++){
                JSONObject jsonObject = response.getJSONObject(i);
                int id = jsonObject.getInt("id");
                String adminId = jsonObject.getString("user_id");
                String name = jsonObject.getString("name");
                String bustime = jsonObject.getString("bustime");
                String description = jsonObject.getString("description");
                Event e = new Event(id, adminId, name, bustime, description);
                events.add(e);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListView lv = (ListView) findViewById(R.id.events_listview);
        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
        for(Event e : events) {
            HashMap<String, String> datum = new HashMap<String, String>();
            datum.put("EventName", e.name);
            datum.put("EventDate", e.date);
            data.add(datum);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2,
                new String[] {"EventName", "EventDate"}, new int[] {android.R.id.text1, android.R.id.text2});
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Event e = events.get(position);
                Intent i = new Intent(EventsActivity.this, EventViewActivity.class);
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

            Log.d("Events", response);
            return response;

        } catch(final IOException e){

            Log.d("Events", e.toString());
            return "";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.events_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.your_events:
                Intent i = new Intent(EventsActivity.this, YourEventsActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    private static String HttpGet(String url){

        try{

            HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
            InputStream is = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            String line = "";
            String response = "";
            while ((line = rd.readLine()) != null){
                response += line;
            }

            return response;

        } catch(Exception e){

            Log.i("Events", e.toString());
        }

        return null;
    }
    */
}
