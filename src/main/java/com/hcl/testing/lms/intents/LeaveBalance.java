/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hcl.testing.lms.intents;

import com.hcl.ai_bot.common.CommonFuns;
import com.hcl.ai_bot.common.IntentProcessor;
import java.util.HashMap;
import java.util.function.Predicate;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author root
 */
public class LeaveBalance implements IntentProcessor{

    @Override
    public String process(HttpSession ses, HashMap<String, Object> request) {
        HashMap<String,String> headers=new HashMap<>();
        headers.put("Content-Type","application/json");
        headers.put("Accept","application/json");
        
        String url="https://m.myhcl.com/LMSMobile/Approvals/GetEmployeeLeaveBalanceDetail";
        String method="POST";
        
        HashMap<String, HashMap<String,String>> contexts = (HashMap<String, HashMap<String,String>>)ses.getAttribute("contexts");
        HashMap<String,String> details=contexts.get("login");
        String json_ob="{\"header\":{\"device_id\":\"WEB\",\"expiry_timespan\":\""+details.get("expiry_timespan")+"\",\"session_token\":\""+details.get("session_token")+"\",\"SapID\":\""+details.get("sap_id")+"\"},\"payload\":{\"EmployeeCode\":\""+details.get("sap_id")+"\"}}";
                
        String parameters=new JSONObject(json_ob).toString();
        HashMap<Integer,Object> ob2=CommonFuns.requestHandler(headers, url, method, parameters);
        if(ob2.containsKey(200))
        {
            JSONObject response=new JSONObject(ob2.get(200).toString());
            if(response.getJSONObject("header").getString("error_id").equalsIgnoreCase("0"))
            {
                contexts = (HashMap<String, HashMap<String,String>>)ses.getAttribute("contexts");
                details=contexts.get("login");
                details.put("device_id", response.getJSONObject("header").getString("device_id"));
                details.put("session_token", response.getJSONObject("header").getString("session_token"));
                details.put("expiry_timespan", response.getJSONObject("header").getString("expiry_timespan"));
                contexts.put("login", details);
                JSONArray lb=response.getJSONObject("payload").getJSONArray("EmpLeaveBalance");
                Predicate<Object> pre= p -> {
                    HashMap<String,String> ob=(HashMap<String,String>) p;
                    return ob.get("Leave_Code").equalsIgnoreCase(request.get("leavetype").toString());
                };
                Object[] res=(lb.toList().stream().filter(pre).toArray());
                if(res==null || res.length==0)
                {
                    return request.get("leavetype").toString() + " leave type is not applicable to you";
                }
                return new JSONObject((HashMap<String,String>)res[0]).toString(1).replaceAll("[{}\",]", "").replaceAll("_", " ");
            }
            else
            {
                return "Service failure: "+response.getJSONObject("header").getString("error_message");
            }
            //return ob2.get(200).toString();
        }
        else
        {
            return "Service failure called else, Please retry again.";
        }
    }

    @Override
    public Object GetInstance() {
        return new LeaveBalance();
    }
    
    
}
