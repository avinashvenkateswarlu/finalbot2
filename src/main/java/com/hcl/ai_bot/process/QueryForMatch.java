package com.hcl.ai_bot.process;

import com.hcl.ai_bot.common.CommonFuns;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.tools.ToolProvider;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.python.util.PythonInterpreter;

/**
 *
 * @author root
 */
public class QueryForMatch {

    HttpSession ses;
    
    public Object process(String query,HttpSession ses,ServletContext appContext)
    {
        
        
        this.ses=ses;
        ses.setAttribute("appcontext", appContext);
        String key=appContext.getInitParameter("api.ai access token").toString();
            
        HashMap<String,String> headers=new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer "+key);
        String url=appContext.getInitParameter("api.ai base-url").toString()+"query?v=20150910";
        String method="POST";
        
        JSONObject request=new JSONObject();
        request.put("query", query);
        request.put("lang", "en");
        request.put("sessionId", ses.getId());
        HashMap<String, HashMap<String,String>> contexts=(HashMap<String, HashMap<String,String>>)ses.getAttribute("contexts");
        JSONArray ctx=new JSONArray();
        if(contexts.keySet()!=null)
        {
        for(String s : contexts.keySet())
        {
            JSONObject ob=new JSONObject();
            ob.put("name", s);
            ob.put("lifespan", 1);
            ctx.put(ob);
        }
        }
        request.put("contexts", ctx);
        if(query.equalsIgnoreCase("exit"))
        {
            if(Collections.list(ses.getAttributeNames()).contains("last_intent"))
            {
                ses.removeAttribute("last_intent");
            }
            request.put("resetContexts", true);
        }
        else
        {
            request.put("resetContexts", false);
        }
        
        
        ArrayList<HashMap<String,String>> intents=(ArrayList<HashMap<String,String>>)appContext.getAttribute("intents");
        if(ses.getAttribute("last_intent")!=null)
        {
            final HashMap<String,String> op=(HashMap<String,String>) ses.getAttribute("last_intent");
            Predicate<HashMap<String,String>> pre= o -> o.get("name").equalsIgnoreCase(op.get("intent"));
            
            if(true)
            {
                appContext.log("\nOp has : "+op.toString()+"\nName: "+op.get("intent")+"\n"+intents.toString());
            }
            
            intents=(ArrayList<HashMap<String,String>>) intents.stream().filter(pre).collect(Collectors.toList());
            
            if(true)
            {
                appContext.log("\nName: "+op.get("intent")+"\n"+intents.toString());
            }
            
            if(true) //op.get("param").startsWith("pattern-"))
            {
            
                if(intents.get(0).containsKey(op.get("param")))
                {
                    String pattern_name=intents.get(0).get(op.get("param")).replaceFirst("@pattern-", "");
                    appContext.log("\n pushing for : "+op.get("param")+" of type "+pattern_name);
                    //appContext.log("\n pattern data is : "+((HashMap<String,HashMap<String,String>>) appContext.getAttribute("patterns")).toString());
                    HashMap<String,String> pattern=((HashMap<String,HashMap<String,String>>) appContext.getAttribute("patterns")).get(pattern_name);

                    appContext.log(pattern.toString());

                    if(pattern.get("code type").equalsIgnoreCase("custom"))
                    {
                        String class_name=pattern.get("name");
                         Compiler.enable();

                            URL u[]=new URL[1];
                            File f=new File(appContext.getRealPath("WEB-INF/pattern-source/"+class_name+".java"));
                            if(f.exists())
                            {
                                try
                                {
                                    //u[0]=f.toURI().toURL().;
                                    appContext.log("\npath: "+f.getAbsoluteFile().toString());

                                    ToolProvider.getSystemJavaCompiler().run(null, null, null, f.getAbsoluteFile().toString());
                                    appContext.log("\ncompilation done");

                                    u[0]=new URL("file://"+f.getParent().replace(class_name+".java", "")+"/");
                                    appContext.log("\n Class p[ath : "+u[0]);
                                    ClassLoader cl=new URLClassLoader(u);

                                    Class c=cl.loadClass(class_name);
                                    Object ob=c.newInstance();
                                    //System.out.println(c.getDeclaredFields().length);
                                    for (Field field : c.getDeclaredFields()) {
                                        appContext.log(field.getName());
                                    }

                                     for (Method m : c.getMethods()) {
                                         if(m.getName().compareTo("process")==0)
                                         {
                                             if(((boolean)m.invoke(ob,query)))
                                             {
                                                 request.put("entities", createUserEntity(appContext,"pattern-"+pattern_name,query,op.get("intent"),op.get("param")));
                                             }
                                         }
                                         appContext.log(m.getName());
                                    }
                                }
                                catch(Exception ex)
                                {
                                    appContext.log(ex.getMessage(),ex);
                                }
                                catch(Error e)
                                {
                                    appContext.log(e.getMessage(),e);
                                }
                            }

                    }
                    if(pattern.get("code type").equalsIgnoreCase("Classpath"))
                    {
                        try
                        {
                                    ClassLoader cl=appContext.getClassLoader();

                                    Class c=cl.loadClass(pattern.get("class"));
                                    Object ob=c.newInstance();

                                    for (Method m : c.getMethods()) {
                                         if(m.getName().compareTo("process")==0)
                                         {
                                             if(((boolean)m.invoke(ob,query)))
                                             {
                                                 request.put("entities", createUserEntity(appContext,"pattern-"+pattern_name,query,op.get("intent"),op.get("param")));
                                             }
                                         }
                                         appContext.log(m.getName());
                                    }
                        }
                        catch(Exception ex)
                        {
                            appContext.log(ex.getMessage(),ex);
                        }
                        catch(Error e)
                        {
                            appContext.log(e.getMessage(),e);
                        }
                    }
                }
            }
            
            if(request.has("entities"))
            {
                request.remove("query");
                request.put("query", "dummytext");
            }
        }
        
        
        String parameters=request.toString(8);
        appContext.log("\nsending query as :\n"+parameters);
        HashMap<Integer,Object> ob2=CommonFuns.requestHandler(headers, url, method, parameters);
        if(ob2.containsKey(200))
        {
            
            if(ses.getAttribute("last_intent")!=null)
            {
                ses.removeAttribute("last_intent");
            }
            
            final JSONObject res=new JSONObject(ob2.get(200).toString());
            appContext.log(res.toString(8));
            ses.setAttribute("current_intent", res.getJSONObject("result").getJSONObject("metadata").getString("intentName"));
            
            
        //appContext.getAttribute("patterns");
        Predicate<HashMap<String,String>> pre= o-> o.get("name").equalsIgnoreCase(res.getJSONObject("result").getString("action"));
        intents=(ArrayList<HashMap<String,String>>)appContext.getAttribute("intents");
        intents=(ArrayList<HashMap<String,String>>)intents.stream().filter(pre).collect(Collectors.toList());
        
            
            JSONArray jr=res.getJSONObject("result").getJSONArray("contexts");
            for(int i=0;i<jr.length();i++)
            {
                if(jr.get(i) instanceof JSONObject)
                {
                    JSONObject temp=jr.getJSONObject(i);
                    if(temp.getString("name").contains("_dialog_params_"))
                    {
                            
                                HashMap<String,String> param_info=new HashMap<>();
                                param_info.put("intent", res.getJSONObject("result").getJSONObject("metadata").getString("intentName"));
                                param_info.put("param", temp.getString("name").substring(temp.getString("name").lastIndexOf("_dialog_params_")+"_dialog_params_".length()));
                                
                                    ses.setAttribute("last_intent", param_info);
                                    appContext.log("\nsession variable is setted with " + param_info.toString());
                    }
                }
            }
            
            if(res.getJSONObject("result").getJSONObject("fulfillment").getString("speech").startsWith("success:java:"))
            {
                    String result="";
                        try
                        {
                            /*
                            [{code type=Classpath, name=EmployeeFinder, pt1=@pattern-employee_id, class=com.hcl.testing.KnowHolidayForEmp}]
                            */
                                    ClassLoader cl=appContext.getClassLoader();

                                    Class c=cl.loadClass(res.getJSONObject("result").getJSONObject("fulfillment").getString("speech").replace("success:java:", "").trim());
                                    Object ob=c.newInstance();

                                    Object args[]=new Object[2];
                                    args[0]=ses;
                                    args[1]=(HashMap<String,Object>)CommonFuns.parsejson("parameters", res.getJSONObject("result").getJSONObject("parameters")).get("parameters");
                                   
                                    
                                    for (Method m : c.getMethods()) {
                                         if(m.getName().compareTo("process")==0)
                                         {
                                             result=m.invoke(ob,args).toString();
                                         }
                                    }
                        }
                        catch(Exception ex)
                        {
                            appContext.log(ex.getMessage(),ex);
                        }
                        catch(Error e)
                        {
                            appContext.log(e.getMessage(),e);
                        }
                    
                
                appContext.log("\ngoing to call: from java match\n "+intents.toString());
                
                if(result.trim().length()==0)
                {
                    result=res.getJSONObject("result").getJSONObject("fulfillment").getString("speech");
                }
                remove_prev_intent_info_session(appContext);
                return result;
            }
            else if(res.getJSONObject("result").getJSONObject("fulfillment").getString("speech").startsWith("success:js:"))
            {
                HashMap<String,String> params=(HashMap<String,String>)CommonFuns.parsejson("parameters", res.getJSONObject("result").getJSONObject("parameters")).get("parameters");
                if(Collections.list(ses.getAttributeNames()).contains("(intent-)"+ses.getAttribute("current_intent").toString()))
                {
                    params.putAll((HashMap<String,String>)ses.getAttribute("(intent-)"+ses.getAttribute("current_intent").toString()));
                    appContext.log("\nPut all called");
                }
                else
                {
                    appContext.log("\nPut all   NOT  called");
                }
                
                Context cx = Context.enter();
        
                try {
                    appContext.log("\nIn sessions: "+(HashMap<String,String>)ses.getAttribute("(intent-)"+ses.getAttribute("current_intent").toString()));
                    appContext.log("\nnumber input is: "+params.get("input"));
                    Scriptable scope = cx.initStandardObjects();

                    Object jsOut = Context.javaToJS(ses, scope);
                    
                    ScriptableObject.putProperty(scope, "session", jsOut);
                    
                    jsOut = Context.javaToJS(appContext, scope);
                    
                    ScriptableObject.putProperty(scope, "appcontext", jsOut);
                    
                    jsOut = Context.javaToJS(params, scope);
                    
                    ScriptableObject.putProperty(scope, "params", jsOut);
                    
                    
                    Object result = cx.evaluateReader(scope, new FileReader(appContext.getRealPath(res.getJSONObject("result").getJSONObject("fulfillment").getString("speech").replace("success:js:", "").trim())), "<cmd>", 1, null);
                    
                    if(ScriptableObject.getProperty(scope, "appcontext") instanceof ServletContext)
                    {
                        appContext=(ServletContext) ScriptableObject.getProperty(scope, "appcontext");
                    }
                    
                    if(ScriptableObject.getProperty(scope, "session") instanceof HttpSession)
                    {
                        ses=(HttpSession) ScriptableObject.getProperty(scope, "session");
                    }
                    
                    appContext.log("\n After call session is: "+Collections.list(ses.getAttributeNames()).toString());
                   
                    // Convert the result to a string and print it.
                    result =Context.jsToJava(result, String.class);
                    if(result instanceof String)
                    {
                        remove_prev_intent_info_session(appContext);
                        return result.toString();
                    }
                    else
                    {
                        remove_prev_intent_info_session(appContext);
                        return "javascript execution error";
                    }
                } 
                catch(Exception ex){
                    appContext.log(ex.toString(),ex);
                    return ex.toString();
                }finally {
                    // Exit from the context.
                    Context.exit();
                }
            }
            else if(res.getJSONObject("result").getJSONObject("fulfillment").getString("speech").startsWith("success:py:"))
            {
                HashMap<String,String> params=(HashMap<String,String>)CommonFuns.parsejson("parameters", res.getJSONObject("result").getJSONObject("parameters")).get("parameters");
                if(Collections.list(ses.getAttributeNames()).contains("(intent-)"+ses.getAttribute("current_intent").toString()))
                {
                    params.putAll((HashMap<String,String>)ses.getAttribute("(intent-)"+ses.getAttribute("current_intent").toString()));
                }
                
                try {
                    
                    StringWriter wr=new StringWriter();
                    
                    PythonInterpreter python = new PythonInterpreter();
                    
                    python.set("session", ses); //put app context
                    python.set("appcontext", appContext); //put app session
                    python.set("params", params);
                    
                    python.setOut(wr); //give it to string
                    
                    python.execfile(new FileInputStream(appContext.getRealPath(res.getJSONObject("result").getJSONObject("fulfillment").getString("speech").replace("success:py:", "").trim())));
        
                    if(python.get("session", HttpSession.class) instanceof HttpSession)
                    {
                        ses=(HttpSession) python.get("session", HttpSession.class);
                    }
                    
                    if(python.get("appcontext", ServletContext.class) instanceof ServletContext)
                    {
                        appContext=(ServletContext) python.get("appcontext", ServletContext.class);
                    }
                    
                    if(wr.toString().trim().length()>0)
                    {
                        remove_prev_intent_info_session(appContext);
                        return wr.toString();
                    }
                    else
                    {
                        remove_prev_intent_info_session(appContext);
                        return "Python script not returned any thing.";
                    }
                } 
                catch(Exception ex){
                    appContext.log(ex.toString(),ex);
                    return ex.toString();
                }
            }
            else
            {
                if(query.equalsIgnoreCase("exit"))
                {
                    remove_prev_intent_info_session(appContext);
                }
                return res.getJSONObject("result").getJSONObject("fulfillment").getString("speech").replace("\\\\n", "\n");
            }
        }
        else
        {
            return "Error Occured while processing your query.";
        }
    }
    
