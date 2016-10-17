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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.json.JSONObject;

/**
 *
 * @author MellemHere
 */
public class EntryLog_OLD {

    Controller con;

    JSONObject entry = new JSONObject();

    private final String entryFile = "entrylog";
    private final String area = "EntryLog";

    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    String today;

    public EntryLog_OLD(Controller con) {
        this.today = Instant.ofEpochSecond(Instant.now().getEpochSecond()).atZone(ZoneId.of("Brazil/East")).format(formatter);
        this.con = con;
        this.load();
    }

    public void newEntry(String uid) {
        this.today = Instant.ofEpochSecond(Instant.now().getEpochSecond()).atZone(ZoneId.of("Brazil/East")).format(formatter);
        if (entry.has(today)) {
            JSONObject todayEntry = new JSONObject(entry.getString(today));
            todayEntry.put(String.valueOf(Instant.now().getEpochSecond()), uid);
            entry.put(today, todayEntry.toString());
        } else {
            JSONObject todayEntry = new JSONObject();
            todayEntry.put(String.valueOf(Instant.now().getEpochSecond()), uid);
            entry.put(today, todayEntry.toString());
        }

        JSONObject entryStats;
        if (this.entry.has("entryTimes")) {
            entryStats = this.entry.getJSONObject("entryTimes");
        } else {
            entryStats = new JSONObject();
        }

        JSONObject times;
        if (!entryStats.has(uid)) {
            times = new JSONObject();
            times.put("times", 1);
        } else {
            times = entryStats.getJSONObject(uid);
            times.put("times", (times.getInt("times") + 1));
        }
        entryStats.put(uid, times);
        this.entry.put("entryTimes", entryStats);
        this.save();
    }

    public void save() {
       /* try {
            FileOutputStream fos = new FileOutputStream(this.con.getDatabase().databaseLocation + "\\" + entryFile);
            fos.write(entry.toString().getBytes("ISO-8859-1"));
            fos.close();
        } catch (Exception e) {
            con.log(area, "Error in saving file! File: " + this.con.getDatabase().databaseLocation + "\\" + entryFile, e);
        }*/
    }

    public void load() {
        /*con.log(area, "Carregando estatisticas", null);
        File file = new File(this.con.getDatabase().databaseLocation + "\\" + entryFile);
        try {
            byte[] fileData = new byte[(int) file.length()];
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            dis.readFully(fileData);
            dis.close();
            if (fileData.length == 0) {
                con.log(area, "Nenhuma estatistica encontrada...", null);
                return;
            }
            this.entry = new JSONObject(new String(fileData));
        } catch (Exception ex) {
            con.log(area, "Error on readfile", ex);
        }*/
    }

    public String getAll() {
        return entry.toString();
    }

    public JSONObject getAllJSON() {
        return entry;
    }

    public JSONObject getAllEntry() {
        return entry.getJSONObject("entryTimes");
    }

    public JSONObject getTodayJSON() {
        this.today = Instant.ofEpochSecond(Instant.now().getEpochSecond()).atZone(ZoneId.of("Brazil/East")).format(formatter);
        if (entry.has(today)) {
            return new JSONObject(entry.getString(today));
        } else {
            return new JSONObject();
        }
    }

    public String getToday() {
        this.today = Instant.ofEpochSecond(Instant.now().getEpochSecond()).atZone(ZoneId.of("Brazil/East")).format(formatter);
        JSONObject todayEntry = new JSONObject(entry.getString(today));
        return todayEntry.toString();
    }

}
