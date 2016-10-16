/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.database;

import com.mellemhere.main.Controller;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import org.json.JSONObject;

/**
 *
 * @author aluno
 */
public class DataBase {

    Controller con;
    /*
     Reads and makes a cache of all the people in
     one array. It makes the process faster but
     is bad for memory and stuff :(
     */
    private final boolean cacheData = true;
    //Map that stores all the people <ID OF THE CARD, Person>
    public HashMap<String, Person> people = new HashMap<>();

    //DataBase Location
    public final String databaseLocation = "‪‪";
    private final String peopleFile = "datappl";

    public final String area = "DATABASE";

    public void savePerson(Person person) {
        this.con.log(area, "Saving new person - Name: " + person.getName() + " -  UID: " + person.getCardID(), null);
        this.people.put(person.getCardID(), person);
        this.save();
    }

    public DataBase(Controller controller) {
        this.con = controller;
        check();
        if (cacheData) {
            this.con.log(area, "Starting to cache", null);
            cache();
        }
    }

    private String readFromDataBase() {
        /*
         SHITTY WAY OF DOING THIS
         FIX LATER
         :) 
         */
        File file = new File(this.databaseLocation + "\\" + this.peopleFile);
        if (!file.exists()) {
            return new JSONObject().toString();
        }
        try {
            byte[] fileData = new byte[(int) file.length()];
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            dis.readFully(fileData);
            dis.close();
            if (fileData.length == 0) {
                return new JSONObject().toString();
            }
            return new String(fileData);
        } catch (Exception ex) {
            System.out.println("Error on readfile " + ex.getMessage());
        }
        return new JSONObject().toString();
    }

    public void save() {
        JSONObject finalData;

        if (this.cacheData) {
            finalData = new JSONObject(readFromDataBase());
            this.people.forEach((name, person) -> {
                finalData.put(person.getCardID(), person.getInfo());
            });
        } else {
            finalData = new JSONObject(readFromDataBase());
        }

        /*
         CREATE JSON FILE
         */
        try {
            FileOutputStream fos = new FileOutputStream(this.databaseLocation + "\\" + peopleFile);
            fos.write(finalData.toString().getBytes("ISO-8859-1"));
            fos.close();
        } catch (Exception e) {
            System.out.println("Error in saving file! File: " + this.databaseLocation + "\\" + peopleFile + " - " + e.getMessage());
        }
    }

    private void cache() {
        this.people = loadPeopleIntoArray();
    }

    private void check() {
        File folder = new File(this.databaseLocation);

        if (!folder.exists()) {
            folder.mkdir();
        }

        File data = new File(this.databaseLocation + "\\" + this.peopleFile);
        if (!data.exists()) {
            try {
                data.createNewFile();
            } catch (IOException ex) {
                this.con.log(area, "Could not create data file " + data.getAbsolutePath(), ex);
            }
        }

        data = new File(this.databaseLocation + "\\" + "stat");
        if (!data.exists()) {
            try {
                data.createNewFile();
            } catch (IOException ex) {
                this.con.log(area, "Could not create data file " + data.getAbsolutePath(), ex);
            }
        }

        data = new File(this.databaseLocation + "\\" + "entrylog");
        if (!data.exists()) {
            try {
                data.createNewFile();
            } catch (IOException ex) {
                this.con.log(area, "Could not create data file " + data.getAbsolutePath(), ex);
            }
        }
    }

    private HashMap<String, Person> loadPeopleIntoArray() {
        HashMap<String, Person> returner = new HashMap<>();
        try {
            JSONObject data = new JSONObject(readFromDataBase());
            for (Object key : data.keySet()) {
                //based on you key types
                String keyStr = (String) key;
                Object keyvalue = data.get(keyStr);
                returner.put(keyStr, new Person((JSONObject) keyvalue));
            }
        } catch (Exception e) {
            System.out.println("com.mellemhere.database.DataBase.cache() - could not read " + e.getMessage());
            e.printStackTrace();
        }
        return returner;
    }

    public HashMap<String, Person> getPeople() {
        if (cacheData) {
            return this.people;
        } else {
            return loadPeopleIntoArray();
        }
    }

    public void reloadAndAdd(Person person) {
        if (cacheData) {
            this.people.put(person.getCardID(), person);
        }
    }


    public boolean hasUID(String UID) {
        if (cacheData) {
            return people.containsKey(UID);
        } else {
            JSONObject database = new JSONObject(readFromDataBase());
            return database.has(UID);
        }
    }

    public void clean() {
        this.cache();
        File data = new File(this.databaseLocation + "\\" + this.peopleFile);
        if (data.exists()) {
            try {
                data.delete();
            } catch (Exception ex) {
                this.con.log(area, "Could not delete data file " + data.getAbsolutePath(), ex);
            }
            try {
                data.createNewFile();
            } catch (IOException ex) {
                this.con.log(area, "Could not create data file " + data.getAbsolutePath(), ex);
            }
        }

    }

    public void deletePerson(String uid) {
        this.clean();
        this.people.remove(uid);

        JSONObject finalData = new JSONObject();

        this.people.forEach((name, person) -> {
            finalData.put(person.getCardID(), person.getInfo());
        });

        /*
         CREATE JSON FILE AGAIN
         */
        try {
            FileOutputStream fos = new FileOutputStream(this.databaseLocation + "\\" + peopleFile);
            fos.write(finalData.toString().getBytes("ISO-8859-1"));
            fos.close();
        } catch (Exception e) {
            System.out.println("Error in saving file! For delete - File: " + this.databaseLocation + "\\" + peopleFile + " - " + e.getMessage());
        }
    }

}
