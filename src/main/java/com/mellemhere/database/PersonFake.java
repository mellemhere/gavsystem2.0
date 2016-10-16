/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.database;

import org.json.JSONObject;

/**
 *
 * @author aluno
 */
public class PersonFake {

    public static JSONObject getFake() {
        JSONObject info = new JSONObject();
        JSONObject info2 = new JSONObject();
        info2.put("UID", "696969");
        info2.put("ID", "hell2o");
        info2.put("POSITION", "hell3o");
        info2.put("NAME", "hell4o");
        
        info.put("696969", info2);
        return info;
    }

}
