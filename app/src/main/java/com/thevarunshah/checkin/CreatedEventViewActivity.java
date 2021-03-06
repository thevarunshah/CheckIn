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
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.thevarunshah.backend.Backend;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class CreatedEventViewActivity extends AppCompatActivity{

    Bundle b;

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

        b = getIntent().getBundleExtra("bundle");
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

        Button scan = (Button) findViewById(R.id.check_in_button);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(CreatedEventViewActivity.this);
                scanIntegrator.initiateScan();
            }
        });
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
            Intent i = new Intent(CreatedEventViewActivity.this, AdminCheckInViewActivity.class);
            Bundle extra = new Bundle();
            extra.putInt("id", b.getInt("id"));
            i.putExtra("bundle", extra);
            startActivity(i);

        } catch(final IOException e){

            Log.d("Event View - Check in", e.toString());
        }
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
