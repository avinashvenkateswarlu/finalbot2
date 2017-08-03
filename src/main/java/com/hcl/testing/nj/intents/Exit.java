/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hcl.testing.nj.intents;

import com.hcl.ai_bot.common.IntentProcessor;
import java.util.HashMap;
import javax.servlet.http.HttpSession;

/**
 *
 * @author root
 */
public class Exit implements IntentProcessor{

    @Override
    public String process(HttpSession ses, HashMap<String, Object> request) {
        
        if(((HashMap<String,HashMap<String,String>>)ses.getAttribute("contexts")).containsKey("login"))
        {
            String output="Welcome to LMS Bot. <br/> Help -> avilable options.<br/>Logout -> logout";
            return output;
        }
        else
        {
            return" Welcome to LMS Bot. <br/>Here you can manage your leaves.<hr/> Please type Login.";
        }
        
    }

    @Override
    public Object GetInstance() {
        return new Exit();
    }
    
}
