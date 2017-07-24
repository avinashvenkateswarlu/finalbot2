<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="org.json.JSONObject"%>
<%@page import="java.util.Scanner"%>
<%@page import="java.util.Collections,com.hcl.ai_bot.common.*"%>
<%@page contentType="application/json" pageEncoding="UTF-8" autoFlush="true"%>
{
<%
    String s="";
    Scanner sc=new Scanner(request.getReader());
    sc.useDelimiter(" ");
    while(sc.hasNext())
    {
        s=s+sc.next()+" ";
    }
    
    JSONObject ob=new JSONObject(s);
    application.log("\n"+ob.toString(8));
    
    String source="";
    if(ob.has("originalRequest"))
    {
        source=ob.getJSONObject("originalRequest").getJSONObject("data").getString("source");
    }
    else
    {
        source=ob.getJSONObject("result").getString("source");
    }
    
    HashMap<String,String> req;
    if(Collections.list(session.getAttributeNames()).contains(ob.getString("sessionId")))
    {
        req=(HashMap<String, String>)session.getAttribute(ob.getString("sessionId"));
        req.put("message", ob.getJSONObject("result").getString("resolvedQuery"));
        application.log("\n"+req.toString());
    }
    else
    {
        req=new HashMap<>();
        req.put("source", source);
        req.put("sesid", "unknown");
        req.put("message", ob.getJSONObject("result").getString("resolvedQuery"));
        session.setAttribute(ob.getString("sessionId"), req);
        application.log("\n"+req.toString());
    }
    
        HashMap<String,String> headers=new HashMap<>();
        
        response.sendError(200);
        String url="http://"+application.getInitParameter("host name")+"/process-message";
        String method="POST";
                        
        String parameters="";
        for(Map.Entry<String,String> par : req.entrySet())
        {
            parameters=parameters+par.getKey()+"="+par.getValue()+"&";
        }
        application.log("\n+"+parameters);
        HashMap<Integer,Object> ob2=CommonFuns.requestHandler(headers, url, method, parameters);
        
        String speech;
        if(ob2.containsKey(200))
        {
            JSONObject jres=new JSONObject(ob2.get(200).toString());
            speech=jres.getString("message");
            req.put("sesid", jres.getString("sesid"));
        }
        else
        {
            speech="Sorry, not able to solve your query.";
        }
        application.log(ob2.toString());
        
    %>
    

"speech": "<%=speech %>",
"displayText": "<%=speech %>",
"source": "<%=source %>"
}