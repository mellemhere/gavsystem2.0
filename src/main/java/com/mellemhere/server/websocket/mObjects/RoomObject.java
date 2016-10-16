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
public class RoomObject {
    
    
    private int id;
    private String name;
    private String description;
    private int doorID;
    private int comID;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDoorID() {
        return doorID;
    }

    public void setDoorID(int doorID) {
        this.doorID = doorID;
    }

    public int getComID() {
        return comID;
    }

    public void setComID(int comID) {
        this.comID = comID;
    }
    
    
}
