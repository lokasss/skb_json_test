package fapp.bush.skb_json;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DateFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


import static fapp.bush.skb_json.R.id.status;

public class MainActivity extends AppCompatActivity {
   private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    // URL
    private static String url = "http://urdm.ru/media/img/ob-director.json";

    ArrayList<HashMap<String, String>> eventList;
    ArrayList<HashMap<String, String>> statusList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eventList = new ArrayList<>();

        lv = (ListView) findViewById(R.id.list);

        new GetContacts().execute();
    }

   private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Пожалуйста подождите...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            httpJson hJ = new httpJson();

            String jsonStr = hJ.makeServiceCall(url);

            Log.e(TAG, "Url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    JSONArray org = jsonObj.getJSONArray("events");

                    for (int i = 0; i < org.length(); i++) {
                        JSONObject c = org.getJSONObject(i);

                        String name = c.getString("name");
                        String period = c.getString("period");
                        String status = c.getString("status");
                        String date = c.getString("date");

                        HashMap<String, String> events = new HashMap<>();
                        events.put("name", name);
                        events.put("period", period);
                        events.put("status", status);
                        events.put("date", date);



                        eventList.add(events);

                    }

                } catch (final JSONException e) {
                    Log.e(TAG, "Error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json.",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();


           ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, eventList,
                    R.layout.row, new String[]{"date","name", "period","status"}, new int[]{R.id.date,R.id.name,
                    R.id.period, status});

            lv.setAdapter(adapter);
        }
    }
}
