/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hcl.testing.nj.intents;

import com.hcl.ai_bot.common.CommonFuns;
import com.hcl.ai_bot.common.IntentProcessor;
import java.util.HashMap;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;

/**
 *
 * @author root
 */
public class login implements IntentProcessor{

    @Override
    public String process(HttpSession ses, HashMap<String, Object> request) {
        HashMap<String,String> pattern_parms=(HashMap<String,String>)ses.getAttribute("(intent-)"+ses.getAttribute("current_intent").toString());
        request.putAll(pattern_parms);
        
        
        HashMap<String,String> headers=new HashMap<>();
        headers.put("Content-Type","application/json");
        headers.put("Accept","application/json");
        
        String url="https://m.myhcl.com/MyHclMobile/Authenticate/Login";
        String method="POST";
        
        
        
        String json_ob="{\n" +
"\"header\":{\n" +
"        \"Manufacturer\":\"\",\n" +
"        \"ModelName\":\"\",\n" +
"        \"ScreenResolution\":\"\",\n" +
"        \"app_version\":\"MyHCL#WEB\",\n" +
"        \"device_id\":\"WEB\",\n" +
"        \"expiry_timespan\":\"\",\n" +
"        \"isRetina\":\"\",\n" +
"        \"os_Platform\":\"Browser Details:Browser name  = Chrome<br>Full version  = 55.0.2883.95<br>Major version = 55<br>navigator.appName = Netscape<br>navigator.userAgent = Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36<br>\",\n" +
"        \"session_token\":\"\"\n" +
"    },\n" +
"\"payload\":{\"domain\":\""+request.get("domains")+"\",\"password\":\""+request.get("password")+"\",\"userName\":\""+request.get("username").toString().trim().replace(" ", ".")+"\"}\n" +
"}";
        
        String parameters=new JSONObject(json_ob).toString();
        HashMap<Integer,Object> ob2=CommonFuns.requestHandler(headers, url, method, parameters);
        if(ob2.containsKey(200))
        {
            JSONObject response=new JSONObject(ob2.get(200).toString());
            if(response.getJSONObject("header").getString("error_id").equalsIgnoreCase("0"))
            {
                HashMap<String, HashMap<String,String>> contexts = (HashMap<String, HashMap<String,String>>)ses.getAttribute("contexts");
                HashMap<String,String> details=new HashMap<>();
                details.put("device_id", response.getJSONObject("header").getString("device_id"));
                details.put("session_token", response.getJSONObject("header").getString("session_token"));
                details.put("expiry_timespan", response.getJSONObject("header").getString("expiry_timespan"));
                details.put("sap_id", response.getJSONObject("payload").getString("sap_id"));
                contexts.put("login", details);
                return "Hurray! you successfully logged in.<br/>Help -> avilable options.<br/>Exit -> To break a dialog.<br/>Logout -> logout";
            }
            else
            {
                return "Login Failure. Error message is: "+response.getJSONObject("header").getString("error_message");
            }
            //return ob2.get(200).toString();
        }
        else
        {
            return "Login Failure, Please retry again.";
        }
    }

    @Override
    public Object GetInstance() {
        return new login();
    }

}

/*
{
"header":{
        "Manufacturer":"",
        "ModelName":"",
        "ScreenResolution":"",
        "app_version":"MyHCL#WEB",
        "device_id":"WEB",
        "expiry_timespan":"",
        "isRetina":"",
        "os_Platform":"Browser Details:Browser name  = Chrome<br>Full version  = 55.0.2883.95<br>Major version = 55<br>navigator.appName = Netscape<br>navigator.userAgent = Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36<br>",
        "session_token":""
    },
"payload":{"domain":"HCLTECH","password":"June@2017","userName":"venkateswarlu.a"}
}

*/