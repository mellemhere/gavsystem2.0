/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.server.websocket.mObjects;

/**
 *
 * @author MellemHere
 */
public class UptimeObject {
    
    String uptime;

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public UptimeObject(String uptime) {
        this.uptime = uptime;
    }
    
}
