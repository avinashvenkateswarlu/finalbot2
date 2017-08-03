/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hcl.testing.nj.intents;

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
    int i=1;
    String output;
    @Override
    public String process(HttpSession ses, HashMap<String, Object> request) {
        
            ServletContext appcontext=(ServletContext)ses.getAttribute("appcontext");
            appcontext.log("\nIntents are: "+new JSONArray((ArrayList<HashMap<String,String>>)appcontext.getAttribute("intents")).toString(8));
            output="Below are the options available.";
            
            
            ((ArrayList<HashMap<String,String>>)appcontext.getAttribute("intents")).forEach(p -> {
                if(p.containsKey("option-name"))
                {
                       if(p.get("option-name").trim().length()>0)
                        {
                           output=output+"<br/>"+i+". "+p.get("option-name");
                           i=i+1;
                        }
                }
            });
            output=output+"<br/>"+i+". "+"Get information on some general questions.";
            return output;
        
        
    }

    @Override
    public Object GetInstance() {
        return new Help();
    }
}
