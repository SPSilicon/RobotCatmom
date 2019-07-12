package com.Hanium.RobotCatmon.Requests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RocatSubsRequest extends StringRequest{
    final static private String requestURL ="http://35.243.113.182/hig/subsRocat.php";

    private HashMap<String, String> parameters;

    public RocatSubsRequest(String username, String rocatID,boolean on, Response.Listener<String> listener)
    {
        super(Method.POST, requestURL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", username);
        parameters.put("rocatID", rocatID);
        if(on)
            parameters.put("subscript","1");
        else
            parameters.put("subscript","0");
    }

    @Override
    public Map<String, String> getParams()
    {
        return parameters;
    }

}
