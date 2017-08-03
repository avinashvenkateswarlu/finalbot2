/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hcl.testing.nj.intents;

import com.hcl.ai_bot.common.IntentProcessor;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.http.HttpSession;

public class Gameslist implements IntentProcessor{

    @Override
    public Object GetInstance() {
        return new Gameslist();
    }

    @Override
    public String process(HttpSession ses, HashMap<String, Object> request) {
        ArrayList<String> games=new ArrayList<>();
        games.add("cash 5");
        games.add("pick 3");
        games.add("pick 4");
        games.add("pick 6");
        games.add("Power Ball");
        games.add("Mega Millions");
        games.add("Cash 4 Life");
        games.add("Five Card Cash");
        games.add("KENO");
        
        
        String result="Currently available Games:<br/>";
        for(int i=0;i<games.size();i++)
            result+=(i+1)+". "+games.get(i)+"<br/>";
        return result;
    }

}
