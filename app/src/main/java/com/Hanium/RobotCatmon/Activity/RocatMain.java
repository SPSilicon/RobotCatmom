package com.Hanium.RobotCatmon.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.Hanium.RobotCatmon.R;
import com.Hanium.RobotCatmon.Requests.GetSubsStatRequest;
import com.Hanium.RobotCatmon.Requests.RegisterInstanceIdRequest;
import com.Hanium.RobotCatmon.Requests.RocatListRequest;
import com.Hanium.RobotCatmon.Requests.RocatSubsRequest;
import com.Hanium.RobotCatmon.Session.SessionManager;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

public class RocatMain extends AppCompatActivity {

    private static final String TAG = "RocatMain";
    private Spinner rocatSelect;
    private TextView welcomeText;
    private Button recordButton;
    private Button logoutButton;
    private Switch notiSwitch;
    private String username;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rocat_main);

        Intent intent = getIntent();
        username = intent.getStringExtra("userid");
        sessionManager = new SessionManager(RocatMain.this);


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        //세션이 없는 경우 유저이름과 deviceID를 웹서버에 저장함
                        // TODO : device_id(instance_ID) 가 변경되는 경우도 있으니 그 경우에 행동을 정의하자
                        if(!sessionManager.checkSession())
                        {
                            registerInstanceId(username, token);
                            sessionManager.createSession(username, token);
                        }
                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                    }
                });


        welcomeText = (TextView)findViewById(R.id.welcomeText);
        String message = "환영합니다 " + username +"님!";
        welcomeText.setText(message);

        notiSwitch = findViewById(R.id.push);
        setnotiSwitchAction();

        rocatSelect = (Spinner) findViewById(R.id.rocatid);
        // RocatMain 진입시 데이터베이스에서 rocatid들을 spinner로 가져옴
        setRocatSelect();
        setRocatSelectAction();

        recordButton = findViewById(R.id.recordButton);
        setRecordButton();

        logoutButton = findViewById(R.id.logoutButton);
        setLogoutButton();


        /*TODO : 접속할때에 db에서 푸시알람 구독을 하고있는지 확인하여 스위치 on/off **complete**
          TODO 2: spinner를 선택할 때마다 구독정보 갱신하여 스위치 on/off **complete**
          TODO 3: 웹서버에서 푸시알림 호출하기(php)
        */
    }
    void setLogoutButton()
    {
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sessionManager.deleteSession())
                {
                    Intent intent = new Intent(RocatMain.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }
    /*
            spinner, Button, switch 등을 상속하는 rocat~ 클래스를 만들어서 멤버메소드로 넣어둘 수 있을까?
     */
    void registerInstanceId(String username, String instanceId)
    {
        Response.Listener<String> responseListener= new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Document html = Jsoup.parse(response);
                String htmlResponse = html.body().text();
                try
                {
                    JSONObject jsonResponce = new JSONObject(htmlResponse);
                    if(jsonResponce.getBoolean("success"))
                    {
                        //db에 fcm instance 값 저장 성공
                    }
                    else
                    {
                        //db에 fcm instance 값 저장 실패
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    AlertDialog.Builder builder = new AlertDialog.Builder(RocatMain.this);
                    builder.setMessage(e.getStackTrace().toString())
                            .setNegativeButton("확인",null)
                            .create()
                            .show();
                }
            }
        };
        RegisterInstanceIdRequest registerInstanceIdRequest = new RegisterInstanceIdRequest(username, instanceId, responseListener);
        RequestQueue queue = Volley.newRequestQueue(RocatMain.this);
        queue.add(registerInstanceIdRequest);
    }

    void setRocatSelect()
    {
        Response.Listener<String> responseListener= new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                Document html = Jsoup.parse(response);
                String htmlResponse = html.body().text();
                try
                {
                    JSONArray jsonResponse = new JSONArray(htmlResponse);
                    ArrayList rocatList = new ArrayList<String>();
                    if(jsonResponse.length() > 0)
                    {
                        for (int i = 0; i < jsonResponse.length(); i++)
                        {
                            rocatList.add(jsonResponse.getJSONObject(i).getString("id"));
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,rocatList);

                        rocatSelect.setAdapter(arrayAdapter);
                        //setnotiSwitch();
                    }
                    else
                    {

                    }


                }
                catch(JSONException e)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RocatMain.this);
                    builder.setMessage("리스트 불러오기 오류.")
                            .setNegativeButton("확인",null)
                            .create()
                            .show();
                }
            }
        };
        RocatListRequest rocatListRequest = new RocatListRequest(responseListener);
        RequestQueue queue = Volley.newRequestQueue(RocatMain.this);
        queue.add(rocatListRequest);
    }
    void setRocatSelectAction() {
        rocatSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String rocatId = (String)adapterView.getAdapter().getItem(i);

                Response.Listener<String> responseLisnter = new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        Document html = Jsoup.parse(response);
                        String htmlResponse = html.body().text();
                        try
                        {
                            JSONObject jsonResponce = new JSONObject(htmlResponse);
                            if(jsonResponce.getBoolean("on"))
                            {
                                notiSwitch.setChecked(true);
                                Log.println(Log.INFO,TAG,htmlResponse);
                            }
                            else
                            {
                                notiSwitch.setChecked(false);
                                Log.println(Log.INFO,TAG,htmlResponse);
                            }
                        }
                        catch(Exception e)
                        {
                            Log.println(Log.INFO,TAG,htmlResponse);
                            e.printStackTrace();
                            AlertDialog.Builder builder = new AlertDialog.Builder(RocatMain.this);
                            builder.setMessage(htmlResponse)
                                    .setNegativeButton("확인",null)
                                    .create()
                                    .show();
                        }
                    }

                };
                GetSubsStatRequest getSubsStatRequest = new GetSubsStatRequest(username,rocatId,responseLisnter);
                RequestQueue queue = Volley.newRequestQueue(RocatMain.this);
                queue.add(getSubsStatRequest); }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    void setRecordButton()
    {
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RocatMain.this, RecordListView.class);
                intent.putExtra("rocatId",rocatSelect.getSelectedItem().toString());
                RocatMain.this.startActivity(intent);
            }
        });
    }
    void setnotiSwitch()
    {
        Response.Listener<String> responseLisnter = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                Document html = Jsoup.parse(response);
                String htmlResponse = html.body().text();
                try
                {
                    JSONObject jsonResponce = new JSONObject(htmlResponse);
                    if(jsonResponce.getBoolean("on"))
                    {
                        notiSwitch.setChecked(true);
                    }
                    else
                    {
                        notiSwitch.setChecked(false);
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    AlertDialog.Builder builder = new AlertDialog.Builder(RocatMain.this);
                    builder.setMessage(htmlResponse)
                            .setNegativeButton("확인",null)
                            .create()
                            .show();
                }
            }

        };
        GetSubsStatRequest getSubsStatRequest = new GetSubsStatRequest(username,rocatSelect.getSelectedItem().toString(),responseLisnter);
        RequestQueue queue = Volley.newRequestQueue(RocatMain.this);
        queue.add(getSubsStatRequest);

    }
    void setnotiSwitchAction()
    {
        notiSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = notiSwitch.isChecked();
                Response.Listener<String> responseLisnter = new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        Document html = Jsoup.parse(response);
                        String htmlResponse = html.body().text();
                        try
                        {
                            JSONObject jsonResponce = new JSONObject(htmlResponse);
                            if(jsonResponce.getBoolean("success"))
                            {
                                //특정 로켓 id에 대해 현제 아이디에 푸시알림 구독정보를 db에 저장 성공
                            }
                            else
                            {
                                //특정 로켓 id에 대해 현제 아이디에 푸시알림 구독정보를 db에 저장 실패
                            }
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                            AlertDialog.Builder builder = new AlertDialog.Builder(RocatMain.this);
                            builder.setMessage(htmlResponse)
                                    .setNegativeButton("확인",null)
                                    .create()
                                    .show();
                        }
                    }

                };
                RocatSubsRequest rocatSubsRequest = new RocatSubsRequest(username,rocatSelect.getSelectedItem().toString(),isChecked,responseLisnter);
                RequestQueue queue = Volley.newRequestQueue(RocatMain.this);
                queue.add(rocatSubsRequest);
            }

        });
    }

}
