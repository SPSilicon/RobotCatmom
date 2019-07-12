package com.Hanium.RobotCatmon.Requests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest{
    final static private String requestURL ="http://35.243.113.182/hig/login.php";

    private HashMap<String, String> parameters;

    public LoginRequest(String username, String password, Response.Listener<String> listener)
    {
        super(Method.POST, requestURL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", username);
        parameters.put("userPassword", password);
    }

    @Override
    public Map<String, String> getParams()
    {
        return parameters;
    }

}
