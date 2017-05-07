/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.patrimony;

import com.mellemhere.main.Controller;
import org.json.JSONObject;

public class PatrimonyController {

    private final Controller con;
    
    public PatrimonyController(Controller con) {
        this.con = con;
    }
    
    public void updatePatrimony(JSONObject object){
        PatrimonyObject ob = new PatrimonyObject();    
    }
    
    public void newPatrimony(PatrimonyObject op){
        con.getMysqlController().makeUpdateQuery("gav_patrimony", op);
    }
    
    

}
