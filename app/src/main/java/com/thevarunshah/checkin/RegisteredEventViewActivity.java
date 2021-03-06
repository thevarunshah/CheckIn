package com.thevarunshah.checkin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.thevarunshah.backend.Backend;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class RegisteredEventViewActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registered_event_view_activity);

        TextView nameTV = (TextView) findViewById(R.id.name_view);
        TextView dateTV = (TextView) findViewById(R.id.date_view);
        TextView timeTV = (TextView) findViewById(R.id.time_view);
        TextView descriptionTV = (TextView) findViewById(R.id.description_view);

        final Bundle b = getIntent().getBundleExtra("bundle");
        nameTV.setText(b.getString("name"));
        dateTV.setText(b.getString("date"));
        timeTV.setText(b.getString("time"));
        descriptionTV.setText(b.getString("description"));

        Button leave = (Button) findViewById(R.id.leave_button);
        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doHttpPost(b.getInt("id"));
            }
        });

        Button showQRCode = (Button) findViewById(R.id.view_qr_code_button);
        showQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateQRCode(v);
            }
        });
    }

    public  void generateQRCode(View view){

        QRCodeWriter writer=new QRCodeWriter();
        try{

            BitMatrix bitMatrix = writer.encode(Backend.token, BarcodeFormat.QR_CODE, 720, 720);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            ((ImageView) findViewById(R.id.qr_imageview)).setImageBitmap(bmp);
        }
        catch (WriterException e) {

        }
    }

    private void doHttpPost(int id){

        URL url;
        String param;
        HttpURLConnection conn;

        try{

            url = new URL(Backend.baseURL + "/events/deregister/" + id);
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

            Log.d("Event View - Leave", response);
            Intent i = new Intent(RegisteredEventViewActivity.this, YourEventsActivity.class);
            startActivity(i);

        } catch(final IOException e){

            Log.d("Event View - Leave", e.toString());
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
                Intent i = new Intent(RegisteredEventViewActivity.this, EventsActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
