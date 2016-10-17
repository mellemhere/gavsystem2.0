/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.main;

import com.mellemhere.util.Festa;
import com.mellemhere.util.LightCon_OLD;
import com.google.gson.Gson;
import com.mellemhere.mysql.MySQLController;
import com.mellemhere.prop.lights.LightState_OLD;
import com.mellemhere.server.websocket.mObjects.LightObject;
import com.mellemhere.servers.http.HTTPController;
import com.mellemhere.servers.connection.ConnectionController;
import com.mellemhere.servers.websocket.WebSocketController;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

public class Controller {

    private MySQLController mysqlController;

   // private DataBase database;

    private HTTPController httpController;
    private WebSocketController webSocketController;
    
    private ConnectionController connectionController;
    
    private String LogBuffer = "";
    //private Statistics satistics;
   // private EntryLog entryLog;
    public long startTime;

    private LightState_OLD light;

    public LightCon_OLD lightcon;

    public Controller() {
        this.start();
        this.startServers();
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
        this.mysqlController = new MySQLController(this);
        //this.database = new DataBase(this);
        //this.entryLog = new EntryLog(this);
       // this.satistics = new Statistics(this);
    }

   /* public DataBase getDatabase() {
        return database;
    }*/


    public LightState_OLD getLight() {
        return light;
    }

    public MySQLController getMysqlController() {
        return mysqlController;
    }

    public HTTPController getHttpController() {
        return httpController;
    }

    public WebSocketController getWebSocketController() {
        return webSocketController;
    }

    public ConnectionController getConnectionController() {
        return connectionController;
    }

    
    
    private void startServers() {
        //WEBSOCKET SERVER
        this.webSocketController = new WebSocketController(this);
        this.webSocketController.start();
        //HTTP SERVER
        this.httpController = new HTTPController(this, this.webSocketController);
        this.httpController.open();
        
        //STARTS ALL THE CONNECTIONS
        this.connectionController = new ConnectionController(this);
        this.connectionController.openAll();
        
    }

    public LightCon_OLD getLightController() {
        return lightcon;
    }

    /*
     SHITTY WAY OF DOING THIS BUT IT WORKS :)
     */
    public void log(String area, String message, Exception x) {
        if (x == null) {
            this.LogBuffer += "\n[" + area.toUpperCase() + "] " + message;
            System.out.println("[" + area.toUpperCase() + "] " + message);
            /*if (this.webSocket != null) {
             this.webSocket.broadcast("log", new LogObject("[" + area.toUpperCase() + "] " + message));
             }*/
        } else {
            this.LogBuffer += "\n[" + area.toUpperCase() + "] " + message;
            System.out.println("[" + area.toUpperCase() + "] " + message + " - " + x.getMessage());
            /*if (this.webSocket != null) {
             this.webSocket.broadcast("log", new LogObject("[" + area.toUpperCase() + "] " + message + " - " + x.getMessage()));
             }*/
        }

    }

    /*public Statistics getStatistics() {
        return this.satistics;
    }*/

    public String getLogBuffer() {
        if (this.LogBuffer.length() > 10000) {
            this.LogBuffer = "";
        }
        return this.LogBuffer;
    }



    /*public EntryLog getEntryLog() {
        return entryLog;
    }*/

    public void restart() {
       //TODO
    }

    public void stop() {
        System.exit(0);
    }

    public void changeLight(LightObject data) {
        //TODO
    }

    Festa festa;

    public void festa(int speed) {
        if (this.festa == null) {
            this.festa = new Festa(this);
            this.festa.speed = speed;
            this.festa.start();
        } else {
            this.festa.setRunning(false);
            this.festa = null;
        }
    }

    public JSONObject toJSON(Object element) {
        Gson g = new Gson();
        return new JSONObject(g.toJson(element));
    }

    public Map<String, Object> objectToMap(Object obj) {
        Method[] methods = obj.getClass().getMethods();

        Map<String, Object> map = new HashMap<>();
        for (Method m : methods) {
            if (m.getName().startsWith("get") && !m.getName().startsWith("getClass")) {
                try {
                    Object value = (Object) m.invoke(obj);
                    map.put(m.getName().substring(3).toLowerCase(), (Object) value);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return map;
    }

    public Map<String, Object> addObjectAsChildMap(Map finalmap, String sector, Object obj) {

        Map<String, Object> map = new HashMap<>();

        if (obj instanceof Object[]) {
            HashSet<Map<String, Object>> maps = new HashSet<>();
            Object[] list = (Object[]) obj;

            for (Object list1 : list) {
                maps.add(objectToMap(list1));
            }

            map.put(sector, maps);

            finalmap.putAll(map);
        } else {
            Method[] methods = obj.getClass().getMethods();

            for (Method m : methods) {
                if (m.getName().startsWith("get") && !m.getName().startsWith("getClass")) {
                    try {
                        Object value = (Object) m.invoke(obj);
                        map.put(m.getName().substring(3).toLowerCase(), (Object) value);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            finalmap.put(sector, map);
        }

        return finalmap;
    }

    public JSONObject mergeJSON(JSONObject one, JSONObject second) {
        for (String key : JSONObject.getNames(second)) {
            one.put(key, second.get(key));
        }
        return one;
    }

}
