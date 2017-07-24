package com.hcl.ai_bot.npl.intent;

import com.hcl.ai_bot.common.CommonFuns;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.ServletContext;
import javax.servlet.jsp.JspWriter;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author root
 */
public class Preparator1 {

    static com.hcl.ai_bot.npl.intent.Preparator1 pre=null;
    
    ServletContext appContext;
    
    public static com.hcl.ai_bot.npl.intent.Preparator1 getInstance()
    {
        if(pre==null)
        {
            pre=new com.hcl.ai_bot.npl.intent.Preparator1();
        }
        
            return pre;
    }

    public Preparator1() {
    }
    
    public void prepare(ServletContext appContext,JspWriter out) throws Exception
    {
        
        this.appContext=appContext;
        ((ArrayList<HashMap<String,String>>)appContext.getAttribute("intents")).clear();
        this.appContext=appContext;
        ArrayList<HashMap<String,String>> intents=(ArrayList<HashMap<String,String>>)appContext.getAttribute("intents");
        
        String key=appContext.getInitParameter("api.ai access token").toString();
        
        HashMap<String,String> headers=new HashMap<String, String>();
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", "Bearer "+key);
            String url=appContext.getInitParameter("api.ai base-url").toString()+"intents?v=20150910";
            String method="GET";


            
            String parameters=null;
            HashMap<Integer,Object> ob2=CommonFuns.requestHandler(headers, url, method, parameters);
            ////out.print(ob2.get(200));
            if(ob2.keySet().contains(200))
            {
                ////out.print(ob2.get(200));
                JSONArray intent=new JSONArray(ob2.get(200).toString());
                intent.forEach(p->{
                    try
                    {
                    JSONObject ob=(JSONObject) p;
                        if(!(ob.getString("name").equalsIgnoreCase("Default Fallback Intent") || ob.getString("name").equalsIgnoreCase("Default Welcome Intent")))
                        {
                            HashMap<String,String> temp=new HashMap<>();
                            temp.put("id", ob.getString("id")+"");
                            temp.put("name", ob.getString("name")+"");
                            temp.put("code type", ob.getString("name")+"");
                            intents.add(temp);
                            //out.print(ob.getString("id")+"<br/>");
                        }
                    }
                    catch(Exception ex){}
                    
                });
                for(int i=0;i<intents.size();i++)
                {
                    intents.set(i, getintentdata(intents.get(i).get("id"),out));
                }
                out.print("Success. Click on Test to test the bot.");
            }
            else
            {
                out.print("Failure. Unable to contect api.ai servers.<br/>Please try after some time.");
            }
        
            
    }
    
    private HashMap<String,String> getintentdata(String id,JspWriter out) throws Exception
    {
        HashMap<String,String> ob=new HashMap<>();
        ob.put("id", id);
     String key=appContext.getInitParameter("api.ai access token").toString();
        
        HashMap<String,String> headers=new HashMap<String, String>();
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", "Bearer "+key);
            String url=appContext.getInitParameter("api.ai base-url").toString()+"intents/"+id+"?v=20150910";
            String method="GET";


            
            String parameters=null;
            HashMap<Integer,Object> ob2=CommonFuns.requestHandler(headers, url, method, parameters);
            ////out.print(ob2.get(200));
            if(ob2.containsKey(200))
            {
                //new JSONObject(ob2.get(200)).getJSONObject("responses");
                try
                {
                    ob.put("name", new JSONObject(ob2.get(200).toString()).getString("name"));
                    JSONArray jr=new JSONObject(ob2.get(200).toString()).getJSONArray("templates");
                    if(jr.length()!=0)
                    {
                        l: for(int i=0;i<jr.length();i++)
                        {
                            if(jr.getString(i).toLowerCase().startsWith("option-name: "))
                            {
                                ob.put("option-name", jr.getString(i).toLowerCase().replaceFirst("option-name: ",""));
                                break l;
                            }
                        }
                    }
                    
                    jr=new JSONObject(ob2.get(200).toString()).getJSONArray("responses").getJSONObject(0).getJSONArray("parameters");
                    if(jr.length()!=0)
                    {
                        for(int i=0;i<jr.length();i++)
                        {
                            //out.print(jr.getJSONObject(i)+"<br/>"+jr.getJSONObject(i).getString("dataType")+"<br/>");
                            if(jr.getJSONObject(i).getString("dataType").startsWith("@pattern-"))
                            {
                                ob.put(jr.getJSONObject(i).getString("name"), jr.getJSONObject(i).getString("dataType"));
                            }
                        }
                    }
                }
                catch(Exception ex)
                {}
            }
            
            //out.print(ob.toString()+"<br/><hr/>");
            return ob;
    }
}