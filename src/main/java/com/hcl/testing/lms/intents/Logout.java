/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hcl.testing.lms.intents;

import com.hcl.ai_bot.common.CommonFuns;
import com.hcl.ai_bot.common.IntentProcessor;
import java.util.HashMap;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;

/**
 *
 * @author root
 */
public class Logout implements IntentProcessor{

    @Override
    public String process(HttpSession ses, HashMap<String, Object> request) {
        if(!request.get("trueorfalse").toString().equalsIgnoreCase("true"))
        {
            return "Logout Cancelled";
        }
        HashMap<String,String> headers=new HashMap<>();
        headers.put("Content-Type","application/json");
        headers.put("Accept","application/json");
        
        String url="https://m.myhcl.com/MyHCLMobile/Authenticate/Logoff";
        String method="POST";
        
        HashMap<String, HashMap<String,String>> contexts = (HashMap<String, HashMap<String,String>>)ses.getAttribute("contexts");
        HashMap<String,String> details=contexts.get("login");
        String json_ob="{\"header\":{\"device_id\":\"WEB\",\"expiry_timespan\":\""+details.get("expiry_timespan")+"\",\"session_token\":\""+details.get("session_token")+"\",\"SapID\":\""+details.get("sap_id")+"\",\"Employee_Code\":\""+details.get("sap_id")+"\"},\"payload\":{\"SAP_ID\":\""+details.get("sap_id")+"\"}}";
                
        String parameters=new JSONObject(json_ob).toString();
        HashMap<Integer,Object> ob2=CommonFuns.requestHandler(headers, url, method, parameters);
        
        if(ob2.containsKey(200))
        {
            if(contexts.containsKey("login"))
            {
                contexts.remove("login");
            }
            return "You are successfully logged out.<br/> In order to Log-In please type login.";
        }
        else
        {
            return "Logout Failure. <br/> Please close the chat to log out.";
        }
    }

    @Override
    public Object GetInstance() {
        return new Logout();
    }
    
}
