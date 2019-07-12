package com.Hanium.RobotCatmon.Requests;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LogoutRequest extends StringRequest {

    final static private  String requestURL ="http://35.243.113.182/hig/logout.php";

    private HashMap<String, String> parameters;

    public LogoutRequest(String userID, String deviceID, Response.Listener<String> listener)
    {
        super(Method.POST, requestURL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID",userID);
        parameters.put("deviceID",deviceID);
    }

    @Override
    public Map<String, String> getParams() { return parameters; }
}
