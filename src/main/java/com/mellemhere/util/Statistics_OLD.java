/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.util;

import com.mellemhere.main.Controller;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;

/**
 *
 * @author MellemHere
 */
public class Statistics_OLD {

    Controller con;

    JSONObject statistics = new JSONObject();
    JSONObject entry = new JSONObject();
    private final String statFile = "stat";

    private final String area = "Statistics";

    public Statistics_OLD(Controller con) {
        this.con = con;
        this.load();
    }

    public void setLastScan(String uid) {
        this.statistics.put("lastScan", uid);
       /* if (!con.getDatabase().getPeople().containsKey(uid)) {
            this.statistics.put("lastUnregisteredScan", uid);
        }*/
        //con.webSocket.sendLastScan(uid);
    }

    public void setLastEntry(String uid) {
        this.statistics.put("lastEntry", uid);
        //this.statistics.put("lastEntryName", con.getDatabase().getPeople().get(uid).getName());
        this.statistics.put("lastEntryTime", Instant.now().getEpochSecond());
        //this.con.webSocket.updateLastEntry();
        this.save();
    }

    public String getLastEntryName(){
        if(this.statistics.has("lastEntryName")){
            return this.statistics.getString("lastEntryName");
        }else{
            return "Sem data";
        }
    }
    
    
    public String getLastEntryUID(){
        if(this.statistics.has("lastEntry")){
            return String.valueOf(this.statistics.get("lastEntry"));
        }else{
            return "Sem data";
        }
    }
    
    
    public void doorOpened() {
        if (this.statistics.has("timesOpened")) {
            this.statistics.put("timesOpened", Integer.valueOf((String) statistics.get("timesOpened").toString()) + 1);
        } else {
            this.statistics.put("timesOpened", 1);
        }
       // this.con.webSocket.updateDoorOpened();
        this.save();
    }
    
    
    

    public void save() {
       /* try {
            FileOutputStream fos = new FileOutputStream(this.con.getDatabase().databaseLocation + "\\" + statFile);
            fos.write(statistics.toString().getBytes("ISO-8859-1"));
            fos.close();
        } catch (Exception e) {
            con.log(area, "Error in saving file! File: " + this.con.getDatabase().databaseLocation + "\\" + statFile, e);
        }*/
    }

    public void load() {
      /*  con.log(area, "Carregando estatisticas", null);
        File file = new File(this.con.getDatabase().databaseLocation + "\\" + statFile);
        try {
            byte[] fileData = new byte[(int) file.length()];
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            dis.readFully(fileData);
            dis.close();
            if (fileData.length == 0) {
                con.log(area, "Nenhuma estatistica encontrada...", null);
                return;
            }
            this.statistics = new JSONObject(new String(fileData));
        } catch (Exception ex) {
            con.log(area, "Error on readfile", ex);
        }*/
    }

    public String get(String name) {
        if (this.statistics.has(name)) {
            return String.valueOf(this.statistics.get(name));
        }else{
            return ""; 
        }
    }

    /*public String getRunningTime() {
        //long millis = System.currentTimeMillis() - con.startTime;

        if (statistics.has("uptime")) {
            long update = statistics.getLong("uptime");
            if (millis > update) {
                statistics.put("uptime", update);
            }
        } else {
            statistics.put("uptime", millis);
        }

        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }*/

    public String getToText() {
        return statistics.toString();
    }
    
    /*
        LIGHTS SAVING
    */
    
    
    public void savingLight(){
        
        
        
    }

    public void setLightStats(JSONObject lightData) {
        statistics.put("lightDataStats", lightData);
        this.save();
    }
    
    public JSONObject getLightStatsData(){
        if(this.statistics.has("lightDataStats")){
            return this.statistics.getJSONObject("lightDataStats");
        }else{
            return new JSONObject();
        }
    }
    
}
