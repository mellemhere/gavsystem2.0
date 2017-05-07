/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.main;

import com.google.gson.Gson;
import com.mellemhere.mysql.MySQLController;
import com.mellemhere.patrimony.PatrimonyController;
import com.mellemhere.servers.http.HTTPController;
import com.mellemhere.servers.connection.ConnectionController;
import com.mellemhere.servers.websocket.WebSocketController;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

public class Controller {

    /*
    
     SYSGAV
    
     */
    public boolean debug = true;

    private MySQLController mysqlController;

    // private DataBase database;
    private HTTPController httpController;
    private WebSocketController webSocketController;

    private ConnectionController connectionController;

    private String LogBuffer = "";
    
    private PatrimonyController patrimonyController;
    
    
    //private Statistics satistics;
    // private EntryLog entryLog;
    public long SYSTEM_STARTUP_TIME;


    public Controller() {
        this.start();
        this.startServers();
    }

    public void start() {
        this.SYSTEM_STARTUP_TIME = System.currentTimeMillis();
        this.mysqlController = new MySQLController(this);
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

    public PatrimonyController getPatrimonyController() {
        return patrimonyController;
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
        
        this.patrimonyController = new PatrimonyController(this);
        
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

    public long getUptime() {
        return System.currentTimeMillis() - SYSTEM_STARTUP_TIME;
    }

    public String formatTime(long timeInMS) {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(timeInMS),
                TimeUnit.MILLISECONDS.toMinutes(timeInMS)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeInMS)),
                TimeUnit.MILLISECONDS.toSeconds(timeInMS)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMS)));
    }

  
}
