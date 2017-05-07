/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.prop.items;


/**
 *
 * @author MellemHere
 */
public class Item {

    private int id;
    private String name;
    private String QRtags; //Base 64 Image
    private String patrimony;
    private int stateID;
    private String comment;
    private int roomID;

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

    public String getQRTags() {
        return QRtags;
    }

    public void setQRTags(String barCode) {
        this.QRtags = barCode;
    }

    public String getPatrimony() {
        return patrimony;
    }

    public void setPatrimony(String patrimony) {
        this.patrimony = patrimony;
    }

    public int getStateID() {
        return stateID;
    }

    public void setStateID(int stateID) {
        this.stateID = stateID;
    }

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int room) {
        this.roomID = room;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
