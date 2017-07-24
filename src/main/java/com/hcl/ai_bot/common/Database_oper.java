/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hcl.ai_bot.common;

import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author root
 */
public class Database_oper {
    public static String databaseurl;
    public static String databaseuser;
    public static String databasepassword;
    public static String jdbcdriverclass;
    private Connection con;
    
    public String open_connection()
    {
        String temp="";
        try
        {
            Class.forName(jdbcdriverclass);
            //return databaseurl+", "+databaseuser+", "+databasepassword;
            con=DriverManager.getConnection(databaseurl, databaseuser, databasepassword);
            
            temp+="success";
        }
        catch(Exception ex)
        {
            temp+=ex.toString();
        }
        finally
        {
            if(temp.equalsIgnoreCase("success"))
            {
                return temp;
            }
            else
            {
                return temp;
            }
        }
        
    }
    
    
    //functions like count, avg, aggregate
    public Object getfirstvalue(String query) {
        Object temp="";
        try
        {
            if (con.isClosed()) {
                return "";
            }
            else
            {
                ResultSet rs=con.createStatement().executeQuery(query);
                
                while(rs.next())
                {
                    temp=rs.getObject(1);
                }
                rs.close();
            }
        }
        catch(Exception ex)
        {
            temp=ex.getLocalizedMessage().toString();
        }
        finally
        {
            return temp;
        }
    }
    
    public String insertorupdate(PreparedStatement ps) {
        //Object temp="success";
        try
        {
            if (con.isClosed()) {
                return "closed";
            }
            else
            {
                int k= ps.executeUpdate();
                ps.closeOnCompletion();
                return k+"";
            }
        }
        catch(Exception ex)
        {
            return ex.toString();
        }
    }

    public String getJson(String query)
    {
        HashMap<String,String> hm;
        ArrayList<HashMap<String,String>> l=new ArrayList<>();
      try
        {
            ResultSet rs=(con.createStatement()).executeQuery(query);
            ResultSetMetaData rm=rs.getMetaData();
            
            String column_names[]=new String[rm.getColumnCount()];
            
            
            for(int i=1;i<=rm.getColumnCount();i++)
            {
                column_names[i-1]=rm.getColumnName(i);
            }
            
            int rowcount=0;
            
            while(rs.next())
            {
                rowcount++;
            }
            
            rs=(con.createStatement()).executeQuery(query);
            
            for(int i=1;i<=rowcount;i++)
            {
                rs.next();
                
                hm=new HashMap<>();
                for(int j=1;j<=column_names.length;j++)
                {
                    Object ob=rs.getObject(column_names[j-1]);
                    if(ob!=null)
                    {
                        hm.put(column_names[j-1],ob.toString());
                    }
                }
                l.add(hm);
            }
            closeConnection();
                    return (new Gson().toJson(l));
        }
        catch(Exception ex)
        {
            closeConnection();
            return ex.getLocalizedMessage();
        }
        finally
        {
            closeConnection();
        }   
    }
    
    public String getJson2(String query)
    {
        HashMap<String,String> hm;
        ArrayList<HashMap<String,String>> l=new ArrayList<>();
      try
        {
            ResultSet rs=(con.createStatement()).executeQuery(query);
            ResultSetMetaData rm=rs.getMetaData();
            
            String column_names[]=new String[rm.getColumnCount()];
            
            
            for(int i=1;i<=rm.getColumnCount();i++)
            {
                column_names[i-1]=rm.getColumnName(i);
            }
            
            int rowcount=0;
            
            while(rs.next())
            {
                rowcount++;
            }
            
            rs=(con.createStatement()).executeQuery(query);
            
            for(int i=1;i<=rowcount;i++)
            {
                rs.next();
                
                hm=new HashMap<>();
                for(int j=1;j<=column_names.length;j++)
                {
                    Object ob=rs.getObject(column_names[j-1]);
                    if(ob!=null)
                    {
                        hm.put(column_names[j-1],ob.toString());
                    }
                }
                l.add(hm);
            }
            return (new Gson().toJson(l));
        }
        catch(Exception ex)
        {
            return ex.getLocalizedMessage();
        }
        finally
        {
            System.gc();
        }   
    }
    
