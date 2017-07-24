package com.hcl.ai_bot.common;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class AppstartupLogic implements ServletContextListener{

    HashMap<String,Long> file_modified;
    Timer tasks;
    static ServletContext application=null;
    
    public AppstartupLogic() {
        file_modified=new HashMap<>();
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        
        file_modified.put("Pattern-Classifier", 0L);
        file_modified.put("Entity-Mapper", 0L);
        file_modified.put("Intent-Mapper", 0L);
        application=sce.getServletContext();
        
        Database_oper.databaseurl=application.getInitParameter("Databaseurl")+"";
        Database_oper.databaseuser=application.getInitParameter("Username")+"";
        Database_oper.databasepassword=application.getInitParameter("Password")+"";
        Database_oper.jdbcdriverclass=application.getInitParameter("Driverclass")+"";
        
        
        application.setAttribute("intents",new ArrayList<HashMap<String,String>>());
        final Timer app_booster=new Timer();
        final URL u[]=((URLClassLoader)application.getClassLoader().getParent()).getURLs();
        app_booster.schedule(new TimerTask() {
            @Override
            public void run() {
                File f=new File(application.getRealPath("WEB-INF/Pattern-Classifier.xml"));
                if(f.exists())
                {
                    if(file_modified.get("Pattern-Classifier")!=f.lastModified())
                    {
                        try
                        {
                            try
                            {
                                HashMap<String,String> headers=new HashMap<String, String>();
                                headers.put("key", application.getInitParameter("api.ai access token"));
                                String url="http://"+application.getInitParameter("host name")+"/secured/patterns-update.jsp";
                                String method="GET";


                                String parameters=null;
                                //HashMap<Integer,Object> ob2=CommonFuns.requestHandler(headers, url, method, parameters);
                                //application.log(ob2.toString());
                                file_modified.put("Pattern-Classifier",f.lastModified());
                                
                                headers=new HashMap<String, String>();
                                headers.put("key", application.getInitParameter("api.ai access token"));
                                url="http://"+application.getInitParameter("host name")+"/secured/index.jsp";
                                method="GET";


                                parameters=null;
                                CommonFuns.requestHandler(headers, url, method, parameters);
                                
                            }
                            catch(Exception ex){}
                        }
                        catch(Exception ex)
                        {
                            application.log(ex.getMessage(),ex);
                        }
                    }
                    System.gc();
                }
            }
        },0, 2000);
        
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if(tasks!=null)
        {
            tasks.purge();
            tasks.cancel();
        }
    }
    
    
}
