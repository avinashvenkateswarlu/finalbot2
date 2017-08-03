package com.hcl.testing.nj.intents;

import com.hcl.ai_bot.common.CommonFuns;
import com.hcl.ai_bot.common.IntentProcessor;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author root
 */
public class Getretailers implements IntentProcessor{

    @Override
    public String process(HttpSession ses, HashMap<String, Object> request) {
        
        HashMap<String,String> headers=new HashMap<>();
        headers.put("Accept-Language","en-US,en;q=0.8,hi;q=0.6,te;q=0.4");
        headers.put("Cache-Control","max-age=0");
        headers.put("Connection","keep-alive");
        headers.put("Dnt","1");
        headers.put("Upgrade-Insecure-Requests","1");
        headers.put("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");


        String url="";
        
        if(request.containsKey("zip"))
        {
            url="https://www.njlottery.com/api/v1/locations?postal-codes="+request.get("zip").toString()+"&page=0&size=10";
        }
        else if(request.containsKey("city"))
        {
            try
            {
                url="https://www.njlottery.com/api/v1/locations?city="+URLEncoder.encode(request.get("city").toString(),java.nio.charset.StandardCharsets.UTF_8.toString())+"&page=0&size=10";
            }
            catch(Exception ex)
            {
                url="https://www.njlottery.com/api/v1/locations?city="+request.get("city").toString()+"&page=0&size=10";
            }
        }
        
        String method="GET";
        ((ServletContext)ses.getAttribute("appcontext")).log("\n"+url);
        
        String parameters=null;
        HashMap<Integer,Object> ob2=CommonFuns.requestHandler(headers, url, method, parameters);
        if(ob2.containsKey(200))
        {
            String result;
            
            if(request.containsKey("zip"))
            {
                result="Below are some of the retailers at "+request.get("zip")+" zip code: <br/>";
            }
            else
            {
                result="Below are some of the retailers at "+request.get("city")+": <br/>";
            }
            JSONObject response=new JSONObject(ob2.get(200).toString());
            ((ServletContext)ses.getAttribute("appcontext")).log("\n"+response.getJSONArray("locations").toString(8));
            
            ArrayList<LinkedHashMap<String,String>> summary=new ArrayList<>();
            JSONArray retailers=response.getJSONArray("locations");
            
            if(retailers.length()==0)
            {
                return "Sorry, there are no retailers at the given location.";
            }
            
            for(int i=0;i<retailers.length();i++)
            {
                LinkedHashMap<String,String> record=new LinkedHashMap<String,String>();
               JSONObject temp_ob=retailers.getJSONObject(i);
               record.put("Name", temp_ob.getString("name"));
               record.put("phoneNumber", temp_ob.getString("phoneNumber"));
               record.put("Address", temp_ob.getString("address1"));
               record.put("City", temp_ob.getString("city"));
               record.put("Country", temp_ob.getString("country"));
               record.put("postalCode", temp_ob.getString("postalCode"));
               summary.add(record);
            }
            
            for(LinkedHashMap<String,String> tp : summary)
            {
                result=result+"<hr/>";
                for(String s : tp.keySet())
                {
                    result+=s+": "+tp.get(s)+"<br/>";
                }
            }
            return result;
        }
        else
        {
            return "Sorry, unable to process your request"+ob2.toString();
        }
        
    }

    @Override
    public Object GetInstance() {
        return new Getretailers();
    }
    
}