    public ArrayList<HashMap<String,String>> getasList(String query)
    {
        HashMap<String,String> hm;
        ArrayList<HashMap<String,String>> l=new ArrayList<>();
      try
        {
            ResultSet rs=(con.createStatement()).executeQuery(query);
            ResultSetMetaData rm=rs.getMetaData();
            
            String column_names[]=new String[rm.getColumnCount()];
            
            
            for(int i=1;i<=rm.getColumnCount();i++)
            {
                column_names[i-1]=rm.getColumnName(i);
            }
            
            int rowcount=0;
            
            while(rs.next())
            {
                rowcount++;
            }
            
            rs=(con.createStatement()).executeQuery(query);
            
            for(int i=1;i<=rowcount;i++)
            {
                rs.next();
                
                hm=new HashMap<>();
                for(int j=1;j<=column_names.length;j++)
                {
                    Object ob=rs.getObject(column_names[j-1]);
                    if(ob!=null)
                    {
                        hm.put(column_names[j-1],ob.toString());
                    }
                }
                l.add(hm);
            }
            closeConnection();
            return l;
        }
        catch(Exception ex)
        {
            closeConnection();
            return null;
        }
        finally
        {
            closeConnection();
        }   
    }
    
    public ArrayList<HashMap<String,String>> getasList2(String query)
    {
        HashMap<String,String> hm;
        ArrayList<HashMap<String,String>> l=new ArrayList<>();
      try
        {
            ResultSet rs=(con.createStatement()).executeQuery(query);
            ResultSetMetaData rm=rs.getMetaData();
            
            String column_names[]=new String[rm.getColumnCount()];
            
            
            for(int i=1;i<=rm.getColumnCount();i++)
            {
                column_names[i-1]=rm.getColumnName(i);
            }
            
            int rowcount=0;
            
            while(rs.next())
            {
                rowcount++;
            }
            
            rs=(con.createStatement()).executeQuery(query);
            
            for(int i=1;i<=rowcount;i++)
            {
                rs.next();
                
                hm=new HashMap<>();
                for(int j=1;j<=column_names.length;j++)
                {
                    Object ob=rs.getObject(column_names[j-1]);
                    if(ob!=null)
                    {
                        hm.put(column_names[j-1],ob.toString());
                    }
                }
                l.add(hm);
            }
            return l;
        }
        catch(Exception ex)
        {
            return null;
        }

    }
    
    public ArrayList<HashMap<String,String>> getasList2(PreparedStatement ps)
    {
        HashMap<String,String> hm;
        ArrayList<HashMap<String,String>> l=new ArrayList<>();
      try
        {
            ResultSet rs=ps.executeQuery();
            ResultSetMetaData rm=rs.getMetaData();
            
            String column_names[]=new String[rm.getColumnCount()];
            
            
            for(int i=1;i<=rm.getColumnCount();i++)
            {
                column_names[i-1]=rm.getColumnName(i);
            }
            
            int rowcount=0;
            
            while(rs.next())
            {
                rowcount++;
            }
            
            rs=ps.executeQuery();
            
            for(int i=1;i<=rowcount;i++)
            {
                rs.next();
                
                hm=new HashMap<>();
                for(int j=1;j<=column_names.length;j++)
                {
                    Object ob=rs.getObject(column_names[j-1]);
                    if(ob!=null)
                    {
                        hm.put(column_names[j-1],ob.toString());
                    }
                }
                l.add(hm);
            }
            ps.close();
            return l;
        }
        catch(Exception ex)
        {
            return null;
        }

    }
    
    public void closeConnection()
    {
        try
        {
            if (con!=null) {
                con.close();
            }
        }
        catch(Exception ex) {
            
        }
    }

    @Override
    protected void finalize() throws Throwable {
        closeConnection();
    }

    public Connection getCon() {
        return con;
    }
    
}
