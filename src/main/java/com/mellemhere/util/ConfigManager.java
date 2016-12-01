/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.util;

import org.json.JSONObject;


/**
 *
 * @author MellemHere
 */
public class ConfigManager {
    
    
    static Object getValue(String config, String key){
        JSONObject json = new JSONObject(config);
        if(json.has(key)){
            return json.get(key);
        }else{
            return null;
        }
    }
    
    static JSONObject get(String config){
        return new JSONObject(config);
    }
    
    static String getString(JSONObject ob){
        return ob.toString();
    }
    
}
