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
public class EventObject {
    
    
    private String event;
    private Object message;

    public EventObject(String reason, Object message) {
        this.event = reason;
        this.message = message;
    }
    
    
    
    public String getReason() {
        return event;
    }

    public void setReason(String reason) {
        this.event = reason;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
    
    
    
    
}
