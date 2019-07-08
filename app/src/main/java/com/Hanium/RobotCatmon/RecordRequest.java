package com.Hanium.RobotCatmon;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RecordRequest extends StringRequest{
    final static private String requestURL ="http://35.243.113.182/hig/recordlist.php";

    private HashMap<String, String> parameters;

    RecordRequest(String rocatid, Response.Listener<String> listener)
    {
        super(Method.POST, requestURL, listener, null);
        parameters = new HashMap<>();
        parameters.put("rocatid",rocatid);
    }

    @Override
    public Map<String, String> getParams()
    {
        return parameters;
    }

}
