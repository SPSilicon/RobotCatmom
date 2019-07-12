package com.Hanium.RobotCatmon.Requests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RocatListRequest extends StringRequest{
    final static private String requestURL ="http://35.243.113.182/hig/rocatlist.php";

    private HashMap<String, String> parameters;

    public RocatListRequest(Response.Listener<String> listener)
    {
        super(Method.POST, requestURL, listener, null);
        parameters = new HashMap<>();
    }

    @Override
    public Map<String, String> getParams()
    {
        return parameters;
    }

}
