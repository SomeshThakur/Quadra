package team.streaks.quadr.quadrasmartlock;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;


public class Notification extends AppCompatActivity {

    public void auto() {
        final long period = 10;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // do your task here
                new JSONTask().execute("https://thingspeak.com/channels/254941/feed.json");
            }
        }, 0, period);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        auto();

    }

    class JSONTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            StringBuffer buffer = new StringBuffer();
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            URL url = null;
            try {
                url = new URL(params[0]);

                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                String line = "";
                buffer = new StringBuffer();
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                String finalJson = buffer.toString();
                JSONObject parentobject = new JSONObject(finalJson);
                JSONArray parentarray = parentobject.getJSONArray("feeds");
                int i = parentarray.length() - 1;

                JSONObject finalobject = parentarray.getJSONObject(i);
                String val = finalobject.getString("field1");
                return val;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            int value = Integer.parseInt(result);
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(Notification.this);

            if (value >= 150) {
                builder.setContentTitle("Warning!");
                builder.setContentText("Change your car headlights");

            } else {
                builder.setContentTitle("Information");
                builder.setContentText("No need to worry! Your car headlight voltage is below 150");

            }
            builder.setSmallIcon(R.drawable.icon);
            Intent notificationIntent = new Intent(Notification.this, Notification.class);
            PendingIntent contentIntent = PendingIntent.getActivity(Notification.this, 0, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);

            // Add as notification
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());

        }
    }
}