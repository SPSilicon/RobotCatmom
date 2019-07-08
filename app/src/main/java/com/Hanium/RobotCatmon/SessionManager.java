package com.Hanium.RobotCatmon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class SessionManager {
    SharedPreferences mSharedPreferences;


    public Context mContext;
    public SharedPreferences.Editor mEditor;



    private String mUserID;


    private String mDeviceID;

    public String getUserID() {
        return mUserID;
    }
    public String getDeviceID() {
        return mDeviceID;
    }
    public SessionManager(@org.jetbrains.annotations.NotNull Context context)
    {
        mContext = context;
        mSharedPreferences = context.getSharedPreferences("LOGIN",Context.MODE_PRIVATE);

    }

    public boolean checkSession()
    {
        if(mSharedPreferences.getBoolean("IS_LOGIN",false))
        {
            mEditor = mSharedPreferences.edit();
            mEditor.clear();
            mEditor.apply();
            return false;
        }
        else
        {

            mUserID = mSharedPreferences.getString("USER_ID",null);
            mDeviceID = mSharedPreferences.getString("DEVICE_ID",null);
            //웹서버에 USER_ID 와 DEVICE_ID 에 해당하는 유저가 있는지 체크

            Response.Listener<String> listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    Document html = Jsoup.parse(response);
                    String htmlResponse = html.body().text();
                    try
                    {
                        JSONObject jsonResponse = new JSONObject(htmlResponse);

                        boolean success = jsonResponse.getBoolean("success");
                        if(success)
                        {
                            // 정상적으로 로그인이 되어있는 상태이므로 아무것도 수행하지 않음
                        }
                        else {
                            //해당 USER_ID와 맞는 DEVICE_ID가 없으므로 초기화시킴
                            mEditor = mSharedPreferences.edit();
                            mEditor.clear();
                            mEditor.apply();
                        }
                    }
                    catch(JSONException e)
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("JSON 오류\n"+e.toString())
                                .setNegativeButton("확인",null)
                                .create()
                                .show();
                    }
                }
            };
            SessionCheckRequest sessionCheckRequest = new SessionCheckRequest(mUserID,mDeviceID,listener);
            RequestQueue queue = Volley.newRequestQueue(mContext);
            queue.add(sessionCheckRequest);

            return mSharedPreferences.getBoolean("IS_LOGIN", false);
            /*TODO: 웹서버에 해당하는 USER_ID 와 DEVICE_ID 가 있는지 체크
            * TODO: 웹서버는 한계정당 하나의 DEVICE_ID를 가지게 함
            * TODO: 로그인할때 이미 다른아이디에 존재하는 DEVICE_ID이면, 기존것을 삭제
            */
        }
    }

    public void createSession(String id, String deviceID)
    {
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean("IS_LOGIN",true);
        mEditor.putString("USER_ID", id);
        mEditor.putString("DEVICE_ID", deviceID);
        if(!mEditor.commit())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("세션 생성 오류")
                    .setNegativeButton("확인",null)
                    .create()
                    .show();
        }
    }

    public void deleteSession()
    {
        mEditor = mSharedPreferences.edit();
        mEditor.clear();
        if(!mEditor.commit())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("세션 삭제 오류")
                    .setNegativeButton("확인",null)
                    .create()
                    .show();
        }
        Intent intent = new Intent(mContext, MainActivity.class);
        mContext.startActivity(intent);
    }
}
