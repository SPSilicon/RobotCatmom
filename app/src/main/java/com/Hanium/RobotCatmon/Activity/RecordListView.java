package com.Hanium.RobotCatmon.Activity;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.Hanium.RobotCatmon.R;
import com.Hanium.RobotCatmon.Requests.RecordRequest;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class RecordListView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list_view);

        Intent intent = getIntent();
        String rocatid = intent.getStringExtra("rocatId");
        final LinearLayout contentList = findViewById(R.id.ListContent);
        final ScrollView scrollView = findViewById(R.id.scrollView2);
        LinearLayout listHead = findViewById(R.id.listHead);



        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Document html = Jsoup.parse(response);
                String htmlResponse = html.body().text();
                try {

                    JSONArray jsonResponse = new JSONArray(htmlResponse);

                    if (jsonResponse.length() > 0) {
                        for (int i = 0; i < jsonResponse.length(); i++) {
                            LinearLayout content = new LinearLayout(RecordListView.this);
                            content.setOrientation(LinearLayout.HORIZONTAL);

                            TextView id = new TextView(RecordListView.this);
                            id.setText(jsonResponse.getJSONObject(i).getString("id") + "\t\t");
                            id.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,0.5f));
                            id.setPadding(50, 0, 0, 0);

                            content.addView(id);

                            Button gps = new Button(RecordListView.this);
                            gps.setText(jsonResponse.getJSONObject(i).getString("gps_la") + " , " + jsonResponse.getJSONObject(i).getString("gps_lo") + "\t\t");
                            gps.setLayoutParams(new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,1f));
                            gps.setPadding(50, 0, 0, 0);
                            double lat = Double.parseDouble(jsonResponse.getJSONObject(i).getString("gps_la"));
                            double lng = Double.parseDouble(jsonResponse.getJSONObject(i).getString("gps_lo"));
                            AddGpsButtonListener(gps,lat,lng);
                            content.addView(gps);

                            TextView time = new TextView(RecordListView.this);
                            time.setText(jsonResponse.getJSONObject(i).getString("date"));
                            time.setLayoutParams(new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,1.5f));
                            time.setPadding(50, 0, 0, 0);
                            content.addView(time);

                            contentList.addView(content);
                        }
                        scrollView.invalidate();
                        scrollView.requestLayout();
                    }

                } catch (JSONException e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecordListView.this);
                    builder.setMessage("리스트 불러오기 오류." + htmlResponse)
                            .setNegativeButton("확인", null)
                            .create()
                            .show();
                }

            }
        };
        RecordRequest recordRequest = new RecordRequest(rocatid, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(recordRequest);

    }

    private void AddGpsButtonListener(Button button, final double lat, final double lng)
    {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gpsMapIntent = new Intent(RecordListView.this, RocatMap.class);
                gpsMapIntent.putExtra("latitude",lat);
                gpsMapIntent.putExtra("longitude",lng);

                RecordListView.this.startActivity(gpsMapIntent);
            }
        });
    }

}
