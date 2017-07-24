/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hcl.testing.lms.intents;

import com.hcl.ai_bot.common.IntentProcessor;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author root
 */
public class Help implements IntentProcessor{

    String output;
    @Override
    public String process(HttpSession ses, HashMap<String, Object> request) {
        
        if(((HashMap<String,HashMap<String,String>>)ses.getAttribute("contexts")).containsKey("login"))
        {
            ServletContext appcontext=(ServletContext)ses.getAttribute("appcontext");
            appcontext.log("\nIntents are: "+new JSONArray((ArrayList<HashMap<String,String>>)appcontext.getAttribute("intents")).toString(8));
            output="Below are the options available.";
            ((ArrayList<HashMap<String,String>>)appcontext.getAttribute("intents")).forEach(p -> {
                if(p.containsKey("option-name"))
                {
                       if(p.get("option-name").trim().length()>0)
                        {
                           output=output+"<br/>"+p.get("option-name");
                        }
                }
            });
            return output;
        }
        else
        {
            String output="Please type login to get information.";
            return output;
        }
    }

    @Override
    public Object GetInstance() {
        return new Help();
    }
}
