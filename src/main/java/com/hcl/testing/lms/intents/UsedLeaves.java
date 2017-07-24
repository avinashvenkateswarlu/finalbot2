/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hcl.testing.lms.intents;

import com.hcl.ai_bot.common.CommonFuns;
import com.hcl.ai_bot.common.IntentProcessor;
import java.util.HashMap;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author root
 */
public class UsedLeaves implements IntentProcessor{

    @Override
    public String process(HttpSession ses, HashMap<String, Object> request) {
        HashMap<String,String> headers=new HashMap<>();
        headers.put("Content-Type","application/json");
        headers.put("Accept","application/json");
        
        String url="https://m.myhcl.com/LMSMobile/Approvals/GetHolidayCalander";
        String method="POST";
        
        HashMap<String, HashMap<String,String>> contexts = (HashMap<String, HashMap<String,String>>)ses.getAttribute("contexts");
        HashMap<String,String> details=contexts.get("login");
        String json_ob="{\"header\":{\"device_id\":\"WEB\",\"expiry_timespan\":\""+details.get("expiry_timespan")+"\",\"session_token\":\""+details.get("session_token")+"\",\"SapID\":\""+details.get("sap_id")+"\"},\"payload\":{\"EmployeeCode\":\""+details.get("sap_id")+"\",\"HolidayParameter\":\"EMP\"}}";
                
        String parameters=new JSONObject(json_ob).toString();
        HashMap<Integer,Object> ob2=CommonFuns.requestHandler(headers, url, method, parameters);
        
        if(ob2.containsKey(200))
        {
            String out="Below are Leaves taken by you: <br/>AL -> Annual Leave<br/>SL -> Sick Leave<br/>MYL -> My Leave<br/>RH -> Restricted Holiday<br/>CH -> COmpany Holiday";
            JSONArray jr=(new JSONObject(ob2.get(200).toString()).getJSONObject("payload").getJSONArray("HolidayCalanderDetails"));
            ServletContext appcontext=(ServletContext) ses.getAttribute("appcontext");
            appcontext.log("\nresponse is : \n"+jr.toString(8));
            for(int i=0;i<jr.length();i++)
            {
                JSONObject temp=jr.getJSONObject(i);
                if(temp.getString("EventType").equalsIgnoreCase("l"))
                {
                    out+="<hr/>Date:"+temp.getString("EventDate")+"<br/>Desc: "+temp.getString("EventDesc");
                }
            }
            return out;
        }
        else
        {
            return "Request Incomplete. Please try again.";
        }
    }

    @Override
    public Object GetInstance() {
        return new UsedLeaves();
    }
}
