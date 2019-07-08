package com.Hanium.RobotCatmon;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    private EditText id;
    private EditText idCheck;
    private EditText pw;
    private EditText pwCheck;
    private Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        id = (EditText) findViewById(R.id.id);
        idCheck = (EditText) findViewById(R.id.idCheck);
        pw = (EditText) findViewById(R.id.pw);
        pwCheck = (EditText) findViewById(R.id.pwCheck);
        submit = (Button) findViewById(R.id.submitButton);

        initSubmitButton();

    }

    private void initSubmitButton()
    {
        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                String userID = id.getText().toString();
                String CheckID = idCheck.getText().toString();
                String password = pw.getText().toString();
                String Checkpw = pwCheck.getText().toString();

                if (!Pattern.matches("^[a-zA-Z0-9]+$",userID))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                    builder.setMessage("아이디는 영문과 숫자로만 이루어져야 합니다.")
                            .setNegativeButton("확인",null)
                            .create()
                            .show();

                    return;
                }
                if(!userID.equals(CheckID) || !password.equals(Checkpw))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                    builder.setMessage("아이디와 비밀번호를 확인해 주세요")
                            .setNegativeButton("확인",null)
                            .create()
                            .show();
                    return;
                }


                Response.Listener<String> responseListener = new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {

                        Document html = Jsoup.parse(response);
                        response = html.body().text();

                        try
                        {
                            JSONObject jsonResponse = new JSONObject(response);

                            boolean success = jsonResponse.getBoolean("success");
                            if(success)
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                                builder.setMessage("회원가입이 정상적으로 처리 되었습니다.")
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(Register.this , MainActivity.class);
                                                Register.this.startActivity(intent);
                                            }
                                        })
                                        .create()
                                        .show();
                            }
                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                                builder.setMessage("이미 존재하는 아이디 입니다.")
                                        .setNegativeButton("확인",null)
                                        .create()
                                        .show();
                            }
                        }
                        catch(JSONException e)
                        {
                            e.printStackTrace();
                            AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                            builder.setMessage("오류!!!!")
                                    .setPositiveButton("확인",null)
                                    .create()
                                    .show();
                        }
                    }
                };
                RegisterRequest registerRequest = new RegisterRequest(userID,password,responseListener);
                RequestQueue queue = Volley.newRequestQueue(Register.this);
                queue.add(registerRequest);
            }
        });
    }
}