    private JSONArray createUserEntity(ServletContext appContext,String pattern_name,String value,String intent,String param)
    {
        HashMap<String,String> params;
        if(Collections.list(ses.getAttributeNames()).contains("(intent-)"+intent))
        {
            params=(HashMap<String,String>) ses.getAttribute("(intent-)"+intent);
        }
        else
        {
            params=new HashMap<>();
        }
        
        params.put(param, value);
        ses.setAttribute("(intent-)"+intent, params);
        appContext.log("\nExiting params for "+"(intent-)"+intent+" :"+ses.getAttribute("(intent-)"+intent).toString());
        
        
        //value=value.replaceAll("[^a-zA-Z]", "");
        
        ArrayList<HashMap<String,Object>> entries=new ArrayList<>();
        
        ArrayList<String> sy=new ArrayList<>();
        sy.add("dummytext");
        HashMap<String,Object> synonym=new HashMap<>();
        synonym.put("value", "dummytext");
        synonym.put("synonyms", sy);
        entries.add(synonym);
        
        ArrayList<HashMap<String,Object>> full=new ArrayList<>();
        HashMap<String,Object> full_par=new HashMap<>();
        full_par.put("name", pattern_name);
        full_par.put("entries", entries);
        full.add(full_par);
        
        
        appContext.log("\nArray for use enttitu:\n "+new JSONArray(full).toString(8));
        return new JSONArray(full);
    }
    
    private void remove_prev_intent_info_session(ServletContext appContext)
    {
        appContext.log("called remove session data");
        Predicate<String> p = o -> o.startsWith("(intent-)");
        Collections.list(ses.getAttributeNames()).stream().filter(p).forEach(l -> {
            ses.removeAttribute(l);
            appContext.log("\nRemoved: "+l);
                });
        
    }
}
