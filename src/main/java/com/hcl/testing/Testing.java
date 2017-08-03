package com.hcl.testing;

import org.json.JSONObject;

/**
 *
 * @author root
 */
public class Testing {
    public String parsethrequest(JSONObject ob)
    {
        try
        {
         return "dummy";   
            //String serviceurl=ob.getString("serviceUrl")
        }
        catch(Exception ex)
        {
            return ex.toString();
        }
    }
}
