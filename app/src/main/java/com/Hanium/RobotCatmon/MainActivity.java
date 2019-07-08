package com.Hanium.RobotCatmon;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MainActivity extends AppCompatActivity {

    private EditText id;
    private EditText password;

    private Button loginButton;
    private Button registerButton;

    private SessionManager sessionManager = new SessionManager(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        id = (EditText) findViewById(R.id.IDtext);
        password = (EditText) findViewById(R.id.PasswordText);

        loginButton = (Button) findViewById(R.id.loginButton);
        registerButton = (Button) findViewById(R.id.registerButton);

        if(sessionManager.checkSession())
        {
            Intent intent = new Intent(MainActivity.this , RocatMain.class);
            intent.putExtra("userid",sessionManager.getUserID());
            MainActivity.this.startActivity(intent);
        }
        setRegisterButton();
        setLoginButton();

    }

    void setRegisterButton()
    {
        registerButton.setOnClickListener(new View.OnClickListener()
                                          {
                                              @Override
                                              public void onClick(View view)
                                              {
                                                  Intent registerIntent = new Intent(MainActivity.this, Register.class);
                                                  MainActivity.this.startActivity(registerIntent);
                                              }
                                          }
        );
    }
    /*
    *   setLoginButton 메소드
    *
    * */
    void setLoginButton()
    {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String textId = id.getText().toString();
                final String textPassword = password.getText().toString();

                if(textId==""||textPassword=="")
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("아이디와 비밀번호를 입력해 주세요")
                            .setNegativeButton("확인",null)
                            .create()
                            .show();
                }

                Response.Listener<String> responseListener= new Response.Listener<String>()
                {
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
                                Intent intent = new Intent(MainActivity.this , RocatMain.class);
                                intent.putExtra("userid",textId);
                                MainActivity.this.startActivity(intent);
                            }
                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage("아이디 또는 비밀번호가 잘못되었습니다."+htmlResponse)
                                        .setNegativeButton("확인",null)
                                        .create()
                                        .show();
                            }
                        }
                        catch(JSONException e)
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("JSON 오류\n"+e.toString())
                                    .setNegativeButton("확인",null)
                                    .create()
                                    .show();
                        }
                    }
                };

                LoginRequest loginRequest = new LoginRequest(textId,textPassword,responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(loginRequest);
            }
        });

    }

}
