<%@page import="com.hcl.ai_bot.common.CommonFuns,java.util.*,java.io.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <%
            com.hcl.ai_bot.npl.intent.Preparator1.getInstance().prepare(application,out);
            
                File f=new File(application.getRealPath("WEB-INF/Pattern-Classifier.xml"));
                if(f.exists())
                {
                    
                        try
                        {
                                HashMap<String,String> headers=new HashMap<String, String>();
                                headers.put("key", application.getInitParameter("api.ai access token"));
                                String url="http://"+application.getInitParameter("host name")+"/secured/patterns-update.jsp";
                                String method="GET";
                                String parameters=null;
                                application.log(CommonFuns.requestHandler(headers, url, method, parameters).toString());
                            
                        }
                        catch(Exception ex)
                        {
                            application.log(ex.getMessage(),ex);
                        }
                    
                    System.gc();
                }
                com.hcl.ai_bot.npl.intent.Preparator1.getInstance().prepare(application,out);
            
        %>
    </body>
</html>
