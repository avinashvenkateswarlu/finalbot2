<%-- 
    Document   : testingjs
    Created on : Aug 3, 2017, 12:04:05 PM
    Author     : root
--%>

<%@page import="org.json.JSONObject"%>
<%@page import="com.hcl.testing.Testing"%>
<%@page import="java.util.Scanner"%>
<%@page import="java.io.DataInputStream"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <%
            Scanner sc=new Scanner(request.getInputStream());
            String req="";
            while(sc.hasNext())
            {
                req=req+sc.nextLine();
            }
            
            application.log(new Testing().parsethrequest(new JSONObject(req)));
            
            %>
    </body>
</html>
