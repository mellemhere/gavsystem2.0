/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.prop.lights;

import com.mellemhere.server.websocket.mObjects.LightObject;
import com.mellemhere.servers.connection.Room;
import com.mellemhere.servers.connection.ConnectionStatus;
import com.mellemhere.servers.websocket.WebSocketController;
import java.util.HashMap;
import org.eclipse.jetty.websocket.api.Session;

/**
 *
 * @author MellemHere
 */
public class LightControl {

    private final HashMap<String, LightState> lights = new HashMap<>();

    private final String LIGHT_CMD = "l";

    private final Room con;
    private final WebSocketController wcon;

    public LightControl(Room con) {
        this.con = con;
        this.wcon = con.getCcon().getCon().getWebSocketController();
    }

    public void toogleLight(String ID, LightState state) {
        if (this.lights.containsKey(ID)) {
            if (this.lights.get(ID) != state) {
                this.lights.put(ID, state);
            }
        } else {
            this.lights.put(ID, LightState.ON);
        }

        this.broadcast(ID);
    }

    public void toogleLight(String ID) {
        if (this.lights.containsKey(ID)) {
            if (isON(ID)) {
                this.lights.put(ID, LightState.OFF);
            } else {
                this.lights.put(ID, LightState.ON);
            }
        } else {
            this.lights.put(ID, LightState.ON);
        }

        this.broadcast(ID);
    }

    public boolean isON(String ID) {
        if (lights.containsKey(ID)) {
            return this.lights.get(ID) == LightState.ON;
        }
        return false;
    }

    public boolean isOFF(String ID) {
        if (lights.containsKey(ID)) {
            return this.lights.get(ID) == LightState.OFF;
        }
        return true;
    }

    public void broadcast(String lightID) {
        /*
        
         UPDATE ALL WEBCLIENTS AND CONTROLER
        
         */
        if (this.lights.get(lightID) == LightState.ON) {
            this.wcon.broadcastToRoomID(con.getRoom().getDoorID(), "light_changed", new LightObject(lightID, "on"));

            if (this.con.getStatus() == ConnectionStatus.CONNECTED) {
                //Comand = l<id><state> Ex. l141 <- Light number 14 is on
                this.con.getConnection().sendMessage(LIGHT_CMD + lightID + "1");
            }

        } else {
            this.wcon.broadcastToRoomID(con.getRoom().getDoorID(), "light_changed", new LightObject(lightID, "off"));
            if (this.con.getStatus() == ConnectionStatus.CONNECTED) {
                //Comand = l<id><state> Ex. l140  <- Light number 14 is off
                this.con.getConnection().sendMessage(LIGHT_CMD + lightID + "0");
            }
        }
    }

    public HashMap<String, LightState> getLights() {
        return lights;
    }

    public void broadcastAllToUser(Session client) {
        this.lights.forEach((lightID, state) -> {
            if (state == LightState.ON) {
                this.wcon.sendMessage(client,  "light_changed", new LightObject(lightID, "on"));
            } else {
                this.wcon.sendMessage(client, "light_changed", new LightObject(lightID, "off"));
            }
        });
    }

}
