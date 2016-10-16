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
public class LogObject {

    String message = "\n";

    public LogObject(String message) {
        this.message += message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message += message;
    }

    
}
