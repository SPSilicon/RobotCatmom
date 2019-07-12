package com.Hanium.RobotCatmon.Session;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;

import com.Hanium.RobotCatmon.Requests.LogoutRequest;
import com.Hanium.RobotCatmon.Requests.SessionCheckRequest;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class SessionManager {
    SharedPreferences mSharedPreferences;

    private boolean mDelStatus;     //DeleteSession에서 반환을위한 변수
    private RequestQueue mQueue;
    public Context mContext;
    public SharedPreferences.Editor mEditor;


    public String getUserID() {
        return mSharedPreferences.getString("USER_ID",null);
    }
    public String getDeviceID() {
        return mSharedPreferences.getString("DEVICE_ID",null);
    }

    public SessionManager(@org.jetbrains.annotations.NotNull Context context)
    {
        mQueue = Volley.newRequestQueue(context);
        mContext = context;
        mSharedPreferences = context.getSharedPreferences("LOGIN",Context.MODE_PRIVATE);
    }

    public boolean checkSession()
    {
        if(!mSharedPreferences.getBoolean("IS_LOGIN",false))
        {
            mEditor = mSharedPreferences.edit();
            mEditor.clear();
            mEditor.apply();
            return false;
        }
        else
        {

            String userID = mSharedPreferences.getString("USER_ID",null);
            String deviceID = mSharedPreferences.getString("DEVICE_ID",null);
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
                        builder.setMessage(htmlResponse)
                                .setNegativeButton("확인",null)
                                .create()
                                .show();
                    }
                }
            };
            SessionCheckRequest sessionCheckRequest = new SessionCheckRequest(userID,deviceID,listener);
            mQueue.add(sessionCheckRequest);

            return mSharedPreferences.getBoolean("IS_LOGIN", false);
            /*TODO: 웹서버에 해당하는 USER_ID 와 DEVICE_ID 가 있는지 체크
            * TODO: 웹서버는 한계정당 하나의 DEVICE_ID를 가지게 함
            * TODO: 로그인할때 이미 다른아이디에 존재하는 DEVICE_ID이면, 기존것을 삭제
            */

        }

    }

    // 로그인정보 저장
    public boolean createSession(String id, String deviceID)
    {
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean("IS_LOGIN",true);
        mEditor.putString("USER_ID", id);
        mEditor.putString("DEVICE_ID", deviceID);
        if(mEditor.commit())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    //로그인 정보 삭제, 웹서버에서도 해당 아이디에 저장된 deviceID 삭제
   public boolean deleteSession() {


       String userID = mSharedPreferences.getString("USER_ID", null);
       String deviceID = mSharedPreferences.getString("DEVICE_ID", null);

       mEditor = mSharedPreferences.edit();
       mEditor.clear();
       mDelStatus = mEditor.commit();
        // 동기적 통신을 해보기 위해 RequestFuture를 사용해보려는데 잘안됨
        // TODO: 동기적통신 구현할수 잇다면 해보기(Volley.toolbox.RequestFuture)
      if(mDelStatus) {
           Response.Listener<String> listener = new Response.Listener<String>() {
               @Override
               public void onResponse(String response) {
                   Document html = Jsoup.parse(response);
                   String htmlResponse = html.body().text();

                   try {
                       JSONObject jsonResponse = new JSONObject(htmlResponse);

                       boolean success = jsonResponse.getBoolean("success");
                       if (success) {
                           mDelStatus = true;
                       } else {
                           AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                           builder.setMessage(htmlResponse)
                                   .setNegativeButton("확인", null)
                                   .create()
                                   .show();
                           mDelStatus = false;
                       }
                   } catch (Exception e) {
                       AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                       builder.setMessage(e.getMessage() + "\n" + htmlResponse)
                               .setNegativeButton("확인", null)
                               .create()
                               .show();
                   }
               }
           };
           LogoutRequest logoutRequest = new LogoutRequest(userID, deviceID, listener);
           mQueue.add(logoutRequest);
       }

       return mDelStatus;
   }
}
