package com.thevarunshah.checkin;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.thevarunshah.backend.Backend;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class CreatedEventViewActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.created_event_view_activity);

        TextView nameTV = (TextView) findViewById(R.id.name_view);
        TextView dateTV = (TextView) findViewById(R.id.date_view);
        TextView timeTV = (TextView) findViewById(R.id.time_view);
        TextView descriptionTV = (TextView) findViewById(R.id.description_view);

        final Bundle b = getIntent().getBundleExtra("bundle");
        nameTV.setText(b.getString("name"));
        dateTV.setText(b.getString("date"));
        timeTV.setText(b.getString("time"));
        descriptionTV.setText(b.getString("description"));

        Button leave = (Button) findViewById(R.id.delete_button);
        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doHttpPost(b.getInt("id"));
            }
        });
    }

    private void doHttpPost(int id){

        URL url;
        String param;
        HttpURLConnection conn;

        try{

            url = new URL(Backend.baseURL + "/events/delete/" + id);
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

            Log.d("Event View - Delete", response);
            Intent i = new Intent(CreatedEventViewActivity.this, YourEventsActivity.class);
            startActivity(i);

        } catch(final IOException e){

            Log.d("Event View - Delete", e.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_view_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.all_events:
                Intent i = new Intent(CreatedEventViewActivity.this, EventsActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
