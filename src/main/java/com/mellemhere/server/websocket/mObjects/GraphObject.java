/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.server.websocket.mObjects;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author MellemHere
 */
public class GraphObject {

    List<Object> x = new ArrayList<>();
    List<Object> y = new ArrayList<>();

    public Object[] getX() {
        return x.toArray();
    }

    public Object[] getY() {
        return y.toArray();
    }

    public void add(Object x, Object y) {
        this.x.add(x);
        this.y.add(y);
    }

}
