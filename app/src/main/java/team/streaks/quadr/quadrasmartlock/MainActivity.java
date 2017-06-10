package team.streaks.quadr.quadrasmartlock;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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


public class MainActivity extends AppCompatActivity {

    public void auto() {
        final long period = 10000;
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
        setContentView(R.layout.activity_main);
        //showNotification();
        auto();
    }

    private void showNotification() {
        Intent intent = new Intent(this, Notification.class);
        startActivity(intent);

    }

    private void checkFingerPrintSensor() {
        FingerprintManager fingerprintManager = (FingerprintManager) getApplicationContext().getSystemService(Context.FINGERPRINT_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Application does not have permission to use fingerprint sensor. Open settings and allow permission to app", Toast.LENGTH_LONG).show();
        }
        if (!fingerprintManager.isHardwareDetected()) {
            Toast.makeText(getApplicationContext(), "Sorry, your phone does not have fingerprint sensor", Toast.LENGTH_LONG).show();
        } else if (!fingerprintManager.hasEnrolledFingerprints()) {
            Toast.makeText(getApplicationContext(), "Please register atleast one fingerprint in your phone", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Ready to make unlock", Toast.LENGTH_LONG).show();
            unlock(new View(this));
        }

    }

    public void unlock(View v) {
        Toast.makeText(getApplicationContext(), "Logic to unlock with verification", Toast.LENGTH_LONG).show();
    }

    public void performance(View v) {
        Intent intent = new Intent(getApplicationContext(), Browser.class);
        intent.putExtra("url", "http://thingspeak.com/channels/285246");
        intent.putExtra("name", "Quadra");
        startActivity(intent);
        /*

        Toast.makeText(getApplicationContext(),"Performance data will be shown here",Toast.LENGTH_LONG).show();
       Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
       startActivity(browserIntent);
      */
    }

    public void lock(View v) {
        Toast.makeText(getApplicationContext(), "Logic to lock", Toast.LENGTH_LONG).show();
    }

    public void changeName(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter your car name");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            String m_Text;

            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();

                ((TextView) findViewById(R.id.carName)).setText(m_Text.toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void onExit() {

    }

    public void showLight(View v) {
        Intent intent = new Intent(getApplicationContext(), Browser.class);
        intent.putExtra("url", "https://thingspeak.com/channels/285246/fields/field1/last");
        intent.putExtra("name", "Head Light Intensity");
        startActivity(intent);
    }

    public void showOil(View v) {
        Intent intent = new Intent(getApplicationContext(), Browser.class);
        intent.putExtra("url", "https://thingspeak.com/channels/285246/fields/field2/last");
        intent.putExtra("name", "Engine Oil level");
        startActivity(intent);
    }


    class JSONTask extends AsyncTask<String, String, String>

    {
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
                    new NotificationCompat.Builder(MainActivity.this);
//Test
            if (value >= 500) {
                builder.setContentTitle("Warning!");
                builder.setContentText("Change your car headlights");

                builder.setSmallIcon(R.drawable.icon);
                Intent notificationIntent = new Intent(MainActivity.this, MainActivity.class);
                PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, 0, notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(contentIntent);

                // Add as notification
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(0, builder.build());

            }
        }
    }
}
