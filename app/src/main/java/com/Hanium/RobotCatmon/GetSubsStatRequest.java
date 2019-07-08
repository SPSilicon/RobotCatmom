package com.Hanium.RobotCatmon;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class GetSubsStatRequest extends StringRequest{
    final static private String requestURL ="http://35.243.113.182/hig/getsubstat.php";

    private HashMap<String, String> parameters;

    GetSubsStatRequest(String username, String rocatID, Response.Listener<String> listener)
    {
        super(Method.POST, requestURL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", username);
        parameters.put("rocatID", rocatID);
    }

    @Override
    public Map<String, String> getParams()
    {
        return parameters;
    }

}
