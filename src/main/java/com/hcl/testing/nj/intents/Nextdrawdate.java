package com.hcl.testing.nj.intents;

import com.hcl.ai_bot.common.CommonFuns;
import com.hcl.ai_bot.common.IntentProcessor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author root
 */
public class Nextdrawdate implements IntentProcessor{

    @Override
    public Object GetInstance() {
        return new Nextdrawdate();
    }

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
        
        if(request.containsKey("gamename"))
        {
            url="https://www.njlottery.com/api/v2/draw-games/draws/?previous-draws=1&next-draws=0";
        }
        
        String method="GET";
        
        ((ServletContext)ses.getAttribute("appcontext")).log("\nURL is"+ url);
        String parameters=null;
        HashMap<Integer,Object> ob2=CommonFuns.requestHandler(headers, url, method, parameters);
        
        if(ob2.containsKey(200))
        {
            JSONObject res=new JSONObject(ob2.get(200).toString());
            ((ServletContext)ses.getAttribute("appcontext")).log("\n"+res.toString(8));
            if(res.has("draws"))
            {
                JSONArray draws=res.getJSONArray("draws");
                for(int i=0;i<draws.length();i++)
                {
                    String $cash="";
                    if(draws.getJSONObject(i).has("estimatedJackpot") && draws.getJSONObject(i).getLong("estimatedJackpot")>0)
                    {
                        $cash="Estimated Jack pot: <b>$ "+(draws.getJSONObject(i).getLong("estimatedJackpot"))+"</b>"; 
                        $cash+="<br/>Cash Options: <b>$ "+(draws.getJSONObject(i).getLong("annuityCashOption"))+"</b>";
                    }
                    if(draws.getJSONObject(i).getString("gameName").equalsIgnoreCase(request.get("gamename").toString()) && draws.getJSONObject(i).getString("status").equalsIgnoreCase("open"))
                    {
                        Date d=new Date(Long.parseLong(draws.getJSONObject(i).get("drawTime").toString()));
                        SimpleDateFormat df=new SimpleDateFormat("d-M-Y E");
                        
                        if(draws.getJSONObject(i).has("name"))
                        {
                            return "Next draw for the game "+draws.getJSONObject(i).getString("gameName")+" is "+df.format(d)+" "+draws.getJSONObject(i).getString("name")+"<br/>"+$cash;
                        }
                        return "Next draw for the game "+draws.getJSONObject(i).getString("gameName")+" is "+df.format(d)+"<br/>"+$cash;
                    }
                }
                return "found draws";
            }
            else
            {
                return "draws not found";
            }
            //((ServletContext)ses.getAttribute("appcontext")).log("\n"+res.toString(8));
            //return res.toString();
        }
        else
        {
            return ob2.toString();
        }
    }
    
}