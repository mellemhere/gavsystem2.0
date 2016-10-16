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
public class LightObject {
    
    
    String lightID;
    String lightState;

    public LightObject() {
    }

    
    public LightObject(String lightID, String lightState) {
        super();
        this.lightID = lightID;
        this.lightState = lightState;
    }

    public String getLightID() {
        return lightID;
    }

    public void setLightID(String lightID) {
        this.lightID = lightID;
    }

    public String getLightState() {
        return lightState;
    }

    public void setLightState(String lightState) {
        this.lightState = lightState;
    }
    
}
