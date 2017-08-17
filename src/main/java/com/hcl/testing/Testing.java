package com.hcl.testing;

import com.hcl.ai_bot.common.CommonFuns;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import javax.servlet.ServletContext;
import org.json.JSONObject;

/**
 *
 * @author root
 */
public class Testing {
    static HashMap<String,Date> conversations=new HashMap<>();
    
    public String parsethrequest(JSONObject ob)
    {
        String token="";
        try
        {
            
           String output="received: \n"+ob.toString(8);
           
           JSONObject res=new JSONObject();
           res.put("type", "message");
           res.put("from", ob.get("recipient"));
           res.put("recipient", ob.get("from"));
           
           if(ob.has("textFormat"))
                 res.put("textFormat", ob.get("textFormat"));
                      
           if(ob.has("locale"))
               res.put("locale", ob.get("locale"));
           
           res.put("text", "processed message will come here");
           res.put("replyToId", ob.get("id"));
           
           output=output+"\nprepared as :\n"+res.toString(8);
           
           output+="\nservice url: "+ob.get("serviceUrl")+"v3/conversations/"+ob.getJSONObject("conversation").getString("id")+"/activities/";
           
          
                if(token.trim().length()==0)
                {
                     HashMap<String,String> headers=new HashMap<>();
                     headers.put("Content-Type","application/x-www-form-urlencoded");

                     String url="https://login.microsoftonline.com/botframework.com/oauth2/v2.0/token";

                     String method="POST";

                     String parameters="grant_type=client_credentials&client_id=ee12e421-b1a9-46c4-85cb-07af1a80bbf1&client_secret=7WKH4uG2bLARMWi5K8VmuAq&scope=https://api.botframework.com/.default";
                     HashMap<Integer,Object> ob2=CommonFuns.requestHandler(headers, url, method, parameters);
                     if(ob2.containsKey(200))
                     {
                        token=new JSONObject(ob2.get(200).toString()).getString("access_token");
                     }
                }
                     HashMap<String,String> headers=new HashMap<>();
                     headers.put("Content-Type","application/json");
                     headers.put("Authorization","Bearer "+token);

                     String url=ob.get("serviceUrl")+"v3/conversations/"+ob.getJSONObject("conversation").getString("id")+"/activities/";

                     String method="POST";

                     String parameters=res.toString(8);
                     HashMap<Integer,Object> ob2=CommonFuns.requestHandler(headers, url, method, parameters);

                     if(ob2.containsKey(401) || ob2.containsKey(10000))
                     {
                        return "message not sent"+ob2.toString();
                     }
                     else
                     {
                         return "message sent"+ob2.toString();
                     }
           
        }
        catch(Exception ex)
        {
            return ex.toString();
        }
    }
}
