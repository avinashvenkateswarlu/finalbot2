package com.hcl.testing.nj.intents;

import com.hcl.ai_bot.common.CommonFuns;
import com.hcl.ai_bot.common.IntentProcessor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author root
 */
public class WinningNumbers implements IntentProcessor{

    @Override
    public Object GetInstance() {
        return new WinningNumbers();
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
            String $cash="";
            JSONObject res=new JSONObject(ob2.get(200).toString());
            ((ServletContext)ses.getAttribute("appcontext")).log("\n"+res.toString(8));
            if(res.has("draws"))
            {
                JSONArray draws=res.getJSONArray("draws");
                for(int i=0;i<draws.length();i++)
                {
                    if(draws.getJSONObject(i).has("estimatedJackpot") && draws.getJSONObject(i).getLong("estimatedJackpot")>0)
                    {
                        $cash="Estimated Jack pot: <b>$ "+(draws.getJSONObject(i).getLong("estimatedJackpot"))+"</b>"; 
                        $cash+="<br/>Cash Options: <b>$ "+(draws.getJSONObject(i).getLong("annuityCashOption"))+"</b>";
                    }
                    if(request.get("gamename").toString().equalsIgnoreCase("Keno"))
                    {
                        if(draws.getJSONObject(i).getString("gameName").equalsIgnoreCase(request.get("gamename").toString()) && draws.getJSONObject(i).getString("status").equalsIgnoreCase("closed"))
                        {
                            Date d=new Date(Long.parseLong(draws.getJSONObject(i).get("drawTime").toString()));
                            SimpleDateFormat df=new SimpleDateFormat("d-M-Y E");
                            
                            JSONArray jr=draws.getJSONObject(i).getJSONArray("results");
                            for(int j=0;j<jr.length();j++) {
                                if(jr.getJSONObject(j).has("prizeTierId"))
                                {
                                   if(jr.getJSONObject(j).getString("prizeTierId").equalsIgnoreCase("Regular"))
                                    {
                                        String result = "Below are the winning numbers of "+request.get("gamename").toString()+" on "+df.format(d);
                                        result+="<br/>"+(jr.getJSONObject(j).getJSONArray("primary").toString()).replace("\"", "").replace(",", ", ").replace("[", "").replace("]", "");
                                        return result+"<br/>"+$cash;
                                    }
                                }
                            }
                        }
                    }
                    else if(request.get("gamename").toString().equalsIgnoreCase("pick 6") ||request.get("gamename").toString().equalsIgnoreCase("cash 5") || request.get("gamename").toString().equalsIgnoreCase("pick 6") || request.get("gamename").toString().equalsIgnoreCase("powerball") || request.get("gamename").toString().equalsIgnoreCase("Cash 4 Life") || request.get("gamename").toString().equalsIgnoreCase("Mega Millions") || request.get("gamename").toString().equalsIgnoreCase("Cash 5"))
                    {
                        if(draws.getJSONObject(i).getString("gameName").equalsIgnoreCase(request.get("gamename").toString()) && draws.getJSONObject(i).getString("status").equalsIgnoreCase("closed"))
                        {
                            Date d=new Date(Long.parseLong(draws.getJSONObject(i).get("drawTime").toString()));
                            SimpleDateFormat df=new SimpleDateFormat("d-M-Y E");
                            
                            JSONArray jr=draws.getJSONObject(i).getJSONArray("results");
                            for(int j=0;j<jr.length();j++) {
                                if(jr.getJSONObject(j).has("drawType"))
                                {
                                   if(jr.getJSONObject(j).getString("drawType").equalsIgnoreCase("Regular"))
                                    {
                                        String result = "Below are the winning numbers of "+request.get("gamename").toString()+" on "+df.format(d);
                                        result+="<br/>";
                                        ArrayList<Object> li=(ArrayList<Object>)jr.getJSONObject(j).getJSONArray("primary").toList();
                                        
                                        if(request.get("gamename").toString().equalsIgnoreCase("cash 5") || request.get("gamename").toString().equalsIgnoreCase("pick 6"))
                                        {
                                            result+=li.toString().replace("[", "").replace("]", "").trim();
                                            Pattern p=Pattern.compile("M-\\d+");
                                            Matcher m=p.matcher(result);
                                            while(m.find())
                                            {
                                                String temp=m.group();
                                                temp=temp.replace("M-", "")+ " - Xtra";
                                                result=result.replace(m.group(), temp);
                                            }
                                        }
                                        else
                                        {
                                            result+=li.toString().replaceAll("M-\\d+(,)?", "").replace("[", "").replace("]", "").trim();
                                        }
                                        
                                        result= result.lastIndexOf(",")==result.length()-1? result.substring(0, result.length()-1) : result;
                                        return result+"<br/>"+$cash;
                                    }
                                }
                            }
                        }
                    }
                    else if(request.get("gamename").toString().equalsIgnoreCase("Pick 3") || request.get("gamename").toString().equalsIgnoreCase("Pick 4"))
                    {
                        String result="";
                        String fireball="";
                        
                        Date d=new Date(Long.parseLong(draws.getJSONObject(i).get("drawTime").toString()));
                        SimpleDateFormat df=new SimpleDateFormat("d-M-Y E");
                        
                        if(draws.getJSONObject(i).getString("gameName").equalsIgnoreCase(request.get("gamename").toString()) && draws.getJSONObject(i).getString("status").equalsIgnoreCase("closed"))
                        {
                            
                            
                            JSONArray jr=draws.getJSONObject(i).getJSONArray("results");
                            for(int j=0;j<jr.length();j++) {
                                if(jr.getJSONObject(j).has("drawType"))
                                {
                                   if(jr.getJSONObject(j).getString("drawType").equalsIgnoreCase("Regular"))
                                    {
                                        ArrayList<Object> li=(ArrayList<Object>)jr.getJSONObject(j).getJSONArray("primary").toList();
                                        String s="";
                                        char c[]=li.toString().replace("[", "").replace("]", "").toCharArray();
                                        for(int z=0;z<c.length;z++)
                                        {
                                            s+=c[z]+" ";
                                        }
                                        result+=s;
                                    }
                                   
                                   if(jr.getJSONObject(j).getString("drawType").equalsIgnoreCase("FIREBALL"))
                                    {
                                        ArrayList<Object> li=(ArrayList<Object>)jr.getJSONObject(j).getJSONArray("primary").toList();
                                        Object[] o=li.toArray();
                                        Arrays.sort(o);
                                        fireball=o[o.length-1].toString().replace("FB-", "Fire Ball is ");
                                    }
                                }
                            }
                        }
                        else
                        {
                            continue;
                        }
                        return "Below are the winning numbers of "+request.get("gamename").toString()+" on "+df.format(d)+"<br/>"+result+", "+fireball+"<br/>"+$cash;
                    }
                    else if(request.get("gamename").toString().equalsIgnoreCase("five card cash"))
                    {
                        if(draws.getJSONObject(i).getString("gameName").equalsIgnoreCase(request.get("gamename").toString()) && draws.getJSONObject(i).getString("status").equalsIgnoreCase("closed"))
                        {
                            Date d=new Date(Long.parseLong(draws.getJSONObject(i).get("drawTime").toString()));
                            SimpleDateFormat df=new SimpleDateFormat("d-M-Y E");
                            
                            JSONArray jr=draws.getJSONObject(i).getJSONArray("results");
                            for(int j=0;j<jr.length();j++) {
                                if(jr.getJSONObject(j).has("drawType") && jr.getJSONObject(j).getString("drawType").equalsIgnoreCase("Win Tonight"))
                                {
                                    String result = "Below are the winning numbers of "+request.get("gamename").toString()+" on "+df.format(d)+"<br/>";
                                    String mappings[]={"2H", "3H", "4H", "5H",
                                                        "6H", "7H", "8H", "9H",
                                                        "10H", "JH", "QH", "KH",
                                                        "AH",
                                                        "2D", "3D", "4D", "5D",
                                                        "6D", "7D", "8D", "9D",
                                                        "10D", "JD", "QD", "KD",
                                                        "AD",
                                                        "2S", "3S", "4S", "5S",
                                                        "6S", "7S", "8S", "9S",
                                                        "10S", "JS", "QS", "KS",
                                                        "AS",
                                                        "2C", "3C", "4C", "5C",
                                                        "6C", "7C", "8C", "9C",
                                                        "10C", "JC", "QC", "KC",
                                                        "AC"
                                                        };
                                    List<Object> cards=jr.getJSONObject(j).getJSONArray("primary").toList();
                                    for(Object o : cards)
                                    {
                                        result=result+" "+mappings[Integer.parseInt(o.toString())-1];
                                    }
                                    return result;
                                }
                            }
                        }
                    }
                }
                return "found draws";
            }
            else
            {
                return "draws not found";
            }
        }
        else
        {
            return ob2.toString();
        }
    }
    
}





