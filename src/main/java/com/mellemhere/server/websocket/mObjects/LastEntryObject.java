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
public class LastEntryObject {

    String name;
    String uid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public LastEntryObject(String name, String uid) {
        this.name = name;
        this.uid = uid;
    }
}
