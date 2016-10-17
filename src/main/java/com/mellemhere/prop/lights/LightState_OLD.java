/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.prop.lights;

import com.mellemhere.main.Controller;
import java.util.HashMap;

/**
 *
 * @author MellemHere
 */
public class LightState_OLD {

    HashMap<String, Integer> lightStates = new HashMap<>();
    HashMap<String, Long> lightTimes = new HashMap<>();

   // SerialConnection_OLD serial;
    Controller con;

    public LightState_OLD(/*SerialConnection_OLD serial,*/ Controller con) {
      //  this.serial = serial;
        this.con = con;
    }

    public void turnLight(String id) {

        if (lightStates.containsKey(id)) {
            if (lightStates.get(id) == 1) {
                //Desliga a luz
                con.log("LUZ", "Desligando a luz " + id, null);
                lightStates.put(id, 0);
               // serial.sendMessage("l" + id + 0);
                lightTimes.remove(id);

                //con.webSocket.broadcast("light", serial);
                //con.webSocket.broadcast("lightChange", new LightObject(id, "0"));
            } else {
                //Liga a luz
                con.log("LUZ", "Ligando a luz " + id, null);
                lightStates.put(id, 1);

                if (!lightTimes.containsKey(id)) {
                    lightTimes.put(id, System.currentTimeMillis());
                }
               // serial.sendMessage("l" + id + 1);
               // con.webSocket.broadcast("lightChange", new LightObject(id, "1"));
            }
        } else {
            lightStates.put(id, 1);

            if (!lightTimes.containsKey(id)) {
                lightTimes.put(id, System.currentTimeMillis());
            }

           // serial.sendMessage("l" + id + 1);
           // con.webSocket.broadcast("lightChange", new LightObject(id, "1"));
        }
    }

    public HashMap<String, Integer> getStates() {
        return this.lightStates;
    }

    public HashMap<String, Integer> getLightStates() {
        return lightStates;
    }

    public HashMap<String, Long> getLightTimes() {
        return lightTimes;
    }

    void lightChanged(String id, int state) {
        con.log("LUZ", "Luz mudada: " + id, null);
        if (this.lightStates.containsKey(id)) {
            if (this.lightStates.get(id) != state) {
               // con.webSocket.broadcast("lightChange", new LightObject(id, String.valueOf(state)));
            }
        } else {
           // con.webSocket.broadcast("lightChange", new LightObject(id, String.valueOf(state)));
        }
        
        this.lightStates.put(id, state);
    }
}
